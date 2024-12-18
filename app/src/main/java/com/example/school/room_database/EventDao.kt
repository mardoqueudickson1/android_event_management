package com.example.school.room_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface EventDao {

    // Inserir um novo evento
    @Insert
    suspend fun insert(event: Event)

    // Obter todos os eventos, ordenados por data
    @Query("SELECT * FROM event_table ORDER BY startDate ASC")
    suspend fun getAllEvents(): List<Event>

    // Deletar um evento
    @Delete
    suspend fun delete(event: Event)

    // Buscar eventos por nome ou data
    @Query("SELECT * FROM event_table WHERE name LIKE :query OR startDate LIKE :query")
    suspend fun searchEvents(query: String): List<Event>
}
