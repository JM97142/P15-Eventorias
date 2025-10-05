package com.example.p15_eventorias.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p15_eventorias.ui.screens.CreateEventScreen
import com.example.p15_eventorias.ui.screens.EventDetailScreen
import com.example.p15_eventorias.ui.screens.HomeScreen
import com.example.p15_eventorias.ui.screens.LoginScreen
import com.example.p15_eventorias.ui.screens.ProfileScreen
import com.example.p15_eventorias.ui.screens.RegisterScreen
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import com.example.p15_eventorias.ui.viewmodels.EventViewModel
import com.example.p15_eventorias.utils.SignedInEventBus

@Composable
fun AppNavGraph(onStartGoogleSignIn: () -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onGoogleSignIn = { onStartGoogleSignIn() },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("profil") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                eventViewModel = eventViewModel,
                onAddEvent = { navController.navigate("addEvent") },
                onProfile = { navController.navigate("profil") },
                onEventClick = { event ->
                    navController.navigate("eventDetail/${event.id}")
                }
            )
        }
        composable("addEvent") {
            CreateEventScreen(
                eventViewModel = eventViewModel,
                onValidate = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable("profil") {
            ProfileScreen(
                user = authViewModel.currentUser(),
                authViewModel = authViewModel,
                onEventsList = { navController.navigate("home") },
                onLogout = { navController.navigate("login") }
            )
        }
        composable("eventDetail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")

            eventId?.let {
                // on fetch l’event avec l’ID
                val event = eventViewModel.events.value.find { e -> e.id == it }

                event?.let { safeEvent ->
                    EventDetailScreen(
                        eventViewModel = eventViewModel,
                        event = safeEvent,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }

    // Observe Google sign-in results
    androidx.compose.runtime.LaunchedEffect(Unit) {
        SignedInEventBus.googleSignInTasks.collect { task ->
            authViewModel.handleGoogleSignInTask(task)
        }
    }
}