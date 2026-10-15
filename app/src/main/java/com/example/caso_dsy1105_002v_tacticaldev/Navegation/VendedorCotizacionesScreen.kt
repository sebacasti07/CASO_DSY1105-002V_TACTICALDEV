@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorCotizacionesScreen(
    vendedorActual: Usuario?,
    listaCotizaciones: List<Venta>,
    onResponderCotizacion: (Venta) -> Unit,
    onVolver: () -> Unit
) {
    var cotizacionAEditar by remember { mutableStateOf<Venta?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cotizaciones Solicitadas por Clientes") },
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
            if (listaCotizaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay solicitudes de cotización pendientes.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaCotizaciones) { cot ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("ID Cotización: ${cot.ventaId}", fontWeight = FontWeight.Bold)
                                Text("Cliente: ${cot.clienteNombre}")
                                Text("Producto / Equipo: ${cot.productoNombre}")
                                Text("Cantidad: ${cot.cantidad}")
                                if (cot.detalle.isNotBlank()) {
                                    Text("Detalle: ${cot.detalle}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    "Monto Cotizado: $${String.format(Locale.getDefault(), "%,.0f", if (cot.montoTotal > 0) cot.montoTotal else cot.total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Estado: ${cot.estado}", fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { cotizacionAEditar = cot },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Responder / Fijar Precio")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    cotizacionAEditar?.let { cot ->
        var precioTexto by remember { mutableStateOf(if (cot.precioUnitario > 0) cot.precioUnitario.toInt().toString() else "") }

        AlertDialog(
            onDismissRequest = { cotizacionAEditar = null },
            title = { Text("Responder Cotización") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cliente: ${cot.clienteNombre}")
                    Text("Producto: ${cot.productoNombre} (${cot.cantidad} unidad/es)")
                    OutlinedTextField(
                        value = precioTexto,
                        onValueChange = { if (it.all { char -> char.isDigit() }) precioTexto = it },
                        label = { Text("Precio Unitario Cotizado ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val pUnit = precioTexto.toDoubleOrNull() ?: 0.0
                    val totalCalc = pUnit * cot.cantidad
                    val cotActualizada = cot.copy(
                        vendedorId = vendedorActual?.usuarioId ?: cot.vendedorId,
                        vendedorNombre = "${vendedorActual?.primerNombre ?: ""} ${vendedorActual?.apellido ?: ""}".trim(),
                        precioUnitario = pUnit,
                        total = totalCalc,
                        montoTotal = totalCalc,
                        estado = "Cotizada"
                    )
                    onResponderCotizacion(cotActualizada)
                    cotizacionAEditar = null
                }) {
                    Text("Guardar y Responder")
                }
            },
            dismissButton = {
                TextButton(onClick = { cotizacionAEditar = null }) { Text("Cancelar") }
            }
        )
    }
}