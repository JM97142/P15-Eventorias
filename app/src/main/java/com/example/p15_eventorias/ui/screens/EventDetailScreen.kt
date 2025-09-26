package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.p15_eventorias.R
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventViewModel: EventViewModel,
    event: Event,
    onBack: () -> Unit
) {
    var creatorPhotoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(event.creatorUid) {
        val url = event.creatorUid.let { eventViewModel.getUserByUid(it) }
        creatorPhotoUrl = url
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.arrow_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(color = Color.Black)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image principale
            if (event.imageUrl != null) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(364.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            // Date et heure
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange,
                    null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    event.date,
                    color = Color.White
                )
            }
            if (creatorPhotoUrl != null) {
            AsyncImage(
                model = creatorPhotoUrl,
                contentDescription = stringResource(R.string.profil_picture),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning,
                    null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    event.time,
                    color = Color.White
                )
            }

            if (creatorPhotoUrl != null) {
                AsyncImage(
                    model = creatorPhotoUrl,
                    contentDescription = stringResource(R.string.profil_picture),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(Modifier.height(6.dp))

            // Description
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))

            // Adresse + Carte
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = event.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Afficher la carte statique si lat/lon dispo
                if (event.latitude != null && event.longitude != null) {
                    val apiKey = "AIzaSyCmn8FS9tx0neuQ2nAyX6bcvTJfOmf9ahg"
                    val mapUrl = "https://maps.googleapis.com/maps/api/staticmap" +
                            "?center=${event.latitude},${event.longitude}" +
                            "&zoom=15&size=400x400" +
                            "&markers=color:red%7C${event.latitude},${event.longitude}" +
                            "&key=$apiKey"

                    AsyncImage(
                        model = mapUrl,
                        contentDescription = "Map of ${event.address}",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}