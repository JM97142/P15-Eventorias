package com.example.p15_eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.screens.EventDetailScreen
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockEventViewModel: EventViewModel
    private var onBackClicked = false

    private val fakeEvent = Event(
        id = "1",
        title = "Concert de Jazz",
        description = "Une soirée musicale inoubliable",
        date = "2025-10-20",
        time = "20:00",
        address = "123 Rue de Paris, Lyon",
        imageUrl = "https://example.com/jazz.jpg",
        latitude = 45.75,
        longitude = 4.85,
        creatorUid = "user123"
    )

    @Before
    fun setup() {
        mockEventViewModel = mockk(relaxed = true)
        coEvery { mockEventViewModel.getUserByUid("user123") } returns "https://example.com/photo.jpg"
        onBackClicked = false
    }

    @Test
    fun eventDetail_displaysBasicInformation() {
        composeTestRule.setContent {
            EventDetailScreen(
                eventViewModel = mockEventViewModel,
                event = fakeEvent,
                onBack = { onBackClicked = true }
            )
        }

        // Vérifie le titre
        composeTestRule.onNodeWithText("Concert de Jazz").assertExists()

        // Vérifie la description
        composeTestRule.onNodeWithText("Une soirée musicale inoubliable").assertExists()

        // Vérifie la date et l'heure
        composeTestRule.onNodeWithText("2025-10-20").assertExists()
        composeTestRule.onNodeWithText("20:00").assertExists()

        // Vérifie l’adresse
        composeTestRule.onNodeWithText("123 Rue de Paris, Lyon").assertExists()

        // Vérifie la présence de l'image principale
        composeTestRule.onAllNodes(hasContentDescription("Image de l’événement Concert de Jazz"))
            .onFirst()
            .assertExists()
    }

    @Test
    fun eventDetail_backButton_callsOnBack() {
        composeTestRule.setContent {
            EventDetailScreen(
                eventViewModel = mockEventViewModel,
                event = fakeEvent,
                onBack = { onBackClicked = true }
            )
        }

        // Appuie sur le bouton retour
        composeTestRule.onNodeWithContentDescription(
            "Bouton retour. Appuyer pour revenir à la liste des événements."
        ).performClick()

        assert(onBackClicked)
    }

    @Test
    fun eventDetail_displaysCreatorPhoto() {
        composeTestRule.setContent {
            EventDetailScreen(
                eventViewModel = mockEventViewModel,
                event = fakeEvent,
                onBack = {}
            )
        }

        // Attendre que LaunchedEffect se termine
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            true
        }

        // Vérifie que la photo du créateur est affichée
        composeTestRule.onAllNodes(hasContentDescription("Photo du créateur de l’événement Concert de Jazz"))
            .onFirst()
            .assertExists()
    }

    @Test
    fun eventDetail_displaysMap_whenCoordinatesAvailable() {
        composeTestRule.setContent {
            EventDetailScreen(
                eventViewModel = mockEventViewModel,
                event = fakeEvent,
                onBack = {}
            )
        }

        // La carte doit être présente (AsyncImage avec contentDescription contenant “Map of”)
        composeTestRule.onAllNodesWithContentDescription("Map of 123 Rue de Paris, Lyon")
            .onFirst()
            .assertExists()
    }

    @Test
    fun eventDetail_doesNotDisplayMap_whenNoCoordinates() {
        val eventWithoutCoords = fakeEvent.copy(latitude = null, longitude = null)

        composeTestRule.setContent {
            EventDetailScreen(
                eventViewModel = mockEventViewModel,
                event = eventWithoutCoords,
                onBack = {}
            )
        }

        composeTestRule.onAllNodesWithContentDescription("Map of 123 Rue de Paris, Lyon")
            .assertCountEquals(0)
    }
}