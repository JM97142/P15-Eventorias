package com.example.p15_eventorias.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.p15_eventorias.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import javax.net.ssl.HttpsURLConnection

open class EventViewModel(
    application: Application,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    open val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                fetchEvents()
            } else {
                Log.d("EventViewModel", "Utilisateur déconnecté : pas de fetchEvents()")
            }
        }
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

    private suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
        return withContext(ioDispatcher) {
            try {
                val urlAddress = URLEncoder.encode(address, "UTF-8")
                val url = URL("https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&q=$urlAddress")

                val connection = (url.openConnection() as HttpsURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "EventoriasApp/1.0 (contact@eventorias.com)")
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)

                if (jsonArray.length() > 0) {
                    val obj = jsonArray.getJSONObject(0)

                    // Récupération des valeurs String
                    val latStr = obj.getString("lat")
                    val lonStr = obj.getString("lon")

                    // Conversion sécurisée en Double
                    val lat = latStr.toDoubleOrNull()
                    val lon = lonStr.toDoubleOrNull()

                    if (lat != null && lon != null) Pair(lat, lon) else null
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
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

                // Géocodage de l'adresse
                val coords = geocodeAddress(event.address)

                // Récupérer UID du user courant
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                // Créer l’event avec URLs
                val finalEvent = event.copy(
                    imageUrl = imageUrl,
                    attachmentUrl = attachmentUrl,
                    latitude = coords?.first,
                    longitude = coords?.second,
                    creatorUid = currentUid
                )
                addEvent(finalEvent)
                onSuccess()

            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    open suspend fun getUserByUid(uid: String): String? {
        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .get()
                .await()

            if (snapshot.exists()) {
                snapshot.getString("photoUrl")
            } else {
                null
            }
        } catch (e: Exception) {
            null
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