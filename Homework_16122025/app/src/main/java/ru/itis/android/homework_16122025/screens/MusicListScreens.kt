package ru.itis.android.homework_16122025.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.itis.android.homework_16122025.R
import ru.itis.android.homework_16122025.data.UserRepository
import ru.itis.android.homework_16122025.model.SongDataModel
import ru.itis.android.homework_16122025.navigation.AddSongScreenObject
import ru.itis.android.homework_16122025.navigation.ProfileScreenObject
import ru.itis.android.homework_16122025.navigation.SortBottomSheetObject
import ru.itis.android.homework_16122025.utils.ImageConstants
import ru.itis.android.homework_16122025.utils.ImageUtils
import ru.itis.android.homework_16122025.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
    repository: UserRepository
) {
    val userId = sessionManager.getUserId()
    val username = sessionManager.getUsername() ?: ""
    val userEmail = sessionManager.getUserEmail() ?: ""

    var sortType by rememberSaveable { mutableStateOf(UserRepository.SortType.NEWEST) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(navController.currentBackStackEntry?.id) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.get<UserRepository.SortType>("sortType")?.let { newSortType ->
            sortType = newSortType
            savedStateHandle.remove<UserRepository.SortType>("sortType")
        }
    }

    val songsFlow = repository.getSongs(userId, sortType)
    val songs by songsFlow.collectAsState(initial = emptyList())

    LaunchedEffect(sortType) {
        isLoading = true
        delay(300)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.music_list_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("currentSortType", sortType)
                            navController.navigate("sort_bottom_sheet/${sortType.name}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sort,
                            contentDescription = stringResource(R.string.sort_button)
                        )
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                val songsCount = repository.getSongsCount(userId)
                                navController.navigate(
                                    ProfileScreenObject(
                                        username = username,
                                        email = userEmail,
                                        songsCount = songsCount
                                    ).createRoute()
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = stringResource(R.string.profile_button)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(AddSongScreenObject.route) },
                icon = { 
                    Icon(
                        Icons.Filled.Add, 
                        stringResource(R.string.add_song_icon_description)
                    ) 
                },
                text = { Text(stringResource(R.string.add_song_button)) }
            )
        }
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
            } else if (songs.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_songs),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(songs) { song ->
                        SongCard(song = song)
                    }
                }
            }
        }
    }
}

@Composable
fun SongCard(song: SongDataModel) {
    var coverBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    val songCoverDescription = stringResource(R.string.song_cover_description)
    val songIconDescription = stringResource(R.string.song_icon_description)
    val separator = stringResource(R.string.separator)

    LaunchedEffect(song.coverImage) {
        if (song.coverImage.isNotEmpty()) {
            coverBitmap = ImageUtils.decodeBase64ToBitmap(song.coverImage)
        } else {
            coverBitmap = null
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(ImageConstants.LIST_COVER_SIZE.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                if (coverBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = coverBitmap!!.asImageBitmap(),
                        contentDescription = songCoverDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(ImageConstants.LIST_COVER_SIZE.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = songIconDescription,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = song.album,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Text(
                        text = separator,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Text(
                        text = stringResource(
                            R.string.duration_format,
                            song.duration / 60,
                            song.duration % 60
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.rating_format, song.rating),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "${song.releaseYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
