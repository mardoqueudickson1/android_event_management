package com.example.school.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.school.room_database.db.AppDatabase
import com.example.school.room_database.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao = AppDatabase.getDatabase(application).eventDao()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    fun loadEvents() {
        viewModelScope.launch {
            _events.value = eventDao.getAllEvents()
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event)
            loadEvents()
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event)
            loadEvents()
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventDao.delete(event)
            loadEvents()
        }
    }

    // Buscar eventos com base em uma consulta
    fun searchEvents(query: String) {
        viewModelScope.launch {
            _events.value = eventDao.searchEvents(query) // Atualiza o StateFlow com os resultados da pesquisa
        }
    }
}
