package com.example.p15_eventorias.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
class NotificationsViewModel : ViewModel() {

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    // Récupère le token FCM au démarrage du ViewModel
    init {
        fetchFcmToken()
    }
    /**
     * Enables notifications for the application.
     *
     */
    fun enableNotifications() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        _notificationsEnabled.value = true
        fetchFcmToken()
    }

    /**
     * Disables notifications for the application.
     *
     */
    fun disableNotifications() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        _notificationsEnabled.value = false
        _fcmToken.value = null
    }

    /**
     * Récupère le token FCM de l’appareil.
     */
    private fun fetchFcmToken() {
        viewModelScope.launch {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d("SettingsViewModel", "FCM Token: $token")
                        _fcmToken.value = token
                    } else {
                        Log.w("SettingsViewModel", "Échec récupération token", task.exception)
                    }
                }
        }
    }

    /**
     * Permet de mettre à jour le token FCM.
     */
    fun updateFcmToken(token: String) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Token mis à jour par le service: $token")
            _fcmToken.value = token
        }
    }
}