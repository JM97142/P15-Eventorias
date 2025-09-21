package com.example.p15_eventorias.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p15_eventorias.ui.screens.CreateEventScreen
import com.example.p15_eventorias.ui.screens.HomeScreen
import com.example.p15_eventorias.ui.screens.LoginScreen
import com.example.p15_eventorias.ui.screens.ProfileScreen
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
                }
            )
        }
        composable("home") {
            HomeScreen(
                eventViewModel = eventViewModel,
                onAddEvent = { navController.navigate("addEvent") },
                onProfile = { navController.navigate("profil") }
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
                onLogout = { navController.navigate("login") }
            )
        }
    }

    // Observe Google sign-in results
    androidx.compose.runtime.LaunchedEffect(Unit) {
        SignedInEventBus.googleSignInTasks.collect { task ->
            authViewModel.handleGoogleSignInTask(task)
        }
    }
}