package com.example.school.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.school.ui.viewmodel.EventViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.school.room_database.Event

@Composable
fun EventDetailScreen(viewModel: EventViewModel = viewModel(), eventId: Int? = null) {
    // Coleta os eventos do ViewModel
    val events by viewModel.events.collectAsState()

    // Filtra o evento com base no ID
    val event = events.find { it.id == eventId }

    // Caso o evento seja encontrado, preenche os campos com os dados do evento
    var name by remember { mutableStateOf(event?.name ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var startDate by remember { mutableStateOf(event?.startDate ?: "") }
    var startTime by remember { mutableStateOf(event?.startTime ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Evento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Data de Início") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Hora de Início") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Localização") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val newEvent = Event(
                id = eventId ?: 0, name = name, description = description, startDate = startDate,
                startTime = startTime, location = location
            )
            if (eventId != null) {
                viewModel.updateEvent(newEvent)  // Edita o evento
            } else {
                viewModel.addEvent(newEvent)    // Adiciona um novo evento
            }
        }) {
            Text("Salvar Evento")
        }
    }
}

@Preview
@Composable
fun PreviewEventDetailScreen() {
    EventDetailScreen(eventId = 1)
}
