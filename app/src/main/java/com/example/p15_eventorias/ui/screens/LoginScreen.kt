package com.example.p15_eventorias.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.p15_eventorias.R
import com.example.p15_eventorias.repository.AuthUiState
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onGoogleSignIn: () -> Unit,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Authenticated) {
            onLoginSuccess()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showEmailFields by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D1B20))
            .padding(85.dp)
            .semantics {
                contentDescription = "Écran de connexion à l'application Eventorias"
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_eventorias),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier
                .size(200.dp)
        )

        if (showEmailFields) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Champ de saisie pour l'adresse e-mail"
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Champ de saisie pour le mot de passe"
                    },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.signInWithEmail(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .width(242.dp)
                    .semantics {
                        contentDescription = "Bouton pour valider la connexion avec e-mail et mot de passe"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(
                    stringResource(id = R.string.validate)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { showEmailFields = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .width(242.dp)
                    .semantics {
                        contentDescription = "Bouton pour annuler la saisie du formulaire e-mail"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(
                    stringResource(id = R.string.cancel)
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Lien pour créer un compte si vous n'en avez pas encore"
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pas encore de compte ? ",
                    color = Color.White
                )
                TextButton(onClick = { onGoToRegister() }) {
                    Text("Créer un compte", color = Color.Red)
                }
            }
        } else {

            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .width(242.dp)
                    .semantics {
                        contentDescription = "Bouton pour se connecter avec un compte Google"
                    },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = stringResource(id = R.string.signin_google)
                    )
                    Text(
                        stringResource(id = R.string.signin_google)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { showEmailFields = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .width(242.dp)
                    .semantics {
                        contentDescription = "Bouton pour se connecter avec un email"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Email, null
                    )
                    Text(
                        stringResource(id = R.string.signin_email)
                    )
                }
            }
        }

        if (uiState is AuthUiState.Loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier.semantics {
                    contentDescription = "Chargement en cours"
                })
        }

        if (uiState is AuthUiState.Error) {
            Spacer(Modifier.height(16.dp))
            Text(
                (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics {
                    contentDescription = "Erreur : ${(uiState as AuthUiState.Error).message}"
                }
            )
        }
    }
}