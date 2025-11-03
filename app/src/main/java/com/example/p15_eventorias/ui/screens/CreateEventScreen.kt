package com.example.p15_eventorias.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
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

@Composable
fun CreateEventScreen(
    eventViewModel: EventViewModel,
    onValidate: () -> Unit,
    onBack: () -> Unit,
    isTest: Boolean = false
) {
    val context = LocalContext.current
    val currentUserUid = Firebase.auth.currentUser?.uid ?: "test_uid"

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> attachmentUri = uri }

    Scaffold(
        modifier = Modifier.semantics { contentDescription = "Écran de création d’un nouvel événement" },
        topBar = { CreateEventTopBar(onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1D1B20))
        ) {
            if (isLoading) {
                Loader()
            } else {
                CreateEventForm(
                    title, { title = it },
                    description, { description = it },
                    date, { date = it },
                    time, { time = it },
                    address, { address = it },
                    imageUri, { imageUri = it },
                    attachmentUri, { attachmentUri = it },
                    imagePickerLauncher,
                    filePickerLauncher,
                    onSave = {
                        handleSaveEvent(
                            context,
                            title,
                            description,
                            date,
                            time,
                            address,
                            imageUri,
                            attachmentUri,
                            currentUserUid,
                            eventViewModel,
                            onValidate,
                            onLoadingChange = { isLoading = it }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEventTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.create_event)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1D1B20),
            titleContentColor = Color.White
        )
    )
}

@Composable
private fun Loader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.Red)
    }
}

@Composable
private fun CreateEventForm(
    title: String, onTitleChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    date: String, onDateChange: (String) -> Unit,
    time: String, onTimeChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    imageUri: Uri?, onImageChange: (Uri?) -> Unit,
    attachmentUri: Uri?, onAttachmentChange: (Uri?) -> Unit,
    imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    filePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Formulaire de création d’événement" },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EventTextField("Title", title, onTitleChange)
        EventTextField("Description", description, onDescriptionChange)
        DateTimePickerRow(date, onDateChange, time, onTimeChange)
        EventTextField("Address", address, onAddressChange)
        FilePickersRow(imagePickerLauncher, filePickerLauncher, imageUri, onImageChange)
        imageUri?.let { AsyncImage(model = it, contentDescription = "Preview", modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop) }
        Spacer(Modifier.weight(1f))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)) {
            Text(stringResource(id = R.string.validate))
        }
    }
}

@Composable
private fun EventTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray,
        )
    )
}

@Composable
private fun FilePickersRow(
    imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    filePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    imageUri: Uri?, onImageChange: (Uri?) -> Unit
) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
            Icon(Icons.Default.AddAPhoto, contentDescription = null)
        }
        Spacer(Modifier.width(16.dp))
        IconButton(onClick = { filePickerLauncher.launch("*/*") }) {
            Icon(Icons.Default.AttachFile, contentDescription = null)
        }
    }
}

private fun handleSaveEvent(
    context: Context,
    title: String,
    description: String,
    date: String,
    time: String,
    address: String,
    imageUri: Uri?,
    attachmentUri: Uri?,
    currentUserUid: String,
    eventViewModel: EventViewModel,
    onValidate: () -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    if (listOf(title, description, date, time, address).any { it.isBlank() }) {
        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        return
    }

    onLoadingChange(true)

    val geocoder = android.location.Geocoder(context)
    var latitude: Double? = null
    var longitude: Double? = null

    try {
        geocoder.getFromLocationName(address, 1)?.firstOrNull()?.let {
            latitude = it.latitude
            longitude = it.longitude
        }
    } catch (_: Exception) {
        Toast.makeText(context, "Impossible de localiser l'adresse", Toast.LENGTH_SHORT).show()
    }

    val event = Event(title, description, date, time, address,
        latitude.toString(), longitude.toString(), currentUserUid)

    eventViewModel.uploadFileAndCreateEvent(
        imageUri,
        attachmentUri,
        event,
        onSuccess = onValidate,
        onError = { e -> Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
    )
}