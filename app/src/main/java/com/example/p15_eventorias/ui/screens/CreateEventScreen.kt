package com.example.p15_eventorias.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.p15_eventorias.R
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.composables.DateTimePickerRow
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    eventViewModel: EventViewModel,
    onValidate: () -> Unit,
    onBack: () -> Unit,
    isTest: Boolean = false
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    val currentUserUid = Firebase.auth.currentUser?.uid ?: "test_uid"

    val context = LocalContext.current

    // Launchers
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> attachmentUri = uri }

    Scaffold(
        modifier = Modifier.semantics {
            contentDescription = "Écran de création d’un nouvel événement"
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.create_event)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics {
                            contentDescription = "Bouton retour. Appuyer pour revenir à la liste des événements."
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.arrow_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D1B20),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            // Loader
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF1D1B20))
                    .semantics { contentDescription = "Chargement en cours" },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .background(Color(0xFF1D1B20))
                    .fillMaxSize()
                    .padding(16.dp)
                    .semantics { contentDescription = "Formulaire de création d’événement" },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Champ de texte : titre de l’événement" },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Champ de texte : description de l’événement" },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                    )
                )

                DateTimePickerRow(
                    date = date,
                    onDateChange = { date = it },
                    time = time,
                    onTimeChange = { time = it },
                    isTest = isTest
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Champ de texte : adresse de l’événement" },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                    )
                )

                // Boutons
                Row(
                    Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Boutons pour ajouter une image ou une pièce jointe" },
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .semantics { contentDescription = "Bouton pour choisir une image" }
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    IconButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.Red, shape = RoundedCornerShape(12.dp))
                            .semantics { contentDescription = "Bouton pour ajouter une pièce jointe" }
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                // preview image
                imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .semantics { contentDescription = "Aperçu de l’image sélectionnée" },
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.weight(1f))

                // Save event
                Button(
                    onClick = {
                        if (title.isBlank() || description.isBlank() || date.isBlank() || time.isBlank() || address.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }

                        isLoading = true

                        val geocoder = android.location.Geocoder(context)
                        var latitude: Double? = null
                        var longitude: Double? = null

                        try {
                            val results = geocoder.getFromLocationName(address, 1)?.toList()
                            if (!results.isNullOrEmpty()) {
                                latitude = results[0].latitude
                                longitude = results[0].longitude
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Impossible de localiser l'adresse",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        val event = Event(
                            title = title,
                            description = description,
                            date = date,
                            time = time,
                            address = address,
                            latitude = latitude,
                            longitude = longitude,
                            creatorUid = currentUserUid
                        )
                        eventViewModel.uploadFileAndCreateEvent(
                            imageUri,
                            attachmentUri,
                            event,
                            onSuccess = onValidate,
                            onError = { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Bouton valider. Appuyer pour créer l’événement." },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(stringResource(id = R.string.validate))
                }
            }
        }
    }
}