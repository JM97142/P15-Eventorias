package com.example.p15_eventorias.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p15_eventorias.repository.AuthRepository
import com.example.p15_eventorias.repository.AuthUiState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        repo.currentUser()?.let {
            _uiState.value = AuthUiState.Authenticated(it)
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val res = repo.signInWithEmail(email, password)
            _uiState.value = res.fold(
                onSuccess = { AuthUiState.Authenticated(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Erreur de connexion") }
            )
        }
    }

    fun createWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val res = repo.createWithEmail(email, password)
            _uiState.value = res.fold(
                onSuccess = { AuthUiState.Authenticated(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Erreur de création") }
            )
        }
    }

    fun handleGoogleSignInTask(task: Task<GoogleSignInAccount>?) {
        if (task == null) {
            _uiState.value = AuthUiState.Error("Connexion Google annulée")
            return
        }
        viewModelScope.launch {
            try {
                val account = task.getResult(Exception::class.java)
                val idToken = account?.idToken ?: return@launch
                _uiState.value = AuthUiState.Loading
                val res = repo.signInWithGoogle(idToken)
                _uiState.value = res.fold(
                    onSuccess = { AuthUiState.Authenticated(it) },
                    onFailure = { AuthUiState.Error(it.message ?: "Erreur Google") }
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Erreur Google: ${e.message}")
            }
        }
    }

    fun signOut() {
        repo.signOut()
        _uiState.value = AuthUiState.Unauthenticated
    }
}