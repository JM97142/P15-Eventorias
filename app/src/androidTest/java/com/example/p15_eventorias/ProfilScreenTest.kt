package com.example.p15_eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.p15_eventorias.ui.screens.ProfileScreen
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import com.example.p15_eventorias.ui.viewmodels.NotificationsViewModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockAuthViewModel: AuthViewModel
    private lateinit var mockNotifViewModel: NotificationsViewModel
    private lateinit var mockUser: FirebaseUser

    private val notificationsState = MutableStateFlow(false)

    @Before
    fun setup() {
        mockAuthViewModel = mockk(relaxed = true)
        mockNotifViewModel = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)

        every { mockUser.displayName } returns "Jean Dupont"
        every { mockUser.email } returns "jean.dupont@example.com"
        every { mockUser.photoUrl } returns null
        every { mockUser.uid } returns "uid123"
        every { mockNotifViewModel.notificationsEnabled } returns notificationsState
    }

    @Test
    fun profileScreen_displaysUserInformation() {
        composeTestRule.setContent {
            ProfileScreen(
                user = mockUser,
                authViewModel = mockAuthViewModel,
                notificationsViewModel = mockNotifViewModel,
                onEventsList = {},
                onLogout = {}
            )
        }

        // Vérifie le titre
        composeTestRule
            .onNodeWithContentDescription("Titre de l'écran : Profil utilisateur")
            .assertExists()

        // Vérifie nom utilisateur
        composeTestRule
            .onNodeWithContentDescription("Nom de l’utilisateur : Jean Dupont")
            .assertExists()

        // Vérifie email
        composeTestRule
            .onNodeWithContentDescription("Adresse email de l’utilisateur : jean.dupont@example.com")
            .assertExists()
    }

    @Test
    fun profileScreen_clickLogout_callsSignOut() {
        composeTestRule.setContent {
            ProfileScreen(
                user = mockUser,
                authViewModel = mockAuthViewModel,
                notificationsViewModel = mockNotifViewModel,
                onEventsList = {},
                onLogout = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Bouton de déconnexion. Appuyer pour se déconnecter du compte.")
            .performClick()

        verify { mockAuthViewModel.signOut() }
    }

    @Test
    fun profileScreen_toggleNotifications_updatesViewModel() {

        composeTestRule.setContent {
            ProfileScreen(
                user = mockUser,
                authViewModel = mockAuthViewModel,
                notificationsViewModel = mockNotifViewModel,
                onEventsList = {},
                onLogout = {},
                isTest = true
            )
        }

        composeTestRule.onNodeWithTag("notif_switch")
            .performClick()

        verify { mockNotifViewModel.enableNotifications() }

        notificationsState.value = true
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("notif_switch")
            .performClick()

        verify { mockNotifViewModel.disableNotifications() }
    }

    @Test
    fun profileScreen_navigationBar_showsCorrectItems() {
        composeTestRule.setContent {
            ProfileScreen(
                user = mockUser,
                authViewModel = mockAuthViewModel,
                notificationsViewModel = mockNotifViewModel,
                onEventsList = {},
                onLogout = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Onglet Événements. Appuyer pour revenir à la liste des événements.")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Onglet Profil sélectionné")
            .assertExists()
    }
}