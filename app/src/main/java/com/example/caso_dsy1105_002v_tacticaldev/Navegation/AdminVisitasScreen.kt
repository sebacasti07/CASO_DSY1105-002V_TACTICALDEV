package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVisitasScreen(
    listaVisitas: List<Visita>,
    onActualizarVisita: (Visita) -> Unit,
    onVolver: () -> Unit
) {
    var visitaACancelar by remember { mutableStateOf<Visita?>(null) }
    var visitaAReprogramar by remember { mutableStateOf<Visita?>(null) }
    var visitaAReactivar by remember { mutableStateOf<Visita?>(null) }

    var motivoAdminCancelacion by remember { mutableStateOf("") }
    var datosConfirmacionReprogramacion by remember { mutableStateOf<Pair<Visita, Pair<String, String>>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Visitas Presenciales") },
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
            if (listaVisitas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay visitas presenciales registradas.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaVisitas) { visita ->
                        val esCancelada = visita.estado.contains("Cancelad", ignoreCase = true)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Visita ID: ${visita.visitaId}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    AssistChip(onClick = {}, label = { Text(visita.estado) })
                                }
                                Text("Fecha: ${visita.fecha} | Bloque: ${visita.bloqueHorario}")
                                Text("Minimarket ID: ${visita.minimarketId}")
                                Text("Motivo: ${visita.motivo}")

                                if (visita.motivoReprogramacion.isNotBlank()) {
                                    Text(
                                        text = "Motivo Reprogramación: ${visita.motivoReprogramacion}",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                if (visita.motivoCancelacion.isNotBlank()) {
                                    Text(
                                        text = "Motivo Cancelación: ${visita.motivoCancelacion}",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!esCancelada) {
                                        TextButton(onClick = { visitaAReprogramar = visita }) {
                                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reprogramar")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { visitaACancelar = visita; motivoAdminCancelacion = "" },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Cancelar")
                                        }
                                    } else {
                                        Button(onClick = { visitaAReactivar = visita }) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reactivar Visita")
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

    visitaACancelar?.let { visita ->
        AlertDialog(
            onDismissRequest = { visitaACancelar = null },
            title = { Text("Cancelar Visita Presencial") },
            text = {
                Column {
                    Text("Ingresa el motivo de cancelación para notificar al usuario:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = motivoAdminCancelacion,
                        onValueChange = { motivoAdminCancelacion = it },
                        label = { Text("Motivo de Cancelación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        val visitaCancelada = visita.copy(
                            estado = "Cancelada por Administrador",
                            motivoCancelacion = motivoAdminCancelacion.trim()
                        )
                        onActualizarVisita(visitaCancelada)
                        visitaACancelar = null
                    }
                ) {
                    Text("Confirmar Cancelación")
                }
            },
            dismissButton = { TextButton(onClick = { visitaACancelar = null }) { Text("Volver") } }
        )
    }

    visitaAReactivar?.let { visita ->
        AlertDialog(
            onDismissRequest = { visitaAReactivar = null },
            title = { Text("Reactivar Visita Presencial") },
            text = { Text("¿Estás seguro de que deseas reactivar esta visita? Pasará nuevamente a estado Pendiente.") },
            confirmButton = {
                Button(
                    onClick = {
                        val visitaReactivada = visita.copy(
                            estado = "Pendiente",
                            motivoCancelacion = ""
                        )
                        onActualizarVisita(visitaReactivada)
                        visitaAReactivar = null
                    }
                ) {
                    Text("Reactivar")
                }
            },
            dismissButton = { TextButton(onClick = { visitaAReactivar = null }) { Text("Cancelar") } }
        )
    }

    visitaAReprogramar?.let { visita ->
        DialogoFormularioReprogramarVisita(
            visitaActual = visita,
            onDismiss = { visitaAReprogramar = null },
            onSolicitarConfirmacion = { nuevaFecha, nuevoBloque, motivo ->
                datosConfirmacionReprogramacion = Pair(visita.copy(motivoReprogramacion = motivo), Pair(nuevaFecha, nuevoBloque))
                visitaAReprogramar = null
            }
        )
    }

    datosConfirmacionReprogramacion?.let { (visita, par) ->
        val (nuevaFecha, nuevoBloque) = par
        AlertDialog(
            onDismissRequest = { datosConfirmacionReprogramacion = null },
            title = { Text("Confirmar Reprogramación") },
            text = { Text("¿Estás seguro de que deseas reprogramar esta visita presencial para el $nuevaFecha ($nuevoBloque)?") },
            confirmButton = {
                Button(
                    onClick = {
                        val visitaActualizada = visita.copy(
                            fecha = nuevaFecha,
                            bloqueHorario = nuevoBloque,
                            estado = "Reprogramada por Admin"
                        )
                        onActualizarVisita(visitaActualizada)
                        datosConfirmacionReprogramacion = null
                    }
                ) {
                    Text("Confirmar y Reprogramar")
                }
            },
            dismissButton = { TextButton(onClick = { datosConfirmacionReprogramacion = null }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFormularioReprogramarVisita(
    visitaActual: Visita,
    onDismiss: () -> Unit,
    onSolicitarConfirmacion: (String, String, String) -> Unit
) {
    var fechaSeleccionadaMillis by remember { mutableStateOf<Long?>(null) }
    var bloqueHorario by remember { mutableStateOf(visitaActual.bloqueHorario.ifBlank { "09:00 - 12:00" }) }
    var motivoReprogramacion by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val fechaFormateada = remember(fechaSeleccionadaMillis) {
        fechaSeleccionadaMillis?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(Date(it))
        } ?: visitaActual.fecha
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reprogramar Visita") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fechaFormateada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nueva Fecha") },
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Seleccionar Bloque:", fontWeight = FontWeight.Bold)
                listOf("09:00 - 12:00", "15:00 - 18:00").forEach { bloque ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (bloqueHorario == bloque),
                            onClick = { bloqueHorario = bloque }
                        )
                        Text(text = bloque)
                    }
                }

                OutlinedTextField(
                    value = motivoReprogramacion,
                    onValueChange = { motivoReprogramacion = it },
                    label = { Text("Motivo de la Reprogramación") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSolicitarConfirmacion(fechaFormateada, bloqueHorario, motivoReprogramacion.trim()) }
            ) {
                Text("Continuar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaSeleccionadaMillis = datePickerState.selectedDateMillis
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }
}