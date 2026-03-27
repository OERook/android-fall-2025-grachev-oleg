package ru.itis.android.homework_16122025.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.itis.android.homework_16122025.db.dao.SongDao
import ru.itis.android.homework_16122025.db.dao.UserDao
import ru.itis.android.homework_16122025.db.entity.SongEntity
import ru.itis.android.homework_16122025.db.entity.UserEntity

private const val DATABASE_VERSION = 2

@Database(
    entities = [UserEntity::class, SongEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class MusicBoxDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val songDao: SongDao
}