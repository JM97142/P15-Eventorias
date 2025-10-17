package com.example.p15_eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.screens.HomeScreen
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Fake EventViewModel
    class FakeEventViewModel(initialEvents: List<Event>) : EventViewModel(android.app.Application()) {
        private val _eventsFlow = MutableStateFlow(initialEvents)
        override val events: StateFlow<List<Event>> get() = _eventsFlow

        override suspend fun getUserByUid(uid: String): String? {
            return null // on évite tout appel Firestore
        }
    }

    private fun fakeEvents() = listOf(
        Event(id = "1", title = "Concert", date = "2025-10-10"),
        Event(id = "2", title = "Conference", date = "2025-09-01"),
        Event(id = "3", title = "Workshop", date = "2025-08-15")
    )

    @Test
    fun eventsDisplayed_correctly() {
        val fakeVM = FakeEventViewModel(fakeEvents())

        composeTestRule.setContent {
            HomeScreen(
                eventViewModel = fakeVM,
                onAddEvent = {},
                onProfile = {},
                onEventClick = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Concert", substring = true).assertExists()
        composeTestRule.onNodeWithText("Conference", substring = true).assertExists()
        composeTestRule.onNodeWithText("Workshop", substring = true).assertExists()
    }

    @Test
    fun filteringEvents_worksCorrectly() {
        val fakeVM = FakeEventViewModel(fakeEvents())

        composeTestRule.setContent {
            HomeScreen(
                eventViewModel = fakeVM,
                onAddEvent = {},
                onProfile = {},
                onEventClick = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Ouvrir la recherche d'événements")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Champ de recherche pour filtrer les événements par titre")
            .performTextInput("work")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Workshop", substring = true).assertExists()
        composeTestRule.onNodeWithText("Concert", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("Conference", substring = true).assertDoesNotExist()
    }

    @Test
    fun clickingEvent_callsOnEventClick() {
        val fakeVM = FakeEventViewModel(fakeEvents())
        var clickedEvent: Event? = null

        composeTestRule.setContent {
            HomeScreen(
                eventViewModel = fakeVM,
                onAddEvent = {},
                onProfile = {},
                onEventClick = { clickedEvent = it }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Conference", substring = true).performClick()

        assert(clickedEvent?.title == "Conference")
    }
}