package com.example.p15_eventorias.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.p15_eventorias.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        fetchEvents()
    }

    private fun addEvent(event: Event) {
        viewModelScope.launch {
            val newEventRef = db.collection("events").document()
            val newEvent = event.copy(id = newEventRef.id)

            newEventRef.set(newEvent)
                .addOnFailureListener { e ->
                    Log.e("EventViewModel", "Error adding event", e)
                }
        }
    }

    fun uploadFileAndCreateEvent(
        imageUri: Uri?,
        attachmentUri: Uri?,
        event: Event,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var imageUrl: String? = null
                var attachmentUrl: String? = null

                // Upload image si présent
                if (imageUri != null) {
                    val imgRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
                    imgRef.putFile(imageUri).await()
                    imageUrl = imgRef.downloadUrl.await().toString()
                }

                // Upload fichier si présent
                if (attachmentUri != null) {
                    val fileRef = storage.reference.child("attachments/${UUID.randomUUID()}")
                    fileRef.putFile(attachmentUri).await()
                    attachmentUrl = fileRef.downloadUrl.await().toString()
                }

                // Créer l’event avec URLs
                val finalEvent = event.copy(
                    imageUrl = imageUrl,
                    attachmentUrl = attachmentUrl
                )
                addEvent(finalEvent)
                onSuccess()

            } catch (e: Exception) {
                onError(e)
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