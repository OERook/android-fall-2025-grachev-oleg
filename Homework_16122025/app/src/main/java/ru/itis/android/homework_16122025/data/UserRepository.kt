package ru.itis.android.homework_16122025.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.itis.android.homework_16122025.db.MusicBoxDatabase
import ru.itis.android.homework_16122025.db.entity.SongEntity
import ru.itis.android.homework_16122025.db.entity.UserEntity
import ru.itis.android.homework_16122025.model.SongDataModel

class UserRepository(private val database: MusicBoxDatabase) {

    suspend fun registerUser(email: String, username: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val existingUser = database.userDao.getUserByEmail(email)
                if (existingUser != null && !existingUser.isDeleted) {
                    android.util.Log.e("UserRepository", "User with email $email already exists")
                    return@withContext false
                }

                val newUser = UserEntity(
                    email = email,
                    username = username,
                    passwordHash = password
                )

                val userId = database.userDao.insertUser(newUser)
                val success = userId > 0
                if (!success) {
                    android.util.Log.e("UserRepository", "Failed to insert user: userId = $userId")
                }
                success
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Error registering user", e)
                e.printStackTrace()
                false
            }
        }

    suspend fun loginUser(email: String, password: String): UserEntity? =
        withContext(Dispatchers.IO) {
            try {
                val user = database.userDao.getUserByEmail(email)
                if (user != null && user.passwordHash == password && !user.isDeleted) {
                    user
                } else {
                    null
                }

            } catch (e: Exception) {
                null
            }
        }

    suspend fun getUserById(userId: Int): UserEntity? =
        withContext(Dispatchers.IO) {
            database.userDao.getUserById(userId)
        }

    suspend fun updateUser(user: UserEntity) =
        withContext(Dispatchers.IO) {
            database.userDao.updateUser(user)
        }

    suspend fun softDeleteUser(userId: Int) =
        withContext(Dispatchers.IO) {
            database.userDao.softDeleteUser(userId, System.currentTimeMillis())
        }

    suspend fun restoreUser(userId: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val deletionTime = database.userDao.getDeletionTime(userId)
                if (deletionTime != null) {
                    val daysSinceDeletion = (System.currentTimeMillis() - deletionTime) / (1000 * 60 * 60 * 24)
                    if (daysSinceDeletion > 7) {
                        return@withContext false
                    }
                }
                database.userDao.restoreUser(userId)
                true
            } catch (e: Exception) {
                false
            }
        }

    suspend fun getDeletionTime(userId: Int): Long? =
        withContext(Dispatchers.IO) {
            try {
                database.userDao.getDeletionTime(userId)
            } catch (e: Exception) {
                null
            }
        }

    suspend fun hardDeleteUser(userId: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                database.userDao.hardDeleteUser(userId, cutoffTime)
                true
            } catch (e: Exception) {
                false
            }
        }

    suspend fun getDeletedUserByEmail(email: String): UserEntity? =
        withContext(Dispatchers.IO) {
            try {
                database.userDao.getDeletedUserByEmail(email)
            } catch (e: Exception) {
                null
            }
        }

    suspend fun cleanupExpiredAccounts() =
        withContext(Dispatchers.IO) {
            try {
                val cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                database.userDao.deleteExpiredAccounts(cutoffTime)
            } catch (e: Exception) {
            }
        }

    suspend fun clearAllData() =
        withContext(Dispatchers.IO) {
            try {
                database.songDao.deleteAllSongs()
                database.userDao.deleteAllUsers()
            } catch (e: Exception) {
            }
        }

    suspend fun addSong(song: SongDataModel, userId: Int) =
        withContext(Dispatchers.IO) {
            try {
                val songEntity = SongEntity(
                    title = song.title,
                    artist = song.artist,
                    album = song.album,
                    duration = song.duration,
                    genre = song.genre,
                    releaseYear = song.releaseYear,
                    rating = song.rating,
                    coverImage = song.coverImage,
                    userId = userId
                )

                database.songDao.insertSong(songEntity)
                true
            } catch (e: Exception) {
                false
            }
        }

    enum class SortType {
        NEWEST, TITLE, ARTIST, RATING, YEAR, DURATION
    }

    fun getSongs(userId: Int, sortType: SortType = SortType.NEWEST): Flow<List<SongDataModel>> =
        flow {
            try {
                val songsFlow = when (sortType) {
                    SortType.NEWEST -> database.songDao.getUserSongs(userId)
                    SortType.TITLE -> database.songDao.getUserSongsSortedByTitle(userId)
                    SortType.YEAR -> database.songDao.getUserSongsSortedByYear(userId)
                    SortType.ARTIST -> database.songDao.getUserSongsSortedByArtist(userId)
                    SortType.RATING -> database.songDao.getUserSongsSortedByRating(userId)
                    SortType.DURATION -> database.songDao.getUserSongsSortedByDuration(userId)
                }

                songsFlow.collect { songs ->
                    emit(songs.map { it.toDataModel() })
                }

            } catch (e: Exception) {
                emit(emptyList())
            }
        }

    suspend fun getSongsCount(userId: Int): Int =
        withContext(Dispatchers.IO) {
            try {
                database.songDao.getAllUserSongsCount(userId)
            } catch (e: Exception) {
                0
            }
        }

    private fun SongEntity.toDataModel() = SongDataModel(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        genre = genre,
        releaseYear = releaseYear,
        rating = rating,
        coverImage = coverImage
    )
}