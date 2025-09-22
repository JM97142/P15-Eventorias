package com.example.p15_eventorias.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p15_eventorias.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        fetchEvents()
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            val newEventRef = db.collection("events").document()
            val newEvent = event.copy(id = newEventRef.id)

            newEventRef.set(newEvent)
                .addOnFailureListener { e ->
                    Log.e("EventViewModel", "Error adding event", e)
                }
        }
    }

    private fun fetchEvents() {
        db.collection("events")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("EventViewModel", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _events.value = snapshot.toObjects(Event::class.java)
                }
            }
    }
}