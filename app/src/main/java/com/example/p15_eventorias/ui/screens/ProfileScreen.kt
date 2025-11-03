package com.example.p15_eventorias.ui.screens

import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.p15_eventorias.R
import com.example.p15_eventorias.ui.viewmodels.AuthViewModel
import com.example.p15_eventorias.ui.viewmodels.NotificationsViewModel
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    user: FirebaseUser?,
    authViewModel: AuthViewModel,
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    onEventsList: () -> Unit,
    onLogout: () -> Unit,
    isTest: Boolean = false
) {
    val notificationsEnabled by notificationsViewModel.notificationsEnabled.collectAsState()

    var userName by remember { mutableStateOf(user?.displayName ?: "") }
    var userPhotoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            authViewModel.getUserByUid(uid) { url -> userPhotoUrl = url }
            authViewModel.getUserNameByUid(uid) { name -> if (!name.isNullOrBlank()) userName = name }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handleNotificationPermission(isGranted, notificationsViewModel)
    }

    Scaffold(
        modifier = Modifier.semantics { contentDescription = "Écran de profil utilisateur" },
        topBar = { ProfileTopBar(userPhotoUrl) },
        bottomBar = { ProfileBottomBar(onEventsList) }
    ) { padding ->
        ProfileContent(
            userName = userName,
            userEmail = user?.email,
            notificationsEnabled = notificationsEnabled,
            permissionLauncher = permissionLauncher,
            notificationsViewModel = notificationsViewModel,
            onLogout = {
                authViewModel.signOut()
                onLogout()
            },
            isTest = isTest,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1D1B20))
                .padding(padding)
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(userPhotoUrl: String?) {
    TopAppBar(
        title = { Text(stringResource(R.string.profil), color = Color.White) },
        actions = {
            AsyncImage(
                model = userPhotoUrl,
                contentDescription = stringResource(R.string.profil_picture),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1D1B20),
            titleContentColor = Color.White
        )
    )
}

@Composable
private fun ProfileBottomBar(onEventsList: () -> Unit) {
    NavigationBar(containerColor = Color(0xFF1D1B20), contentColor = Color.White) {
        NavigationBarItem(
            selected = false,
            onClick = onEventsList,
            label = { Text("Events") },
            icon = { Icon(Icons.Default.DateRange, null) }
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            label = { Text("Profile") },
            icon = { Icon(Icons.Default.Person, null) }
        )
    }
}

@Composable
private fun ProfileContent(
    userName: String,
    userEmail: String?,
    notificationsEnabled: Boolean,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    notificationsViewModel: NotificationsViewModel,
    onLogout: () -> Unit,
    isTest: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DisabledTextField(label = stringResource(R.string.user_name), value = userName)
        DisabledTextField(label = stringResource(R.string.email), value = userEmail ?: "")

        LogoutButton(onLogout = onLogout)

        NotificationRow(
            notificationsEnabled = notificationsEnabled,
            permissionLauncher = permissionLauncher,
            notificationsViewModel = notificationsViewModel,
            isTest = isTest
        )
    }
}

@Composable
private fun DisabledTextField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.White,
            disabledLabelColor = Color.White,
            disabledIndicatorColor = Color.Gray,
            disabledContainerColor = Color.DarkGray
        )
    )
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
        shape = RoundedCornerShape(3.dp)
    ) {
        Text(stringResource(id = R.string.logout))
    }
}

@Composable
private fun NotificationRow(
    notificationsEnabled: Boolean,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    notificationsViewModel: NotificationsViewModel,
    isTest: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { isChecked ->
                handleNotificationToggle(isChecked, notificationsViewModel, permissionLauncher, isTest)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Red
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(id = R.string.notifications), color = Color.White)
    }
}

private fun handleNotificationPermission(
    isGranted: Boolean,
    notificationsViewModel: NotificationsViewModel
) {
    if (isGranted) notificationsViewModel.enableNotifications()
    else notificationsViewModel.disableNotifications()
}

private fun handleNotificationToggle(
    isChecked: Boolean,
    notificationsViewModel: NotificationsViewModel,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    isTest: Boolean
) {
    if (isChecked) {
        if (!isTest && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificationsViewModel.enableNotifications()
        }
    } else {
        notificationsViewModel.disableNotifications()
    }
}