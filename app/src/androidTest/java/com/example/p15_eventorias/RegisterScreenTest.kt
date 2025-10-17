package com.example.p15_eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.p15_eventorias.repository.AuthUiState
import com.example.p15_eventorias.ui.screens.RegisterScreen
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockAuthViewModel: AuthViewModel
    private val uiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)

    private var backClicked = false
    private var registerSuccess = false

    @Before
    fun setup() {
        mockAuthViewModel = mockk(relaxed = true)
        every { mockAuthViewModel.uiState } returns uiState
        backClicked = false
        registerSuccess = false
    }

    // ✅ Vérifie que tous les champs et boutons sont visibles
    @Test
    fun registerScreen_displaysAllFields_andButtons() {
        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockAuthViewModel,
                onBack = { backClicked = true },
                onRegisterSuccess = { registerSuccess = true }
            )
        }

        composeTestRule.onNodeWithText("Name").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Choose Profile Picture").assertExists()
        composeTestRule.onNodeWithTag("RegisterButton").assertExists()
    }

    // Vérifie que le bouton retour déclenche bien le callback
    @Test
    fun registerScreen_backButton_callsOnBack() {
        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockAuthViewModel,
                onBack = { backClicked = true },
                onRegisterSuccess = { registerSuccess = true }
            )
        }

        // Clique sur l'icône retour
        composeTestRule
            .onNodeWithContentDescription("BackButton", substring = true)
            .performClick()

        composeTestRule.waitForIdle()
        assert(backClicked)
    }

    // ✅ Vérifie que le bouton register ne fait rien si les champs sont vides
    @Test
    fun registerScreen_registerButton_withEmptyFields_doesNotCallRegisterUser() {
        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockAuthViewModel,
                onBack = {},
                onRegisterSuccess = {}
            )
        }

        composeTestRule.onNodeWithTag("RegisterButton").performClick()
        composeTestRule.waitForIdle()

        verify(exactly = 0) { mockAuthViewModel.registerUser(any(), any(), any(), any()) }
    }

    // Vérifie que registerUser est appelé avec les bonnes valeurs
    @Test
    fun registerScreen_registerButton_withFilledFields_callsRegisterUser() {
        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockAuthViewModel,
                onBack = {},
                onRegisterSuccess = {}
            )
        }

        composeTestRule.onNodeWithText("Name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("Email").performTextInput("john@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")

        composeTestRule.onNodeWithTag("RegisterButton").performClick()
        composeTestRule.waitForIdle()

        verify {
            mockAuthViewModel.registerUser("John Doe", "john@example.com", "123456", null)
        }
    }

    // Vérifie que l’erreur s’affiche correctement
    @Test
    fun registerScreen_errorState_displaysErrorMessage() {
        uiState.value = AuthUiState.Error("Erreur de création")

        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockAuthViewModel,
                onBack = {},
                onRegisterSuccess = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Erreur de création").assertExists()
    }

    // Vérifie que la redirection se fait si Authenticated
    @Test
    fun registerScreen_authenticatedState_triggersOnRegisterSuccess() {
        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockAuthViewModel,
                onBack = {},
                onRegisterSuccess = { registerSuccess = true }
            )
        }

        // Simule l’état connecté
        uiState.value = AuthUiState.Authenticated(mockk(relaxed = true))

        composeTestRule.waitUntil(timeoutMillis = 2000) { registerSuccess }
        assert(registerSuccess)
    }
}