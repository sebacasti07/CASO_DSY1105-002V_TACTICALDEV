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
fun AdminVisitaRemotaScreen(
    listaRemotas: List<AsistenciaRemota>,
    onActualizarRemota: (AsistenciaRemota) -> Unit,
    onVolver: () -> Unit
) {
    var remotaAEditar by remember { mutableStateOf<AsistenciaRemota?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión Visitas Remotas (Admin)") },
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
                    Text("No hay solicitudes de asistencia remota.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaRemotas) { remota ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("ID: ${remota.asistenciaId}", fontWeight = FontWeight.Bold)
                                Text("Cliente: ${remota.clienteNombre}")
                                Text("Fecha: ${remota.fecha} | Bloque: ${remota.bloqueHorario}")
                                Text("Motivo: ${remota.motivo}")
                                Text("Enlace Reunión: ${remota.enlaceReunion.ifBlank { "No asignado" }}")
                                Text("Estado: ${remota.estado}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { remotaAEditar = remota },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Asignar Enlace / Estado")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    remotaAEditar?.let { remota ->
        var nuevoEnlace by remember { mutableStateOf(remota.enlaceReunion) }
        var nuevoEstado by remember { mutableStateOf(remota.estado) }

        AlertDialog(
            onDismissRequest = { remotaAEditar = null },
            title = { Text("Gestionar Asistencia Remota") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nuevoEnlace,
                        onValueChange = { nuevoEnlace = it },
                        label = { Text("Enlace de Reunión (Meet/Teams)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoEstado,
                        onValueChange = { nuevoEstado = it },
                        label = { Text("Estado (Pendiente, Programada, Finalizada)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onActualizarRemota(
                        remota.copy(
                            enlaceReunion = nuevoEnlace.trim(),
                            estado = nuevoEstado.trim()
                        )
                    )
                    remotaAEditar = null
                }) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { remotaAEditar = null }) { Text("Cancelar") }
            }
        )
    }
}