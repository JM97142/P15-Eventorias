package com.example.p15_eventorias.repository

import com.google.firebase.auth.FirebaseUser

sealed class AuthUiState {
    object Unauthenticated : AuthUiState()
    object Loading : AuthUiState()
    data class Authenticated(val user: FirebaseUser?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}