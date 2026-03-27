package ru.itis.android.homework_16122025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.itis.android.homework_16122025.R
import ru.itis.android.homework_16122025.data.UserRepository
import ru.itis.android.homework_16122025.navigation.MusicListScreenObject
import ru.itis.android.homework_16122025.navigation.RegisterScreenObject
import ru.itis.android.homework_16122025.utils.SessionManager
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
    repository: UserRepository
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val allFieldsRequiredText = stringResource(R.string.all_fields_required)
    val invalidInputText = stringResource(R.string.invalid_input)
    val accountPermanentlyDeletedText = stringResource(R.string.account_permanently_deleted)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading.value
            )

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                visualTransformation = if (passwordVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(
                            imageVector = if (passwordVisible.value) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(R.string.password_label)
                        )
                    }
                },
                enabled = !isLoading.value
            )

            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Button(
                    onClick = {
                        if (email.value.isEmpty() || password.value.isEmpty()) {
                            errorMessage.value = allFieldsRequiredText
                        } else {
                            isLoading.value = true
                            scope.launch {
                                val user = repository.loginUser(email.value, password.value)
                                if (user != null) {
                                    sessionManager.saveSession(user.id, user.email, user.username)
                                    navController.navigate(MusicListScreenObject.route) {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    val deletedUser = repository.getDeletedUserByEmail(email.value)
                                    if (deletedUser != null && deletedUser.passwordHash == password.value) {
                                        val deletionTime = repository.getDeletionTime(deletedUser.id)
                                        if (deletionTime != null) {
                                            val daysSinceDeletion = (System.currentTimeMillis() - deletionTime) / (1000 * 60 * 60 * 24)
                                            if (daysSinceDeletion <= 7) {
                                                navController.navigate("recovery/${deletedUser.id}")
                                            } else {
                                                errorMessage.value = accountPermanentlyDeletedText
                                            }
                                        } else {
                                            errorMessage.value = invalidInputText
                                        }
                                    } else {
                                        errorMessage.value = invalidInputText
                                    }
                                }
                                isLoading.value = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(stringResource(R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val loginInteractionSource = remember { MutableInteractionSource() }
            Text(
                text = stringResource(R.string.no_account),
                modifier = Modifier
                    .clickable(
                        interactionSource = loginInteractionSource,
                        indication = null
                    ) {
                        navController.navigate(RegisterScreenObject.route)
                    }
                    .padding(8.dp),
                color = Color.Blue
            )
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
    repository: UserRepository
) {
    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val allFieldsRequiredText = stringResource(R.string.all_fields_required)
    val passwordsNotMatchText = stringResource(R.string.passwords_not_match)
    val passwordMinLengthText = stringResource(R.string.password_min_length)
    val errorOccurredText = stringResource(R.string.error_occurred)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.register_title),
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 40.dp, top = 40.dp)
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading.value
            )

            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text(stringResource(R.string.username_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !isLoading.value
            )

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = if (passwordVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                enabled = !isLoading.value
            )

            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text(stringResource(R.string.confirm_password_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                visualTransformation = if (passwordVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                enabled = !isLoading.value
            )

            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Button(
                    onClick = {
                        when {
                            email.value.isEmpty() || username.value.isEmpty() ||
                                    password.value.isEmpty() || confirmPassword.value.isEmpty() ->
                                errorMessage.value = allFieldsRequiredText
                            password.value != confirmPassword.value ->
                                errorMessage.value = passwordsNotMatchText
                            password.value.length < 6 ->
                                errorMessage.value = passwordMinLengthText
                            else -> {
                                isLoading.value = true
                                scope.launch {
                                    val success = repository.registerUser(
                                        email.value,
                                        username.value,
                                        password.value
                                    )
                                    if (success) {
                                        val user = repository.loginUser(email.value, password.value)
                                        if (user != null) {
                                            sessionManager.saveSession(user.id, user.email, user.username)
                                            navController.navigate(MusicListScreenObject.route) {
                                                popUpTo(0) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    } else {
                                        errorMessage.value = errorOccurredText
                                    }
                                    isLoading.value = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(stringResource(R.string.register_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val registerInteractionSource = remember { MutableInteractionSource() }
            Text(
                text = stringResource(R.string.have_account),
                modifier = Modifier
                    .clickable(
                        interactionSource = registerInteractionSource,
                        indication = null
                    ) { 
                        navController.popBackStack() 
                    }
                    .padding(8.dp),
                color = Color.Blue
            )
        }
    }
}