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
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorMisVentasScreen(
    listaMisVentas: List<Venta>,
    onDarDeBajaArriendo: (Venta) -> Unit,
    onVolver: () -> Unit
) {
    var arriendoAConfirmar by remember { mutableStateOf<Venta?>(null) }

    val totalVendido = remember(listaMisVentas) {
        listaMisVentas
            .filter { it.estado != "Arriendo Finalizado" && it.estado != "Dado de Baja" }
            .sumOf { if (it.montoTotal > 0) it.montoTotal else it.total }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Ventas y Arriendos") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen Personal de Operaciones", fontWeight = FontWeight.Bold)
                    Text("Total Operaciones Registradas: ${listaMisVentas.size}")
                    Text("Monto Total Activo: $${String.format("%,.0f", totalVendido)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            if (listaMisVentas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no has registrado ninguna venta o arriendo.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaMisVentas) { venta ->
                        val esArriendo = venta.tipoOperacion == "Arriendo"
                        val arriendoActivo = esArriendo && venta.estado != "Arriendo Finalizado" && venta.estado != "Dado de Baja"

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
                                    Text("ID Operación: ${venta.ventaId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(if (arriendoActivo) "Arriendo Activo" else venta.estado) }
                                    )
                                }
                                Text("Cliente: ${venta.clienteNombre}")
                                Text("Producto / Equipo: ${venta.productoNombre}")
                                Text("Cantidad: ${venta.cantidad} unidad(es) | Precio Unitario: $${String.format("%,.0f", venta.precioUnitario)}")
                                Text(
                                    "Monto Total: $${String.format("%,.0f", if (venta.montoTotal > 0) venta.montoTotal else venta.total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Fecha: ${venta.fecha} | Estado: ${venta.estado}", style = MaterialTheme.typography.bodySmall)

                                if (venta.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Descripción: ${venta.descripcion}", style = MaterialTheme.typography.bodySmall)
                                }

                                GaleriaFotos(fotosUrlsString = venta.fotosUrls)

                                if (arriendoActivo) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        onClick = { arriendoAConfirmar = venta },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Dar de Baja Arriendo")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    arriendoAConfirmar?.let { arriendo ->
        AlertDialog(
            onDismissRequest = { arriendoAConfirmar = null },
            title = { Text("Confirmar Baja de Arriendo") },
            text = {
                Text(
                    "¿Estás seguro de que deseas dar de baja el arriendo del equipo '${arriendo.productoNombre}' para el cliente '${arriendo.clienteNombre}'?\n\n" +
                            "Esta acción marcará el contrato como finalizado y devolverá ${arriendo.cantidad} unidad(es) al stock disponible del inventario."
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        onDarDeBajaArriendo(arriendo)
                        arriendoAConfirmar = null
                    }
                ) {
                    Text("Confirmar Baja y Reponer Stock")
                }
            },
            dismissButton = {
                TextButton(onClick = { arriendoAConfirmar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}