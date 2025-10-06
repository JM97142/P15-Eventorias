package com.example.p15_eventorias.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.p15_eventorias.R
import com.example.p15_eventorias.repository.AuthUiState
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Launcher pour sélectionner une photo
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> photoUri = uri }

    // Si connexion réussie → on redirige
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Authenticated) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.arrow_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D1B20),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        // Écran de chargement
        if (uiState is AuthUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1D1B20))
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFF1D1B20))
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text("Choose Profile Picture")
            }

            // Preview photo
            photoUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    authViewModel.registerUser(name, email, password, photoUri)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(stringResource(id = R.string.register))
            }

            if (uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}