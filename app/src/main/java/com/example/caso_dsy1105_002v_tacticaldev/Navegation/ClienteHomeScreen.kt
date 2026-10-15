package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteHomeScreen(
    onNavigateToSolicitarCotizacion: () -> Unit,
    onNavigateToSolicitarVisita: () -> Unit,
    onNavigateToSolicitarVisitaRemota: () -> Unit,
    onNavigateToMisSolicitudes: () -> Unit,
    onNavigateToMisEquipos: () -> Unit,
    onNavigateToCatalogoEquipos: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Cliente - TacticalDEV") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
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
            Text(text = "Servicios y Solicitudes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            ClienteMenuCard(
                titulo = "Solicitar Cotización",
                descripcion = "Pide presupuesto para equipos o repuestos (2 días hábiles de anticipación).",
                icono = Icons.Default.ShoppingCart,
                onClick = onNavigateToSolicitarCotizacion
            )

            ClienteMenuCard(
                titulo = "Solicitar Visita Presencial",
                descripcion = "Agenda una revisión en tu local (Lunes a Viernes, 2 días hábiles de anticipación).",
                icono = Icons.Default.LocationOn,
                onClick = onNavigateToSolicitarVisita
            )

            ClienteMenuCard(
                titulo = "Solicitar Visita Remota",
                descripcion = "Atención a distancia (Lunes a Sábado, 1 día de anticipación).",
                icono = Icons.Default.Phone,
                onClick = onNavigateToSolicitarVisitaRemota
            )

            ClienteMenuCard(
                titulo = "Mis Solicitudes",
                descripcion = "Revisa el estado de tus solicitudes (Pendientes y Realizadas).",
                icono = Icons.Default.DateRange,
                onClick = onNavigateToMisSolicitudes
            )

            ClienteMenuCard(
                titulo = "Mis Equipos",
                descripcion = "Consulta el historial de tus equipos comprados y arrendados.",
                icono = Icons.Default.Home,
                onClick = onNavigateToMisEquipos
            )

            ClienteMenuCard(
                titulo = "Catálogo Equipos",
                descripcion = "Vitrina de equipos de refrigeración disponibles.",
                icono = Icons.Default.List,
                onClick = onNavigateToCatalogoEquipos
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun ClienteMenuCard(titulo: String, descripcion: String, icono: ImageVector, onClick: () -> Unit) {
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