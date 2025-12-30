package ru.itis.android.homework_16122025.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itis.android.homework_16122025.R
import ru.itis.android.homework_16122025.data.UserRepository
import ru.itis.android.homework_16122025.model.SongDataModel
import ru.itis.android.homework_16122025.utils.ImageConstants
import ru.itis.android.homework_16122025.utils.ImageUtils
import ru.itis.android.homework_16122025.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
    repository: UserRepository
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var album by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var coverImageBase64 by remember { mutableStateOf("") }
    var coverBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userId = sessionManager.getUserId()

    val allFieldsRequiredText = stringResource(R.string.all_fields_required)
    val durationMustBeNumberText = stringResource(R.string.duration_must_be_number)
    val yearMustBeNumberText = stringResource(R.string.year_must_be_number)
    val ratingMustBeNumberText = stringResource(R.string.rating_must_be_number)
    val ratingMustBeBetweenText = stringResource(R.string.rating_must_be_between)
    val songAddedText = stringResource(R.string.song_added)
    val errorOccurredText = stringResource(R.string.error_occurred)
    val imageLoadErrorText = stringResource(R.string.image_load_error)
    val songCoverDescription = stringResource(R.string.song_cover_description)
    val addCoverDescription = stringResource(R.string.add_cover_description)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isProcessingImage = true
                errorMessage = ""
                
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val decodedBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        decodedBitmap
                    }

                    if (bitmap != null) {
                        val scaledBitmap = ImageUtils.scaleBitmap(
                            bitmap,
                            ImageConstants.MAX_COVER_WIDTH,
                            ImageConstants.MAX_COVER_HEIGHT
                        )

                        val base64String = ImageUtils.encodeBitmapToBase64(scaledBitmap)
                        
                        if (base64String != null) {
                            coverImageBase64 = base64String
                            coverBitmap = scaledBitmap
                            errorMessage = ""
                        } else {
                            errorMessage = imageLoadErrorText
                            coverBitmap = null
                        }
                    } else {
                        errorMessage = imageLoadErrorText
                        coverBitmap = null
                    }
                } catch (e: Exception) {
                    errorMessage = imageLoadErrorText
                    coverBitmap = null
                } finally {
                    isProcessingImage = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_song_button),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    val coverInteractionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ImageConstants.ADD_COVER_HEIGHT.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable(
                                interactionSource = coverInteractionSource,
                                indication = null,
                                enabled = !isProcessingImage
                            ) { 
                                imagePickerLauncher.launch(ImageConstants.IMAGE_MIME_TYPE) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isProcessingImage -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            coverBitmap != null -> {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(ImageConstants.ADD_COVER_HEIGHT.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    androidx.compose.foundation.Image(
                                        bitmap = coverBitmap!!.asImageBitmap(),
                                        contentDescription = songCoverDescription,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = addCoverDescription,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.add_cover),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.title_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = artist,
                        onValueChange = {
                            artist = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.artist_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = album,
                        onValueChange = {
                            album = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.album_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = duration,
                        onValueChange = {
                            duration = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.duration_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = genre,
                        onValueChange = {
                            genre = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.genre_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = releaseYear,
                        onValueChange = {
                            releaseYear = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.year_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = rating,
                        onValueChange = {
                            rating = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.rating_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = !isLoading
                        ) {
                            Text(stringResource(R.string.cancel_button))
                        }

                        Button(
                            onClick = {
                                when {
                                    title.isEmpty() || artist.isEmpty() ||
                                            album.isEmpty() || duration.isEmpty() ||
                                            genre.isEmpty() || releaseYear.isEmpty() ||
                                            rating.isEmpty() -> {
                                        errorMessage = allFieldsRequiredText
                                    }

                                    duration.toIntOrNull() == null -> {
                                        errorMessage = durationMustBeNumberText
                                    }

                                    releaseYear.toIntOrNull() == null -> {
                                        errorMessage = yearMustBeNumberText
                                    }

                                    rating.toFloatOrNull() == null -> {
                                        errorMessage = ratingMustBeNumberText
                                    }

                                    rating.toFloat() < 0f || rating.toFloat() > 10f -> {
                                        errorMessage = ratingMustBeBetweenText
                                    }

                                    else -> {
                                        isLoading = true

                                        scope.launch {
                                            val song = SongDataModel(
                                                title = title,
                                                artist = artist,
                                                album = album,
                                                duration = duration.toInt(),
                                                genre = genre,
                                                releaseYear = releaseYear.toInt(),
                                                rating = rating.toFloat(),
                                                coverImage = coverImageBase64
                                            )

                                            val success = repository.addSong(song, userId)

                                            if (success) {
                                                successMessage = songAddedText
                                                snackbarHostState.showSnackbar(songAddedText)
                                                isLoading = false
                                                navController.popBackStack()
                                            } else {
                                                errorMessage = errorOccurredText
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = !isLoading && !isProcessingImage
                        ) {
                            Text(stringResource(R.string.add_button))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
