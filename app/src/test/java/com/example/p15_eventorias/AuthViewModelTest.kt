package com.example.p15_eventorias

import com.example.p15_eventorias.repository.AuthRepository
import com.example.p15_eventorias.repository.AuthUiState
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var repo: AuthRepository
    private lateinit var user: FirebaseUser
    private lateinit var viewModel: AuthViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk(relaxed = true)
        user = mockk(relaxed = true)
        viewModel = AuthViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TESTS UNITAIRES
    @Test
    fun `createWithEmail success updates uiState to Authenticated`() = runTest {
        // Arrange
        coEvery { repo.createWithEmail("test@example.com", "password") } returns Result.success(user)

        // Act
        viewModel.createWithEmail("test@example.com", "password")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Authenticated)
        assertEquals(user, (state as AuthUiState.Authenticated).user)
    }

    @Test
    fun `createWithEmail failure updates uiState to Error`() = runTest {
        coEvery { repo.createWithEmail("bad@example.com", "password") } returns Result.failure(Exception("Erreur de création"))

        viewModel.createWithEmail("bad@example.com", "password")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Erreur de création", (state as AuthUiState.Error).message)
    }

    @Test
    fun `signInWithEmail success updates uiState to Authenticated`() = runTest {
        coEvery { repo.signInWithEmail("test@example.com", "123456") } returns Result.success(user)

        viewModel.signInWithEmail("test@example.com", "123456")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Authenticated)
    }

    @Test
    fun `signInWithEmail failure updates uiState to Error`() = runTest {
        coEvery { repo.signInWithEmail("wrong@example.com", "badpass") } returns Result.failure(Exception("Erreur de connexion"))

        viewModel.signInWithEmail("wrong@example.com", "badpass")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Erreur de connexion", (state as AuthUiState.Error).message)
    }

    @Test
    fun `signOut sets uiState to Unauthenticated`() = runTest {
        viewModel.signOut()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Unauthenticated)
        coVerify { repo.signOut() }
    }

    @Test
    fun `registerUser success updates uiState to Authenticated`() = runTest {
        // Arrange
        coEvery { repo.createWithEmail(any(), any()) } returns Result.success(user)
        coEvery { repo.createUserProfile(any(), any(), any()) } returns Result.success(Unit)

        // Act
        viewModel.registerUser("John", "john@example.com", "pass", null)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Authenticated)
        assertEquals(user, (state as AuthUiState.Authenticated).user)
    }

    @Test
    fun `registerUser fails when createWithEmail returns error`() = runTest {
        coEvery { repo.createWithEmail(any(), any()) } returns Result.failure(Exception("Erreur création compte"))

        viewModel.registerUser("John", "john@example.com", "pass", null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Erreur création compte", (state as AuthUiState.Error).message)
    }

    @Test
    fun `registerUser fails when createUserProfile fails`() = runTest {
        coEvery { repo.createWithEmail(any(), any()) } returns Result.success(user)
        coEvery { repo.createUserProfile(any(), any(), any()) } returns Result.failure(Exception("Erreur création profil"))

        viewModel.registerUser("John", "john@example.com", "pass", null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Erreur création profil", (state as AuthUiState.Error).message)
    }
}