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

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Address").assertIsDisplayed()

        composeTestRule.onNodeWithText("Validate").assertIsDisplayed()
    }

    @Test
    fun fillingAllFields_andClickingValidate_callsUploadFileAndCreateEvent() {
        every {
            mockViewModel.uploadFileAndCreateEvent(any(), any(), any(), any(), any())
        } just Runs

        composeTestRule.setContent {
            CreateEventScreen(
                eventViewModel = mockViewModel,
                onValidate = {},
                onBack = {},
                isTest = true
            )
        }

        composeTestRule.onNodeWithText("Title").performTextInput("Test Event")
        composeTestRule.onNodeWithText("Description").performTextInput("Test description")
        composeTestRule.onNodeWithText("Date").performTextInput("01/01/2025")
        composeTestRule.onNodeWithText("Time").performTextInput("12:00")
        composeTestRule.onNodeWithText("Address").performTextInput("123 Test Street")

        composeTestRule.onNodeWithText("Validate").performClick()

        verify(timeout = 3000) {
            mockViewModel.uploadFileAndCreateEvent(
                imageUri = any(),
                attachmentUri = any(),
                event = any(),
                onSuccess = any(),
                onError = any()
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

        composeTestRule.onNodeWithText("Validate")
            .performClick()

        verify(exactly = 0) {
            mockViewModel.uploadFileAndCreateEvent(any(), any(), any(), any(), any())
        }
    }
}