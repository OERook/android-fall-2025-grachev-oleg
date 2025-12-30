package ru.itis.android.homework_16122025.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.itis.android.homework_16122025.db.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("SELECT * FROM users WHERE is_deleted = 0 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users WHERE is_deleted = 0")
    fun getAllActiveUsers(): Flow<List<UserEntity>>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET is_deleted = 1, deleted_at = :deletedAt WHERE id = :userId")
    suspend fun softDeleteUser(userId: Int, deletedAt: Long)

    @Query("UPDATE users SET is_deleted = 0, deleted_at = NULL WHERE id = :userId")
    suspend fun restoreUser(userId: Int)

    @Query("DELETE FROM users WHERE id = :userId AND deleted_at <= :cutoffTime")
    suspend fun hardDeleteUser(userId: Int, cutoffTime: Long)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT deleted_at FROM users WHERE id = :userId")
    suspend fun getDeletionTime(userId: Int): Long?

    @Query("SELECT * FROM users WHERE email = :email AND is_deleted = 1 LIMIT 1")
    suspend fun getDeletedUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE is_deleted = 1")
    suspend fun getAllDeletedUsers(): List<UserEntity>

    @Query("DELETE FROM users WHERE is_deleted = 1 AND deleted_at <= :cutoffTime")
    suspend fun deleteExpiredAccounts(cutoffTime: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

