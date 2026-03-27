package ru.itis.android.homework_16122025.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.itis.android.homework_16122025.R
import ru.itis.android.homework_16122025.data.UserRepository
import ru.itis.android.homework_16122025.navigation.LoginScreenObject
import ru.itis.android.homework_16122025.navigation.MusicListScreenObject
import ru.itis.android.homework_16122025.utils.SessionManager
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(
    navController: NavHostController,
    userId: Int,
    sessionManager: SessionManager,
    repository: UserRepository
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var timeSinceDeletion by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var canRestore by remember { mutableStateOf(true) }
    var daysRemaining by remember { mutableStateOf(0) }
    val accountRestoredText = stringResource(R.string.account_restored)
    val recoveryExpiredText = stringResource(R.string.recovery_expired)
    val accountPermanentlyDeletedText = stringResource(R.string.account_permanently_deleted)
    val errorOccurredText = stringResource(R.string.error_occurred)
    val justNowText = stringResource(R.string.just_now)

    LaunchedEffect(Unit) {
        val deletionTime = repository.getDeletionTime(userId)
        if (deletionTime != null) {
            val daysSinceDeletion = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - deletionTime)
            daysRemaining = (7 - daysSinceDeletion).toInt()
            canRestore = daysRemaining > 0
            timeSinceDeletion = formatTimeSinceDeletion(deletionTime, context, justNowText)
        } else {
            timeSinceDeletion = justNowText
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.recovery_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.recovery_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.recovery_message, timeSinceDeletion),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!canRestore) {
                Text(
                    text = stringResource(R.string.recovery_expired),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else if (daysRemaining > 0) {
                Text(
                    text = stringResource(R.string.recovery_days_remaining, daysRemaining),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                val success = repository.restoreUser(userId)
                                if (success) {
                                    val user = repository.getUserById(userId)
                                    user?.let {
                                        sessionManager.saveSession(it.id, it.email, it.username)
                                    }
                                    successMessage = accountRestoredText
                                    showSuccessDialog = true
                                } else {
                                    errorMessage = recoveryExpiredText
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = canRestore && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.restore_account_button))
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                val success = repository.hardDeleteUser(userId)
                                if (success) {
                                    sessionManager.clearSession()
                                    successMessage = accountPermanentlyDeletedText
                                    showSuccessDialog = true
                                } else {
                                    errorMessage = errorOccurredText
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.permanent_delete_button))
                    }
                }
            }
        }
    }

    val recoveryTitleText = stringResource(R.string.recovery_title)
    val deleteAccountDialogTitleText = stringResource(R.string.delete_account_dialog_title)
    val confirmButtonText = stringResource(R.string.confirm_button)

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                if (successMessage == accountRestoredText) {
                    navController.navigate(MusicListScreenObject.route) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    navController.navigate(LoginScreenObject.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            title = {
                Text(
                    text = if (successMessage == accountRestoredText)
                        recoveryTitleText
                    else
                        deleteAccountDialogTitleText
                )
            },
            text = {
                Text(text = successMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        if (successMessage == accountRestoredText) {
                            navController.navigate(MusicListScreenObject.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(LoginScreenObject.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(confirmButtonText)
                }
            }
        )
    }
}

private fun formatTimeSinceDeletion(
    deletionTime: Long?,
    context: android.content.Context,
    justNowText: String
): String {
    if (deletionTime == null) return justNowText

    val diff = System.currentTimeMillis() - deletionTime
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

    return when {
        days > 0 -> context.getString(R.string.days_ago, days.toInt())
        hours > 0 -> context.getString(R.string.hours_ago, hours.toInt())
        minutes > 0 -> context.getString(R.string.minutes_ago, minutes.toInt())
        else -> justNowText
    }
}