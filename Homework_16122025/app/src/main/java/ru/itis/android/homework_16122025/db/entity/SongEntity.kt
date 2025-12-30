package ru.itis.android.homework_16122025.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.itis.android.homework_16122025.db.entity.UserEntity

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "artist")
    val artist: String,

    @ColumnInfo(name = "album")
    val album: String,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "genre")
    val genre: String,

    @ColumnInfo(name = "release_year")
    val releaseYear: Int,

    @ColumnInfo(name = "rating")
    val rating: Float = 0f,

    @ColumnInfo(name = "cover_image")
    val coverImage: String = "",

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
