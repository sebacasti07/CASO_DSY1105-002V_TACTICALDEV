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
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import java.util.Locale

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
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("ID Cotización: ${cot.ventaId}", fontWeight = FontWeight.Bold)
                                            Text("Producto: ${cot.productoNombre}")
                                            Text("Monto Estimado: $${String.format(Locale.getDefault(), "%,.0f", if (cot.montoTotal > 0) cot.montoTotal else cot.total)}")
                                            Text("Estado: ${cot.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                            if (cot.estado != "Cancelada") {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                    onClick = { onCancelarCotizacion(cot.copy(estado = "Cancelada")) }
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

                    1 -> {
                        if (listaVisitas.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes visitas presenciales solicitadas.")
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listaVisitas) { vis ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("ID Visita: ${vis.visitaId}", fontWeight = FontWeight.Bold)
                                            Text("Fecha: ${vis.fecha} | Bloque: ${vis.bloqueHorario}")
                                            Text("Motivo: ${vis.motivo}")
                                            Text("Estado: ${vis.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                            if (vis.estado != "Cancelada") {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                    onClick = { onCancelarVisita(vis.copy(estado = "Cancelada", motivoCancelacion = "Cancelada por Cliente")) }
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

                    2 -> {
                        if (listaRemotas.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes asistencias remotas solicitadas.")
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listaRemotas) { rem ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("ID Asistencia: ${rem.asistenciaId}", fontWeight = FontWeight.Bold)
                                            Text("Fecha: ${rem.fecha} | Bloque: ${rem.bloqueHorario}")
                                            Text("Motivo: ${rem.motivo}")
                                            Text("Enlace: ${rem.enlaceReunion.ifBlank { "Pendiente por asignar" }}")
                                            Text("Estado: ${rem.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                            if (rem.estado != "Cancelada") {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                    onClick = { onCancelarRemota(rem.copy(estado = "Cancelada", motivoCancelacion = "Cancelada por Cliente")) }
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