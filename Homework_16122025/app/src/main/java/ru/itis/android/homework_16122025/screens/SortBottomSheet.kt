package ru.itis.android.homework_16122025.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.itis.android.homework_16122025.R
import ru.itis.android.homework_16122025.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    navController: NavHostController,
    currentSortType: UserRepository.SortType = UserRepository.SortType.NEWEST
) {
    val selectedSort = remember { mutableStateOf(currentSortType) }
    
    val onSortSelected: (UserRepository.SortType) -> Unit = { sortType ->
        navController.previousBackStackEntry?.savedStateHandle?.set("sortType", sortType)
    }

    ModalBottomSheet(
        onDismissRequest = { navController.popBackStack() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.sort_button),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SortOption(
                title = stringResource(R.string.sort_newest),
                selected = selectedSort.value == UserRepository.SortType.NEWEST,
                onClick = {
                    selectedSort.value = UserRepository.SortType.NEWEST
                    onSortSelected(UserRepository.SortType.NEWEST)
                    navController.popBackStack()
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            SortOption(
                title = stringResource(R.string.sort_title),
                selected = selectedSort.value == UserRepository.SortType.TITLE,
                onClick = {
                    selectedSort.value = UserRepository.SortType.TITLE
                    onSortSelected(UserRepository.SortType.TITLE)
                    navController.popBackStack()
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            SortOption(
                title = stringResource(R.string.sort_artist),
                selected = selectedSort.value == UserRepository.SortType.ARTIST,
                onClick = {
                    selectedSort.value = UserRepository.SortType.ARTIST
                    onSortSelected(UserRepository.SortType.ARTIST)
                    navController.popBackStack()
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            SortOption(
                title = stringResource(R.string.sort_rating),
                selected = selectedSort.value == UserRepository.SortType.RATING,
                onClick = {
                    selectedSort.value = UserRepository.SortType.RATING
                    onSortSelected(UserRepository.SortType.RATING)
                    navController.popBackStack()
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            SortOption(
                title = stringResource(R.string.sort_year),
                selected = selectedSort.value == UserRepository.SortType.YEAR,
                onClick = {
                    selectedSort.value = UserRepository.SortType.YEAR
                    onSortSelected(UserRepository.SortType.YEAR)
                    navController.popBackStack()
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            SortOption(
                title = stringResource(R.string.sort_duration),
                selected = selectedSort.value == UserRepository.SortType.DURATION,
                onClick = {
                    selectedSort.value = UserRepository.SortType.DURATION
                    onSortSelected(UserRepository.SortType.DURATION)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun SortOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}