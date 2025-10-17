package com.example.p15_eventorias.repository

import com.google.firebase.auth.FirebaseUser

sealed class AuthUiState {
    data object Unauthenticated : AuthUiState()
    data object Loading : AuthUiState()
    data class Authenticated(val user: FirebaseUser?) : AuthUiState()
    data class Error(val message: String?) : AuthUiState()
}