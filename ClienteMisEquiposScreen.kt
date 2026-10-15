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
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteMisEquiposScreen(
    listaEquipos: List<EquipoRefrigeracion>,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Equipos Adquiridos / Arrendados") },
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
            if (listaEquipos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes equipos registrados por compras o arriendos.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaEquipos) { equipo ->
                        val esDevuelto = equipo.estadoOperativo.contains("Devuelto", ignoreCase = true)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // 1. FOTO GRANDE ARRIBA
                                GaleriaFotos(fotosUrlsString = equipo.fotosUrls)

                                Spacer(modifier = Modifier.height(12.dp))

                                // 2. DETALLES ABAJO
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = equipo.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(if (esDevuelto) "Devuelto" else equipo.modalidad) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("ID Equipo: ${equipo.equipoId}", style = MaterialTheme.typography.bodySmall)
                                Text("Tipo: ${equipo.tipo.ifBlank { "Refrigeración" }}", style = MaterialTheme.typography.bodySmall)
                                Text("Modelo: ${equipo.modelo.ifBlank { "N/A" }}", style = MaterialTheme.typography.bodySmall)
                                Text("Fecha Adquisición: ${equipo.fechaAdquisicion}", style = MaterialTheme.typography.bodySmall)

                                if (equipo.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Descripción: ${equipo.descripcion}", style = MaterialTheme.typography.bodyMedium)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Estado: ${equipo.estadoOperativo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (esDevuelto) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}