package com.example.school.room_database
import androidx.room.*


@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Delete
    suspend fun delete(user: User)
}