package com.example.p15_eventorias.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.viewmodels.EventViewModel

@Composable
fun EventItem(
    event: Event,
    eventViewModel: EventViewModel,
    onClick: () -> Unit = {}
) {
    var creatorPhotoUrl by remember { mutableStateOf<String?>(null) }

    // Récupère la photo de l’auteur depuis Firestore
    LaunchedEffect(event.creatorUid) {
        val url = eventViewModel.getUserByUid(event.creatorUid)
        creatorPhotoUrl = url
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo du créateur
            if (creatorPhotoUrl != null) {
                AsyncImage(
                    model = creatorPhotoUrl,
                    contentDescription = "Creator photo",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                )
            } else {
                Spacer(Modifier.width(8.dp))
            }
            // Partie texte
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(2f)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Partie image
            if (!event.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Event image",
                    modifier = Modifier
                        .width(136.dp)
                        .weight(2f)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}