package com.example.p15_eventorias

import android.app.Application
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.storage.*
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

@OptIn(ExperimentalCoroutinesApi::class)
class EventViewModelTest {

    private lateinit var viewModel: EventViewModel
    private lateinit var application: Application

    private val mockDb = mockk<FirebaseFirestore>(relaxed = true)
    private val mockStorage = mockk<FirebaseStorage>(relaxed = true)
    private val mockAuth = mockk<FirebaseAuth>(relaxed = true)
    private val mockUser = mockk<FirebaseUser>(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)

        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseStorage::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        every { FirebaseFirestore.getInstance() } returns mockDb
        every { FirebaseStorage.getInstance() } returns mockStorage
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUid"

        val mockCollection = mockk<CollectionReference>(relaxed = true)
        every { mockDb.collection("events") } returns mockCollection
        every { mockCollection.addSnapshotListener(any<EventListener<QuerySnapshot>>()) } returns mockk(relaxed = true)
        every { mockCollection.addSnapshotListener(any<Executor>(), any<EventListener<QuerySnapshot>>()) } returns mockk(relaxed = true)
        every { mockCollection.addSnapshotListener(any<MetadataChanges>(), any<EventListener<QuerySnapshot>>()) } returns mockk(relaxed = true)

        viewModel = spyk(EventViewModel(application), recordPrivateCalls = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    // Tests unitaires
    @Test
    fun `getUserByUid returns photoUrl when user exists`() = runTest {
        val mockCollection = mockk<CollectionReference>()
        val mockDoc = mockk<DocumentReference>()
        val mockSnapshot = mockk<DocumentSnapshot>()

        every { mockDb.collection("users") } returns mockCollection
        every { mockCollection.document("testUid") } returns mockDoc
        coEvery { mockDoc.get().await() } returns mockSnapshot
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.getString("photoUrl") } returns "https://image.png"

        val result = viewModel.getUserByUid("testUid")
        assertEquals("https://image.png", result)
    }

    @Test
    fun `getUserByUid returns null when user not found`() = runTest {
        val mockCollection = mockk<CollectionReference>()
        val mockDoc = mockk<DocumentReference>()
        val mockSnapshot = mockk<DocumentSnapshot>()

        every { mockDb.collection("users") } returns mockCollection
        every { mockCollection.document("404") } returns mockDoc
        coEvery { mockDoc.get().await() } returns mockSnapshot
        every { mockSnapshot.exists() } returns false

        val result = viewModel.getUserByUid("404")
        assertEquals(null, result)
    }

    @Test
    fun `fetchEvents updates state flow`() = runTest {
        val mockCollection = mockk<CollectionReference>()
        val mockSnapshot = mockk<QuerySnapshot>()
        val mockRegistration = mockk<ListenerRegistration>()

        every { mockDb.collection("events") } returns mockCollection

        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        every { mockCollection.addSnapshotListener(capture(listenerSlot)) } answers {
            listenerSlot.captured.onEvent(mockSnapshot, null)
            mockRegistration
        }

        val eventsList = listOf(Event(id = "1", title = "Expo"))
        every { mockSnapshot.toObjects(Event::class.java) } returns eventsList

        // Instanciation manuelle
        viewModel = EventViewModel(application)

        // Appel manuel de fetchEvents car le listener FirebaseAuth n’est pas déclenché en test
        val fetchEventsMethod = viewModel.javaClass.getDeclaredMethod("fetchEvents")
        fetchEventsMethod.isAccessible = true
        fetchEventsMethod.invoke(viewModel)

        advanceUntilIdle()

        val result = viewModel.events.first()
        assertEquals(1, result.size)
        assertEquals("Expo", result.first().title)
    }
}