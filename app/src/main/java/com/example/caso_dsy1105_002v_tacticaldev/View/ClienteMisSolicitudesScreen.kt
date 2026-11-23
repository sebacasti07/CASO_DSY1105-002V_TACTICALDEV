package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import java.text.SimpleDateFormat
import java.util.*

private fun calcularMinimoDiasHabiles(diasHabilesRequeridos: Int): Long {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    var diasContados = 0
    while (diasContados < diasHabilesRequeridos) {
        cal.add(Calendar.DAY_OF_YEAR, 1)
        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            diasContados++
        }
    }
    return cal.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteMisSolicitudesScreen(
    listaCotizaciones: List<Venta>,
    listaVisitas: List<Visita>,
    listaRemotas: List<AsistenciaRemota>,
    onCancelarCotizacion: (Venta) -> Unit,
    onCancelarVisita: (Visita) -> Unit,
    onCancelarRemota: (AsistenciaRemota) -> Unit,
    onReagendarCotizacion: (Venta) -> Unit,
    onReagendarVisita: (Visita) -> Unit,
    onReagendarRemota: (AsistenciaRemota) -> Unit,
    onVolver: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cotizaciones", "Visitas Presenciales", "Visitas Remotas")

    var visitaAReagendar by remember { mutableStateOf<Visita?>(null) }
    var remotaAReagendar by remember { mutableStateOf<AsistenciaRemota?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes Realizadas") },
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
        ) {
            TabRow(selectedTabIndex = tabSeleccionada) {
                tabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = { Text(titulo) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (tabSeleccionada) {
                    0 -> {
                        if (listaCotizaciones.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes cotizaciones solicitadas.")
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listaCotizaciones) { cot ->
                                    val esCotizada = cot.estado.equals("Cotizada", ignoreCase = true)
                                    val esCancelada = cot.estado.contains("Cancelad", ignoreCase = true)

                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("ID Cotización: ${cot.ventaId}", fontWeight = FontWeight.Bold)
                                            Text("Producto: ${cot.productoNombre}")
                                            Text("Cantidad: ${cot.cantidad}")
                                            Text("Monto Estimado: $${String.format(Locale.getDefault(), "%,.0f", if (cot.montoTotal > 0) cot.montoTotal else cot.total)}")
                                            Text("Estado: ${cot.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (esCotizada) {
                                                    Button(
                                                        onClick = {
                                                            onReagendarCotizacion(
                                                                cot.copy(estado = "Pendiente de Despacho")
                                                            )
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                    ) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Aceptar / Aprobar Cotización")
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }

                                                if (!esCancelada) {
                                                    OutlinedButton(
                                                        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.error),
                                                        onClick = { onCancelarCotizacion(cot.copy(estado = "Cancelada por Cliente")) }
                                                    ) {
                                                        Text("Cancelar Cotización")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        if (listaVisitas.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes visitas presenciales solicitadas.")
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listaVisitas) { vis ->
                                    val esCancelada = vis.estado.contains("Cancelad", ignoreCase = true)

                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("ID Visita: ${vis.visitaId}", fontWeight = FontWeight.Bold)
                                            Text("Fecha: ${vis.fecha} | Bloque: ${vis.bloqueHorario}")
                                            Text("Motivo: ${vis.motivo}")
                                            Text("Estado: ${vis.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                if (!esCancelada) {
                                                    OutlinedButton(onClick = { visitaAReagendar = vis }) {
                                                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Reagendar")
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                        onClick = { onCancelarVisita(vis.copy(estado = "Cancelada por Cliente", motivoCancelacion = "Cancelada por Cliente")) }
                                                    ) {
                                                        Text("Cancelar Visita")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        if (listaRemotas.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes asistencias remotas solicitadas.")
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listaRemotas) { rem ->
                                    val esCancelada = rem.estado.contains("Cancelad", ignoreCase = true)

                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("ID Asistencia: ${rem.asistenciaId}", fontWeight = FontWeight.Bold)
                                            Text("Fecha: ${rem.fecha} | Bloque: ${rem.bloqueHorario}")
                                            Text("Motivo: ${rem.motivo}")
                                            Text("Enlace: ${rem.enlaceReunion.ifBlank { "Pendiente por asignar" }}")
                                            Text("Estado: ${rem.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                if (!esCancelada) {
                                                    OutlinedButton(onClick = { remotaAReagendar = rem }) {
                                                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Reagendar")
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                        onClick = { onCancelarRemota(rem.copy(estado = "Cancelada por Cliente", motivoCancelacion = "Cancelada por Cliente")) }
                                                    ) {
                                                        Text("Cancelar Solicitud")
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
            }
        }
    }

    visitaAReagendar?.let { vis ->
        DialogoReagendarSolicitud(
            fechaActual = vis.fecha,
            bloqueActual = vis.bloqueHorario,
            esRemota = false,
            onDismiss = { visitaAReagendar = null },
            onConfirmar = { nuevaFecha, nuevoBloque ->
                onReagendarVisita(vis.copy(fecha = nuevaFecha, bloqueHorario = nuevoBloque, estado = "Reagendada por Cliente"))
                visitaAReagendar = null
            }
        )
    }

    remotaAReagendar?.let { rem ->
        DialogoReagendarSolicitud(
            fechaActual = rem.fecha,
            bloqueActual = rem.bloqueHorario,
            esRemota = true,
            onDismiss = { remotaAReagendar = null },
            onConfirmar = { nuevaFecha, nuevoBloque ->
                onReagendarRemota(rem.copy(fecha = nuevaFecha, bloqueHorario = nuevoBloque, estado = "Reagendada por Cliente"))
                remotaAReagendar = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoReagendarSolicitud(
    fechaActual: String,
    bloqueActual: String,
    esRemota: Boolean,
    onDismiss: () -> Unit,
    onConfirmar: (String, String) -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf(fechaActual) }
    var fechaSeleccionadaMillis by remember { mutableStateOf<Long?>(null) }
    var diaSemanaSeleccionado by remember { mutableIntStateOf(Calendar.MONDAY) }
    var bloqueHorario by remember { mutableStateOf(bloqueActual.ifBlank { if (esRemota) "09:00 - 11:00" else "09:00 - 12:00" }) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var errorFecha by remember { mutableStateOf<String?>(null) }

    val minTimeMillis = calcularMinimoDiasHabiles(2)

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (utcTimeMillis < minTimeMillis) return false
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcTimeMillis }
                return cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
            }
        }
    )

    val opcionesBloques = remember(esRemota, diaSemanaSeleccionado) {
        if (esRemota && diaSemanaSeleccionado == Calendar.SATURDAY) {
            listOf("09:00 - 11:00", "12:00 - 14:00")
        } else {
            listOf("09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00")
        }
    }

    LaunchedEffect(opcionesBloques) {
        if (bloqueHorario !in opcionesBloques) {
            bloqueHorario = opcionesBloques.first()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (esRemota) "Reagendar Asistencia Remota" else "Reagendar Visita Presencial") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fechaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nueva Fecha (Mín. 2 días hábiles)") },
                    isError = errorFecha != null,
                    supportingText = { errorFecha?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (errorFecha != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            IconButton(onClick = { mostrarDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarDatePicker = true }
                )

                Text("Seleccionar Nuevo Bloque:", fontWeight = FontWeight.Bold)
                opcionesBloques.forEach { bloque ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (bloqueHorario == bloque),
                            onClick = { bloqueHorario = bloque }
                        )
                        Text(text = bloque)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fechaSeleccionada.isBlank() || fechaSeleccionada == fechaActual && fechaSeleccionadaMillis == null) {
                        errorFecha = "Selecciona una fecha válida en el calendario"
                        return@Button
                    }
                    errorFecha = null
                    onConfirmar(fechaSeleccionada, bloqueHorario)
                }
            ) {
                Text("Confirmar Reagendamiento")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
                        fechaSeleccionada = sdf.format(Date(millis))
                        fechaSeleccionadaMillis = millis
                        diaSemanaSeleccionado = cal.get(Calendar.DAY_OF_WEEK)
                        errorFecha = null
                    }
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }
}
