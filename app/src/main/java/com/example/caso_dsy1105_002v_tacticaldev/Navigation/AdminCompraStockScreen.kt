@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.OrdenCompra
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCompraStockScreen(
    listaOrdenes: List<OrdenCompra>,
    onRegistrarCompra: (OrdenCompra) -> Unit,
    onVolver: () -> Unit
) {
    var filtroTexto by remember { mutableStateOf("") }
    var mostrarModalCrear by remember { mutableStateOf(false) }

    val ordenesFiltradas = remember(filtroTexto, listaOrdenes) {
        if (filtroTexto.isBlank()) listaOrdenes
        else listaOrdenes.filter {
            it.proveedor.contains(filtroTexto, ignoreCase = true) ||
                    it.nombreRepuesto.contains(filtroTexto, ignoreCase = true) ||
                    it.estado.contains(filtroTexto, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión Compra de Stock") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarModalCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Compra")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = filtroTexto,
                onValueChange = { filtroTexto = it },
                label = { Text("Filtrar por Proveedor, Repuesto o Estado") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (ordenesFiltradas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay órdenes de compra registradas.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ordenesFiltradas) { orden ->
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
                                    Text("Orden: ${orden.ordenId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    AssistChip(onClick = {}, label = { Text(orden.estado) })
                                }
                                Text("Proveedor: ${orden.proveedor}")
                                Text("Repuesto / Producto: ${orden.nombreRepuesto}")
                                Text("Cantidad Adquirida: ${orden.cantidad} unidades")
                                Text("Precio Costo Unitario: $${String.format("%,.0f", orden.precioCosto)}")
                                Text("Monto Total Invertido: $${String.format("%,.0f", orden.montoTotal)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Fecha Ingreso: ${orden.fecha}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarModalCrear) {
        DialogoNuevaCompraStock(
            onDismiss = { mostrarModalCrear = false },
            onGuardar = { nuevaOrden ->
                onRegistrarCompra(nuevaOrden)
                mostrarModalCrear = false
            }
        )
    }
}

@Composable
private fun DialogoNuevaCompraStock(
    onDismiss: () -> Unit,
    onGuardar: (OrdenCompra) -> Unit
) {
    var proveedor by remember { mutableStateOf("") }
    var nombreRepuesto by remember { mutableStateOf("") }
    var cantidadTexto by remember { mutableStateOf("") }
    var precioCostoTexto by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Compra de Stock") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = proveedor, onValueChange = { proveedor = it }, label = { Text("Nombre del Proveedor") }, singleLine = true)
                OutlinedTextField(value = nombreRepuesto, onValueChange = { nombreRepuesto = it }, label = { Text("Repuesto / Producto") }, singleLine = true)
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) cantidadTexto = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = precioCostoTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) precioCostoTexto = it },
                    label = { Text("Precio Costo Unitario ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                errorMensaje?.let { Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            Button(onClick = {
                val cant = cantidadTexto.toIntOrNull() ?: 0
                val costo = precioCostoTexto.toDoubleOrNull() ?: 0.0

                if (proveedor.isBlank() || nombreRepuesto.isBlank() || cant <= 0 || costo <= 0) {
                    errorMensaje = "Completa todos los campos con valores válidos."
                    return@Button
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val nuevaOrden = OrdenCompra(
                    ordenId = "ORD-" + System.currentTimeMillis(),
                    proveedor = proveedor.trim(),
                    repuestoId = "REP-" + System.currentTimeMillis(),
                    nombreRepuesto = nombreRepuesto.trim(),
                    cantidad = cant,
                    precioCosto = costo,
                    montoTotal = cant * costo,
                    fecha = fechaActual,
                    estado = "Completado"
                )
                onGuardar(nuevaOrden)
            }) {
                Text("Guardar Compra")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}