@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteSolicitarVisitaRemotaScreen(
    minimarketCliente: Minimarket?,
    asistenciasExistentes: List<AsistenciaRemota>,
    onSolicitarRemota: (AsistenciaRemota) -> Unit,
    onVolver: () -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf("") }
    var bloqueHorario by remember { mutableStateOf("09:00 - 12:00") }
    var motivo by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }

    val opcionesBloques = listOf("09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00")

    fun procesarSolicitud() {
        if (minimarketCliente == null) {
            errorMensaje = "No se encontraron datos del minimarket."
            return
        }
        if (fechaSeleccionada.isBlank() || motivo.isBlank()) {
            errorMensaje = "Ingresa la fecha requerida y el motivo de la asistencia remota."
            return
        }

        val nuevaAsistencia = AsistenciaRemota(
            asistenciaId = "REM-" + System.currentTimeMillis(),
            minimarketId = minimarketCliente.minimarketId,
            clienteNombre = minimarketCliente.nombre,
            fecha = fechaSeleccionada.trim(),
            bloqueHorario = bloqueHorario,
            motivo = motivo.trim(),
            estado = "Pendiente",
            enlaceReunion = "",
            motivoCancelacion = "",
            motivoReprogramacion = ""
        )

        onSolicitarRemota(nuevaAsistencia)
        exitoMensaje = true
        errorMensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Asistencia Remota") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            minimarketCliente?.let { mini ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Datos del Minimarket:", fontWeight = FontWeight.Bold)
                        Text("Nombre: ${mini.nombre}")
                        Text("Dirección: ${mini.direccion}")
                    }
                }
            }

            OutlinedTextField(
                value = fechaSeleccionada,
                onValueChange = { fechaSeleccionada = it },
                label = { Text("Fecha Solicitada (ej: DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Bloque Horario Preferido:", fontWeight = FontWeight.Bold)
            opcionesBloques.forEach { bloque ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (bloqueHorario == bloque),
                        onClick = { bloqueHorario = bloque }
                    )
                    Text(bloque)
                }
            }

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo o Diagnóstico Requerido") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            errorMensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            if (exitoMensaje) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Solicitud enviada correctamente.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarSolicitud() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enviar Solicitud Remota")
            }
        }
    }
}