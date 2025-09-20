package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    user: FirebaseUser?,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AsyncImage(
                model = user?.photoUrl,
                contentDescription = "Profile picture",
                modifier = Modifier.size(48.dp).clip(CircleShape)
            )
        }

        OutlinedTextField(
            value = user?.displayName ?: "",
            onValueChange = {},
            label = { Text("Name") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = user?.email ?: "",
            onValueChange = {},
            label = { Text("E-mail") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Bouton Logout
        Button(
            onClick = {
                authViewModel.signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            Spacer(Modifier.width(8.dp))
            Text("Notifications")
        }
    }
}