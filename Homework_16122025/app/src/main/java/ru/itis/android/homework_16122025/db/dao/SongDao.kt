package ru.itis.android.homework_16122025.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.itis.android.homework_16122025.db.entity.SongEntity

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("select * from songs where user_id = :userId order by created_at desc")
    fun getUserSongs(userId: Int): Flow<List<SongEntity>>

    @Query("select * from songs where user_id = :userId order by title asc")
    fun getUserSongsSortedByTitle(userId: Int): Flow<List<SongEntity>>

    @Query("select * from songs where user_id = :userId order by artist asc")
    fun getUserSongsSortedByArtist(userId: Int): Flow<List<SongEntity>>

    @Query("select * from songs where user_id = :userId order by rating desc")
    fun getUserSongsSortedByRating(userId: Int): Flow<List<SongEntity>>

    @Query("select * from songs where user_id = :userId order by release_year desc")
    fun getUserSongsSortedByYear(userId: Int): Flow<List<SongEntity>>

    @Query("select * from songs where user_id = :userId order by duration asc")
    fun getUserSongsSortedByDuration(userId: Int): Flow<List<SongEntity>>

    @Query("select * from songs where id = :songId limit 1")
    suspend fun getSongById(songId: Int): SongEntity?

    @Update
    suspend fun updateSong(song: SongEntity)

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("delete from songs where user_id = :userId")
    suspend fun deleteAllUserSongs(userId: Int)

    @Query("select count(*) from songs where user_id = :userId")
    suspend fun getAllUserSongsCount(userId: Int): Int

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}
