package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.p15_eventorias.R
import com.example.p15_eventorias.ui.composables.EventItem
import com.example.p15_eventorias.ui.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    eventViewModel: EventViewModel,
    onAddEvent: () -> Unit,
    onProfile: () -> Unit
) {
    val events by eventViewModel.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.events)) },
                actions = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEvent,
                contentColor = Color.White,
                containerColor = Color.Red
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_event)
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    label = { Text("Events") },
                    icon = { Icon(Icons.Default.DateRange, null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfile,
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, null) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding).padding(16.dp)
        ) {
            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events yet")
                }
            } else {
                events.forEach { event ->
                    EventItem(event = event) {
                        /* TODO: open event details */
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}