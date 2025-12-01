package ru.itis.android.homework_24112025.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ru.itis.android.homework_24112025.R
import ru.itis.android.homework_24112025.presentation.viewmodel.CoroutineConfig
import ru.itis.android.homework_24112025.presentation.viewmodel.CoroutineException
import ru.itis.android.homework_24112025.presentation.viewmodel.CoroutinesViewModel
import ru.itis.android.homework_24112025.presentation.viewmodel.ExceptionType
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoroutinesScreen(viewModel: CoroutinesViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var coroutineCount by remember { mutableStateOf(50f) }
    var selectedDispatcher by remember { mutableStateOf(DispatcherType.DEFAULT) }
    var isSequential by remember { mutableStateOf(true) }
    var isParallel by remember { mutableStateOf(false) }
    var isDeferred by remember { mutableStateOf(false) }
    var workInBackground by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val onSequentialChange = { value: Boolean ->
        if (value && !isParallel) {
            isSequential = true
        }
    }

    val onParallelChange = { value: Boolean ->
        if (value) {
            isSequential = false
            isParallel = true
        }
    }

    val onDisableSwitch = { switch: String ->
        when (switch) {
            "sequential" -> {
                isSequential = false
                isParallel = true
            }
            "parallel" -> {
                isParallel = false
                isSequential = true
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.coroutines_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.coroutine_count_label))
                        Text(
                            text = coroutineCount.toInt().toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Slider(
                        value = coroutineCount,
                        onValueChange = { coroutineCount = it },
                        valueRange = 10f..100f,
                        steps = 17,
                        enabled = !isLoading
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.dispatcher_label))
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDispatcher.name,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            enabled = !isLoading
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DispatcherType.values().forEach { dispatcher ->
                                DropdownMenuItem(
                                    text = { Text(dispatcher.name) },
                                    onClick = {
                                        selectedDispatcher = dispatcher
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.sequential_label))
                        Switch(
                            checked = isSequential,
                            onCheckedChange = { value ->
                                if (value) {
                                    onSequentialChange(value)
                                } else {
                                    onDisableSwitch("sequential")
                                }
                            },
                            enabled = !isLoading
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.parallel_label))
                        Switch(
                            checked = isParallel,
                            onCheckedChange = { value ->
                                if (value) {
                                    onParallelChange(value)
                                } else {
                                    onDisableSwitch("parallel")
                                }
                            },
                            enabled = !isLoading
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.deferred_label))
                        Switch(
                            checked = isDeferred,
                            onCheckedChange = { isDeferred = it },
                            enabled = !isLoading
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.background_work_label))
                        Switch(
                            checked = workInBackground,
                            onCheckedChange = { workInBackground = it },
                            enabled = !isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(stringResource(R.string.executing_label))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )

                        Button(
                            onClick = {
                                val cancelledCount = viewModel.cancelAllJobs()
                                isLoading = false

                                Toast.makeText(
                                    context,
                                    context.getString(R.string.cancelled_message, cancelledCount),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.cancel_label))
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            try {
                                val config = CoroutineConfig(
                                    count = coroutineCount.toInt(),
                                    dispatcher = selectedDispatcher.dispatcher,
                                    isSequential = isSequential,
                                    isParallel = isParallel,
                                    isDeferred = isDeferred,
                                    workInBackground = workInBackground
                                )

                                val exceptions = viewModel.executeCoroutines(config)

                                handleExceptions(
                                    exceptions,
                                    snackbarHostState,
                                    scope,
                                    context,
                                    { isSequential = true; isParallel = false }
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.launch_label))
                }
            }
        }
    }
}

enum class DispatcherType {
    DEFAULT, IO, MAIN, UNCONFINED;

    val dispatcher: CoroutineDispatcher
        get() = when (this) {
            DEFAULT -> Dispatchers.Default
            IO -> Dispatchers.IO
            MAIN -> Dispatchers.Main
            UNCONFINED -> Dispatchers.Unconfined
        }
}

private suspend fun handleExceptions(
    exceptions: List<CoroutineException>,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    resetDefaults: () -> Unit
) {
    exceptions.forEach { exception ->
        when (exception.type) {
            ExceptionType.NETWORK -> {
                val message = context.getString(R.string.network_error)
                snackbarHostState.showSnackbar(message)
            }
            ExceptionType.TIMEOUT -> {
                val message = context.getString(R.string.timeout_error)
                snackbarHostState.showSnackbar(message)
            }
            ExceptionType.PROCESSING -> {
                resetDefaults()
                val message = context.getString(R.string.processing_error)
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}