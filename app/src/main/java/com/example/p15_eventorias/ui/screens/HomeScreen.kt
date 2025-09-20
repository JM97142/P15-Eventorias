package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.p15_eventorias.model.Event
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
                title = { Text("Events") })
                 },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
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
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (events.isEmpty()) {
                Text("No events yet")
            } else {
                events.forEach { event ->
                    EventItem(event)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* open detail */ }
            .padding(8.dp)
    ) {
        Text(event.title)
        Spacer(Modifier.height(4.dp))
        Text(event.description)
        Spacer(Modifier.height(2.dp))
        Text("${event.date} - ${event.time}")
    }
}