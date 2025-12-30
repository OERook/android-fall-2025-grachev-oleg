package ru.itis.android.homework_16122025.di

import android.content.Context
import androidx.room.Room
import ru.itis.android.homework_16122025.data.UserRepository
import ru.itis.android.homework_16122025.db.MusicBoxDatabase
import ru.itis.android.homework_16122025.utils.SessionManager

object ServiceLocator {
    private const val DB_NAME = "musicbox.db"
    private var database: MusicBoxDatabase? = null
    private var repository: UserRepository? = null
    private var sessionManager: SessionManager? = null

    fun initDatabase(appCtx: Context) {
        try {
            if (database == null) {
                database = Room.databaseBuilder(
                    appCtx,
                    MusicBoxDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            if (repository == null) {
                repository = UserRepository(database!!)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize database", e)
        }
    }

    fun initSessionManager(context: Context) {
        if (sessionManager == null) {
            sessionManager = SessionManager(context)
        }
    }

    fun getDatabase(): MusicBoxDatabase =
        database ?: throw IllegalStateException("Database not initialized. Call initDatabase() first.")

    fun getRepository(): UserRepository =
        repository ?: throw IllegalStateException("Repository not initialized. Call initDatabase() first.")

    fun getSessionManager(): SessionManager =
        sessionManager ?: throw IllegalStateException("SessionManager not initialized. Call initSessionManager() first.")
}