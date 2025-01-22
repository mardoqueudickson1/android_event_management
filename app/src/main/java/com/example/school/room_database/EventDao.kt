package com.example.school.room_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: Event)

    @Query("SELECT * FROM event_table ORDER BY startDate ASC")
    suspend fun getAllEvents(): List<Event>

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM event_table WHERE name LIKE :query OR startDate LIKE :query")
    suspend fun searchEvents(query: String): List<Event>
}
