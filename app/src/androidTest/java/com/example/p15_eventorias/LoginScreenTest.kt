package com.example.p15_eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.p15_eventorias.repository.AuthUiState
import com.example.p15_eventorias.ui.screens.LoginScreen
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.runs

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AuthViewModel
    private lateinit var uiState: MutableStateFlow<AuthUiState>

    private var googleClicked = false
    private var loginSuccess = false
    private var goToRegister = false

    @Before
    fun setup() {
        uiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)

        mockViewModel = mockk<AuthViewModel>(relaxed = true)
        every { mockViewModel.uiState } returns uiState
        every { mockViewModel.signInWithEmail(any(), any()) } just runs
    }

    private fun setContent() {
        composeTestRule.setContent {
            LoginScreen(
                authViewModel = mockViewModel,
                onGoogleSignIn = { googleClicked = true },
                onLoginSuccess = { loginSuccess = true },
                onGoToRegister = { goToRegister = true }
            )
        }
    }

    // Présence de base de l’écran
    @Test
    fun loginScreen_displaysBasicElements() {
        setContent()

        composeTestRule.onNodeWithContentDescription("Écran de connexion à l'application Eventorias")
            .assertExists()

        composeTestRule.onNodeWithContentDescription("Bouton pour se connecter avec un compte Google")
            .assertExists()

        composeTestRule.onNodeWithContentDescription("Bouton pour se connecter avec un email")
            .assertExists()
    }

    // Clic sur bouton Google
    @Test
    fun clickingGoogleButton_triggersCallback() {
        setContent()

        composeTestRule
            .onNodeWithContentDescription("Bouton pour se connecter avec un compte Google")
            .performClick()

        assert(googleClicked)
    }

    // Affichage du formulaire e-mail
    @Test
    fun clickingEmailButton_showsEmailForm() {
        setContent()

        composeTestRule
            .onNodeWithContentDescription("Bouton pour se connecter avec un email")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Champ de saisie pour l'adresse e-mail")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Champ de saisie pour le mot de passe")
            .assertExists()
    }

    // Champ e-mail et mot de passe éditables
    @Test
    fun emailPasswordFields_acceptInput() {
        setContent()

        composeTestRule
            .onNodeWithContentDescription("Bouton pour se connecter avec un email")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Champ de saisie pour l'adresse e-mail")
            .performTextInput("user@test.com")

        composeTestRule
            .onNodeWithContentDescription("Champ de saisie pour le mot de passe")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("user@test.com")
            .assertExists()
    }

    // Bouton annuler revient à l’état initial
    @Test
    fun cancelButton_returnsToMainView() {
        setContent()

        composeTestRule
            .onNodeWithContentDescription("Bouton pour se connecter avec un email")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Bouton pour annuler la saisie du formulaire e-mail")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Bouton pour se connecter avec un compte Google")
            .assertExists()
    }

    // Affichage de l’état Loading
    @Test
    fun showsProgressIndicator_whenLoading() {
        uiState.value = AuthUiState.Loading
        setContent()

        composeTestRule
            .onNodeWithContentDescription("Chargement en cours")
            .assertExists()
    }

    // Affichage d’un message d’erreur
    @Test
    fun showsErrorMessage_whenErrorState() {
        uiState.value = AuthUiState.Error("Identifiants incorrects")
        setContent()

        composeTestRule
            .onNodeWithContentDescription("Erreur : Identifiants incorrects")
            .assertExists()
    }
}