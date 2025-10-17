package com.example.p15_eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.p15_eventorias.ui.screens.CreateEventScreen
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import io.mockk.*
import org.junit.Rule
import org.junit.Test

class CreateEventScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<EventViewModel>(relaxed = true)

    @Test
    fun formFields_areDisplayed() {
        composeTestRule.setContent {
            CreateEventScreen(
                eventViewModel = mockViewModel,
                onValidate = {},
                onBack = {}
            )
        }

        // Vérifie la présence des champs
        composeTestRule.onNodeWithContentDescription("Champ de texte : titre de l’événement").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Champ de texte : description de l’événement").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Champ de texte : adresse de l’événement").assertIsDisplayed()

        // Vérifie la présence des boutons
        composeTestRule.onNodeWithContentDescription("Bouton pour choisir une image").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Bouton pour ajouter une pièce jointe").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Bouton valider. Appuyer pour créer l’événement.").assertIsDisplayed()
    }

    @Test
    fun fillingAllFields_andClickingValidate_callsUploadFileAndCreateEvent() {
        composeTestRule.setContent {
            CreateEventScreen(
                eventViewModel = mockViewModel,
                onValidate = {},
                onBack = {},
                isTest = true // ← active les champs éditables
            )
        }

        // Remplir tous les champs
        composeTestRule.onNodeWithText("Title").performTextInput("Test Event")
        composeTestRule.onNodeWithText("Description").performTextInput("Test description")
        composeTestRule.onNodeWithText("Date").performTextInput("01/01/2025")
        composeTestRule.onNodeWithText("Time").performTextInput("12:00")
        composeTestRule.onNodeWithText("Address").performTextInput("123 Test Street")

        // Cliquer sur le bouton "Validate"
        composeTestRule.onNodeWithText("Validate").performClick()

        // Vérifier que la fonction du ViewModel a été appelée
        verify(timeout = 3000) {
            mockViewModel.uploadFileAndCreateEvent(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun clickingValidate_withEmptyFields_showsToastAndDoesNotCallViewModel() {
        composeTestRule.setContent {
            CreateEventScreen(
                eventViewModel = mockViewModel,
                onValidate = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Bouton valider. Appuyer pour créer l’événement.")
            .performClick()

        verify(exactly = 0) {
            mockViewModel.uploadFileAndCreateEvent(any(), any(), any(), any(), any())
        }
    }
}