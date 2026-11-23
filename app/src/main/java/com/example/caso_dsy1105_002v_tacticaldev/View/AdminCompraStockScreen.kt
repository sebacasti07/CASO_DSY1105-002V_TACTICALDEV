package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.OrdenCompra
import com.example.caso_dsy1105_002v_tacticaldev.Util.ValidationUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCompraStockScreen(
    listaOrdenes: List<OrdenCompra>,
    listaInventario: List<InventarioRepuesto> = emptyList(),
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
            listaInventario = listaInventario,
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
    listaInventario: List<InventarioRepuesto>,
    onDismiss: () -> Unit,
    onGuardar: (OrdenCompra) -> Unit
) {
    var itemSeleccionado by remember { mutableStateOf<InventarioRepuesto?>(null) }
    var proveedor by remember { mutableStateOf("") }
    var nombreRepuestoNuevo by remember { mutableStateOf("") }
    var cantidadTexto by remember { mutableStateOf("") }
    var precioCostoTexto by remember { mutableStateOf("") }

    var errorProveedor by remember { mutableStateOf<String?>(null) }
    var errorNombreRepuesto by remember { mutableStateOf<String?>(null) }
    var errorCantidad by remember { mutableStateOf<String?>(null) }
    var errorPrecioCosto by remember { mutableStateOf<String?>(null) }

    fun validar(): Boolean {
        errorProveedor = if (proveedor.isBlank()) "Ingresa el proveedor" else null

        val nombreFinal = if (itemSeleccionado != null) itemSeleccionado!!.nombreRepuesto.ifBlank { itemSeleccionado!!.nombre } else nombreRepuestoNuevo
        errorNombreRepuesto = if (nombreFinal.trim().isEmpty()) "El nombre del repuesto es obligatorio" else null

        val cant = cantidadTexto.toIntOrNull()
        errorCantidad = if (cant == null || cant <= 0) "Ingresa una cantidad mayor a 0" else null

        val costo = precioCostoTexto.toDoubleOrNull()
        errorPrecioCosto = if (costo == null || costo <= 0.0) "Ingresa un precio costo válido" else null

        return errorProveedor == null && errorNombreRepuesto == null && errorCantidad == null && errorPrecioCosto == null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Compra de Stock") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Seleccionar Ítem del Inventario:", fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (itemSeleccionado == null),
                        onClick = { itemSeleccionado = null }
                    )
                    Text("Crear Nuevo Registro de Repuesto")
                }

                listaInventario.forEach { item ->
                    val nombre = item.nombreRepuesto.ifBlank { item.nombre }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (itemSeleccionado == item),
                            onClick = {
                                itemSeleccionado = item
                                errorNombreRepuesto = null
                            }
                        )
                        Text("$nombre (Stock actual: ${item.stockDisponible})")
                    }
                }

                OutlinedTextField(
                    value = proveedor,
                    onValueChange = { proveedor = it; errorProveedor = null },
                    label = { Text("Nombre del Proveedor") },
                    isError = errorProveedor != null,
                    supportingText = { errorProveedor?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorProveedor != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    singleLine = true
                )

                AnimatedVisibility(
                    visible = itemSeleccionado == null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = nombreRepuestoNuevo,
                        onValueChange = { nombreRepuestoNuevo = it; errorNombreRepuesto = null },
                        label = { Text("Nombre Nuevo Repuesto / Producto") },
                        isError = errorNombreRepuesto != null,
                        supportingText = { errorNombreRepuesto?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        trailingIcon = { if (errorNombreRepuesto != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) { cantidadTexto = it; errorCantidad = null } },
                    label = { Text("Cantidad a Ingresar") },
                    isError = errorCantidad != null,
                    supportingText = { errorCantidad?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorCantidad != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = precioCostoTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) { precioCostoTexto = it; errorPrecioCosto = null } },
                    label = { Text("Precio Costo Unitario ($)") },
                    isError = errorPrecioCosto != null,
                    supportingText = { errorPrecioCosto?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorPrecioCosto != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (validar()) {
                    val cant = cantidadTexto.toInt()
                    val costo = precioCostoTexto.toDouble()
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaActual = sdf.format(Date())

                    val repId = itemSeleccionado?.repuestoId?.ifBlank { itemSeleccionado?.inventarioId } ?: ("REP-" + System.currentTimeMillis())
                    val nomRep = itemSeleccionado?.let { it.nombreRepuesto.ifBlank { it.nombre } } ?: nombreRepuestoNuevo.trim()

                    val nuevaOrden = OrdenCompra(
                        ordenId = "ORD-" + System.currentTimeMillis(),
                        proveedor = proveedor.trim(),
                        repuestoId = repId,
                        nombreRepuesto = nomRep,
                        cantidad = cant,
                        precioCosto = costo,
                        montoTotal = cant * costo,
                        fecha = fechaActual,
                        estado = "Completado"
                    )
                    onGuardar(nuevaOrden)
                }
            }) {
                Text("Guardar Compra")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
