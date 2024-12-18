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

    // StateFlow para armazenar a lista de eventos
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    // Carregar os eventos do banco de dados
    fun loadEvents() {
        viewModelScope.launch {
            _events.value = eventDao.getAllEvents() // Atualiza o StateFlow com os eventos do banco de dados
        }
    }

    // Adicionar um novo evento
    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event)
            loadEvents()  // Recarrega a lista de eventos após a inserção
        }
    }

    // Atualizar um evento (o método insert também pode ser usado para atualizar)
    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event) // Usa insert para atualizar o evento
            loadEvents()  // Recarrega a lista de eventos após a atualização
        }
    }

    // Deletar um evento
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventDao.delete(event)
            loadEvents()  // Recarrega a lista de eventos após a exclusão
        }
    }

    // Buscar eventos com base em uma consulta
    fun searchEvents(query: String) {
        viewModelScope.launch {
            _events.value = eventDao.searchEvents(query) // Atualiza o StateFlow com os resultados da pesquisa
        }
    }
}
