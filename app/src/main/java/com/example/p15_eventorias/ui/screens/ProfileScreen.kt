package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.p15_eventorias.R
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import com.example.p15_eventorias.ui.viewmodels.NotificationsViewModel
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: FirebaseUser?,
    authViewModel: AuthViewModel,
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    onEventsList: () -> Unit,
    onLogout: () -> Unit
) {
    val notificationsEnabled by notificationsViewModel.notificationsEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profil),
                        color = Color.White
                    )
                },
                actions = {
                    AsyncImage(
                        model = user?.photoUrl,
                        contentDescription = stringResource(R.string.profil_picture),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onEventsList,
                    label = { Text("Events") },
                    icon = { Icon(Icons.Default.DateRange, null) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, null) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = user?.displayName ?: "",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.user_name)) },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledLabelColor = Color.White,
                    disabledIndicatorColor = Color.Gray,
                    disabledContainerColor = Color.DarkGray
                )
            )

            OutlinedTextField(
                value = user?.email ?: "",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.email)) },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledLabelColor = Color.White,
                    disabledIndicatorColor = Color.Gray,
                    disabledContainerColor = Color.DarkGray
                )
            )

            Spacer(Modifier.height(16.dp))
            // Bouton Logout
            Button(
                onClick = {
                    authViewModel.signOut()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(242.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(stringResource(id = R.string.logout))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            notificationsViewModel.enableNotifications()
                        } else {
                            notificationsViewModel.disableNotifications()
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Red
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(id = R.string.notifications),
                    color = Color.White
                )
            }
        }
    }
}