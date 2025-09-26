package com.example.p15_eventorias.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.p15_eventorias.ui.nav.AppNavGraph
import com.example.p15_eventorias.ui.theme.P15EventoriasTheme
import com.example.p15_eventorias.utils.SignedInEventBus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération du token FCM
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Échec récupération token", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM", "Token: $token")
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("692744991033-0dqvlm07sqmlfl1jfgb2gpvhu629pgkh.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                SignedInEventBus.postGoogleTask(task)
            } else {
                SignedInEventBus.postGoogleTask(null)
            }
        }

        setContent {
            P15EventoriasTheme {
                AppNavGraph(
                    onStartGoogleSignIn = {
                        val intent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(intent)
                    }
                )
            }
        }
    }
}