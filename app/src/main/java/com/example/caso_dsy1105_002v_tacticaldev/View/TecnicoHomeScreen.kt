package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoHomeScreen(
    onNavigateToVisitas: () -> Unit,
    onNavigateToSalaRemota: () -> Unit,
    onNavigateToInventarioRepuestos: () -> Unit,
    onLogout: () -> Unit
) {
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Técnico - TacticalDEV") },
                actions = {
                    IconButton(onClick = { mostrarDialogoLogout = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Servicios Técnicos Asignados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            TecnicoMenuCard(
                titulo = "Gestión de Visitas (Terreno / Remota)",
                descripcion = "Visualiza y actualiza la pauta de visitas técnicas presenciales y agendamientos remotos.",
                icono = Icons.Default.LocationOn,
                onClick = onNavigateToVisitas
            )

            TecnicoMenuCard(
                titulo = "Sala Remota Virtual",
                descripcion = "Inicia reunión y diagnóstico en vivo con los clientes que solicitaron asistencia remota.",
                icono = Icons.Default.Phone,
                onClick = onNavigateToSalaRemota
            )

            TecnicoMenuCard(
                titulo = "Inventario de Repuestos",
                descripcion = "Consulta el catálogo y disponibilidad de repuestos para mantenciones.",
                icono = Icons.Default.Build,
                onClick = onNavigateToInventarioRepuestos
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { mostrarDialogoLogout = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Está seguro de que desea cerrar sesión?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        mostrarDialogoLogout = false
                        onLogout()
                    }
                ) {
                    Text("Sí, Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TecnicoMenuCard(titulo: String, descripcion: String, icono: ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
