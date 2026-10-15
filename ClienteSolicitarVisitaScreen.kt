@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteSolicitarVisitaScreen(
    minimarketCliente: Minimarket?,
    visitasExistentes: List<Visita>,
    onSolicitarVisita: (Visita) -> Unit,
    onVolver: () -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf("") }
    var fechaMillisSeleccionada by remember { mutableLongStateOf(0L) }
    var bloqueHorario by remember { mutableStateOf("09:00 - 12:00") }
    var motivo by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    // Días mínimos de anticipación requeridos
    val diasAnticipacionMinimos = 2

    // Calcular el timestamp mínimo permitido (Hoy + 2 días)
    val calMinimo = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, diasAnticipacionMinimos)
    }
    val minTimeMillis = calMinimo.timeInMillis

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minTimeMillis
            }
        }
    )

    val opcionesBloques = listOf("09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00")

    fun procesarSolicitud() {
        if (minimarketCliente == null) {
            errorMensaje = "No se encontraron datos del minimarket asociado."
            return
        }
        if (fechaSeleccionada.isBlank()) {
            errorMensaje = "Selecciona una fecha en el calendario."
            return
        }
        if (fechaMillisSeleccionada < minTimeMillis) {
            errorMensaje = "Las solicitudes deben realizarse con al menos $diasAnticipacionMinimos días de anticipación."
            return
        }
        if (motivo.isBlank()) {
            errorMensaje = "Ingresa el motivo o problema del equipo."
            return
        }

        val nuevaVisita = Visita(
            visitaId = "VIS-" + System.currentTimeMillis(),
            minimarketId = minimarketCliente.minimarketId,
            clienteNombre = minimarketCliente.nombre,
            direccion = minimarketCliente.direccion,
            fecha = fechaSeleccionada.trim(),
            bloqueHorario = bloqueHorario,
            motivo = motivo.trim(),
            estado = "Pendiente",
            observacionesTecnico = "",
            motivoCancelacion = "",
            motivoReprogramacion = "",
            ubicacionGps = ""
        )

        onSolicitarVisita(nuevaVisita)
        exitoMensaje = true
        errorMensaje = null
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        if (millis < minTimeMillis) {
                            errorMensaje = "Debes seleccionar una fecha con al menos $diasAnticipacionMinimos días de anticipación."
                        } else {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            fechaSeleccionada = sdf.format(Date(millis))
                            fechaMillisSeleccionada = millis
                            errorMensaje = null
                        }
                    }
                    mostrarCalendario = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Visita Técnica Presencial") },
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

            // AVISO REGLA DE ANTICIPACIÓN
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Text(
                    text = "⚠️ Nota: Las visitas presenciales deben programarse con un mínimo de $diasAnticipacionMinimos días de anticipación.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // SELECCIÓN DE FECHA
            OutlinedTextField(
                value = fechaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha Solicitada") },
                placeholder = { Text("Haz clic aquí para abrir el calendario") },
                trailingIcon = {
                    IconButton(onClick = { mostrarCalendario = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Abrir Calendario")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarCalendario = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                label = { Text("Motivo / Problema del Equipo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            errorMensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            if (exitoMensaje) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Solicitud de visita técnica enviada correctamente.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarSolicitud() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enviar Solicitud de Visita")
            }
        }
    }
}