@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoSalaRemotaScreen(
    listaRemotas: List<AsistenciaRemota>,
    onFinalizarReunion: (AsistenciaRemota) -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sala de Asistencia Remota") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (listaRemotas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reuniones o asistencias remotas programadas.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaRemotas) { remota ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Cliente: ${remota.clienteNombre}", fontWeight = FontWeight.Bold)
                                Text("Fecha: ${remota.fecha} | Bloque: ${remota.bloqueHorario}")
                                Text("Motivo: ${remota.motivo}")
                                Text("Enlace Reunión: ${remota.enlaceReunion.ifBlank { "Sin Enlace Generado" }}")
                                Text("Estado: ${remota.estado}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                if (remota.estado != "Finalizada" && remota.estado != "Atendida") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onFinalizarReunion(remota.copy(estado = "Atendida"))
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Marcar Asistencia como Atendida")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}