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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

    var userName by remember { mutableStateOf(user?.displayName ?: "") }
    var userPhotoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }

    // Récupération du profil Firestore (si email/password)
    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            authViewModel.getUserByUid(uid) { url ->
                userPhotoUrl = url
            }
            authViewModel.getUserNameByUid(uid) { name ->
                if (!name.isNullOrBlank()) userName = name
            }
        }
    }

    Scaffold(
        modifier = Modifier.semantics {
            contentDescription = "Écran de profil utilisateur"
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profil),
                        color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription = "Titre de l'écran : Profil utilisateur"
                        }
                    )
                },
                actions = {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = stringResource(R.string.profil_picture),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .semantics {
                                contentDescription = "Photo de profil de l’utilisateur"
                            }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D1B20),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1D1B20),
                contentColor = Color.White,
                modifier = Modifier.semantics {
                    contentDescription = "Barre de navigation du profil"
                }
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onEventsList,
                    label = { Text("Events") },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Gray
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Onglet Événements. Appuyer pour revenir à la liste des événements."
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Gray
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Onglet Profil sélectionné"
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1D1B20))
                .padding(padding)
                .padding(16.dp)
                .semantics {
                    contentDescription = "Informations de profil de l’utilisateur"
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = userName,
                onValueChange = {},
                label = { Text(stringResource(id = R.string.user_name)) },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Nom de l’utilisateur : $userName"
                    },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Adresse email de l’utilisateur : ${user?.email ?: "Non disponible"}"
                    },
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
                    .width(242.dp)
                    .semantics {
                        contentDescription = "Bouton de déconnexion. Appuyer pour se déconnecter du compte."
                    },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(stringResource(id = R.string.logout))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics {
                    contentDescription = if (notificationsEnabled)
                        "Notifications activées. Appuyer pour les désactiver."
                    else
                        "Notifications désactivées. Appuyer pour les activer."
                }
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