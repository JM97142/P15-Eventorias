package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.p15_eventorias.R
import com.example.p15_eventorias.model.Event
import com.example.p15_eventorias.ui.composables.EventItem
import com.example.p15_eventorias.ui.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    eventViewModel: EventViewModel,
    onAddEvent: () -> Unit,
    onProfile: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    val events by eventViewModel.events.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Filtre les events
    val filteredEvents = events
        .sortedByDescending { it.date } // tri du plus récent au plus ancien
        .filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        modifier = Modifier.semantics {
            contentDescription = "Écran d'accueil affichant la liste des événements"
        },
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "Champ de recherche pour filtrer les événements par titre"
                                },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                            )
                        )
                    } else {
                        Text(
                            stringResource(id = R.string.events),
                            modifier = Modifier.semantics {
                                contentDescription = "Titre : Liste des événements"
                            }
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isSearching = !isSearching },
                        modifier = Modifier.semantics {
                            contentDescription =
                                if (isSearching) "Fermer la recherche" else "Ouvrir la recherche d'événements"
                        }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D1B20),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEvent,
                contentColor = Color.White,
                containerColor = Color.Red,
                modifier = Modifier.semantics {
                    contentDescription = "Bouton pour ajouter un nouvel événement"
                }
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1D1B20),
                contentColor = Color.White,
                modifier = Modifier.semantics {
                    contentDescription = "Barre de navigation principale"
                }
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    label = { Text(stringResource(id = R.string.events)) },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Gray
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Onglet Événements sélectionné"
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfile,
                    label = { Text(stringResource(id = R.string.profil)) },
                    icon = { Icon(Icons.Default.Person, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Gray
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Onglet Profil. Appuyer pour afficher les informations du profil utilisateur"
                    }
                )
            }
        }
    ) { padding ->
        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF1D1B20))
                    .semantics {
                        contentDescription =
                            if (events.isEmpty()) "Aucun événement disponible pour le moment"
                            else "Aucun événement ne correspond à votre recherche"
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("No events yet", color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFF1D1B20))
                    .semantics {
                        contentDescription = "Liste des événements disponibles"
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredEvents) { event ->
                    EventItem(
                        event = event,
                        eventViewModel = eventViewModel,
                        onClick = { onEventClick(event) })
                }
            }
        }
    }
}