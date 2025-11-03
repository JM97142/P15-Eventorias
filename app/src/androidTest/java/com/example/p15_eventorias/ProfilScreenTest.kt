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
                onLogout = {},
                isTest = true
            )
        }

        // Vérifie le titre du screen
        composeTestRule
            .onNodeWithContentDescription("Écran de profil utilisateur")
            .assertExists()

        // Vérifie nom utilisateur
        composeTestRule
            .onNode(hasText("Jean Dupont"))
            .assertExists()

        // Vérifie email
        composeTestRule
            .onNode(hasText("jean.dupont@example.com"))
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
                onLogout = {},
                isTest = true
            )
        }

        // Clique sur le bouton de logout
        composeTestRule
            .onNode(hasText("Logout"))
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

        // Active la notification
        composeTestRule.onNode(isToggleable())
            .performClick()
        verify { mockNotifViewModel.enableNotifications() }

        notificationsState.value = true
        composeTestRule.waitForIdle()

        // Désactive la notification
        composeTestRule.onNode(isToggleable())
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
                onLogout = {},
                isTest = true
            )
        }

        // Onglet Events
        composeTestRule
            .onNode(hasText("Events") and isNotSelected())
            .assertExists()

        // Onglet Profile sélectionné
        composeTestRule
            .onNode(hasText("Profile") and isSelected())
            .assertExists()
    }
}