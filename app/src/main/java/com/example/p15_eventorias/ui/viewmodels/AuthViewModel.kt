package com.example.p15_eventorias.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p15_eventorias.repository.AuthRepository
import com.example.p15_eventorias.repository.AuthUiState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
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

    // Expose l'utilisateur courant
    fun currentUser(): FirebaseUser? = repo.currentUser()

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

    fun registerUser(name: String, email: String, password: String, photoUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val createRes = repo.createWithEmail(email, password)
            createRes.fold(
                onSuccess = { user ->
                    if (user != null) {
                        val profileRes = repo.createUserProfile(user, name, photoUri)
                        _uiState.value = profileRes.fold(
                            onSuccess = { AuthUiState.Authenticated(user) },
                            onFailure = { AuthUiState.Error(it.message ?: "Erreur création profil") }
                        )
                    } else {
                        _uiState.value = AuthUiState.Error("Utilisateur null")
                    }
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Erreur création compte")
                }
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