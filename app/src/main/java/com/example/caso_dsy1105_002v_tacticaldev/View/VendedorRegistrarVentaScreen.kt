package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorRegistrarVentaScreen(
    vendedorActual: Usuario?,
    listaMinimarkets: List<Minimarket>,
    listaInventario: List<InventarioRepuesto>,
    onRegistrarOperacion: (Venta, InventarioRepuesto?) -> Unit,
    onVolver: () -> Unit
) {
    var tipoOperacion by remember { mutableStateOf("Venta Directa") }
    var minimarketSeleccionado by remember { mutableStateOf<Minimarket?>(null) }
    var productoSeleccionado by remember { mutableStateOf<InventarioRepuesto?>(null) }
    var cantidadTexto by remember { mutableStateOf("1") }

    var errorMinimarket by remember { mutableStateOf<String?>(null) }
    var errorProducto by remember { mutableStateOf<String?>(null) }
    var errorCantidad by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }

    val precioUnitarioAuto = remember(productoSeleccionado, tipoOperacion) {
        if (productoSeleccionado == null) 0.0
        else if (tipoOperacion == "Arriendo") productoSeleccionado!!.precioArriendo
        else productoSeleccionado!!.precioUnitario
    }

    val cantidadInt = cantidadTexto.toIntOrNull() ?: 0
    val montoTotalCalculado = cantidadInt * precioUnitarioAuto

    fun validarFormulario(): Boolean {
        errorMinimarket = if (minimarketSeleccionado == null) "Selecciona un Minimarket / Cliente" else null
        errorProducto = if (productoSeleccionado == null) "Selecciona un producto o equipo del inventario" else null

        val stockActual = productoSeleccionado?.stockDisponible ?: 0
        errorCantidad = when {
            cantidadInt <= 0 -> "Ingresa una cantidad mayor a 0"
            cantidadInt > stockActual -> "Stock insuficiente ($stockActual unidades disponibles)"
            else -> null
        }

        if (productoSeleccionado != null && precioUnitarioAuto <= 0) {
            errorGeneral = "El producto no tiene configurado un precio válido para $tipoOperacion."
            return false
        } else {
            errorGeneral = null
        }

        return errorMinimarket == null && errorProducto == null && errorCantidad == null
    }

    fun procesarRegistro() {
        if (!validarFormulario()) return

        val stockActual = productoSeleccionado!!.stockDisponible
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        val nuevoStock = stockActual - cantidadInt
        val inventarioActualizado = productoSeleccionado!!.copy(
            stockDisponible = nuevoStock,
            cantidadDisponible = nuevoStock
        )

        val nombreProd = if (productoSeleccionado!!.nombreRepuesto.isNotBlank()) productoSeleccionado!!.nombreRepuesto else productoSeleccionado!!.nombre

        // Estado inicial de logística: "Pendiente de Despacho"
        val nuevaVenta = Venta(
            ventaId = "VEN-" + System.currentTimeMillis(),
            vendedorId = vendedorActual?.usuarioId ?: "USR-VEN-DEFAULT",
            vendedorNombre = "${vendedorActual?.primerNombre ?: "Vendedor"} ${vendedorActual?.apellido ?: ""}".trim(),
            clienteNombre = minimarketSeleccionado?.nombre ?: "",
            minimarketId = minimarketSeleccionado?.minimarketId ?: "",
            repuestoId = productoSeleccionado?.inventarioId ?: "",
            productoNombre = nombreProd,
            tipoOperacion = tipoOperacion,
            cantidad = cantidadInt,
            precioUnitario = precioUnitarioAuto,
            total = montoTotalCalculado,
            montoTotal = montoTotalCalculado,
            fecha = fechaActual,
            estado = "Pendiente de Despacho",
            descripcion = productoSeleccionado?.descripcion ?: "",
            fotosUrls = productoSeleccionado?.fotosUrls ?: ""
        )

        onRegistrarOperacion(nuevaVenta, inventarioActualizado)
        exitoMensaje = true
        errorGeneral = null
        onVolver()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Venta o Arriendo") },
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
            Text("Tipo de Operación Comercial:", fontWeight = FontWeight.Bold)
            Row {
                FilterChip(
                    selected = (tipoOperacion == "Venta Directa"),
                    onClick = { tipoOperacion = "Venta Directa" },
                    label = { Text("Venta Directa") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = (tipoOperacion == "Arriendo"),
                    onClick = { tipoOperacion = "Arriendo" },
                    label = { Text("Arriendo de Máquina") }
                )
            }

            Text("Seleccionar Cliente / Minimarket:", fontWeight = FontWeight.Bold)
            if (listaMinimarkets.isEmpty()) {
                Text("No hay minimarkets registrados por Admin.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            } else {
                listaMinimarkets.forEach { mini ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (minimarketSeleccionado == mini),
                            onClick = {
                                minimarketSeleccionado = mini
                                errorMinimarket = null
                            }
                        )
                        Text("${mini.nombre} (${mini.rutComercial})")
                    }
                }
                errorMinimarket?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Text("Seleccionar Producto / Equipo (Stock Admin):", fontWeight = FontWeight.Bold)
            if (listaInventario.isEmpty()) {
                Text("No hay stock en inventario.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            } else {
                listaInventario.forEach { inv ->
                    val stockDisp = inv.stockDisponible
                    val nombreMostrar = if (inv.nombreRepuesto.isNotBlank()) inv.nombreRepuesto else inv.nombre
                    val pVenta = inv.precioUnitario
                    val pArriendo = inv.precioArriendo

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (productoSeleccionado == inv),
                            onClick = {
                                productoSeleccionado = inv
                                errorProducto = null
                            }
                        )
                        Text("$nombreMostrar [${inv.tipoItem}] (Stock: $stockDisp | Venta: $${pVenta.toInt()} | Arriendo: $${pArriendo.toInt()})")
                    }
                }
                errorProducto?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            AnimatedVisibility(
                visible = productoSeleccionado != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                productoSeleccionado?.let { prod ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Vista previa del ítem seleccionado (${prod.tipoItem}):", fontWeight = FontWeight.Bold)
                            if (prod.descripcion.isNotBlank()) {
                                Text("Descripción: ${prod.descripcion}", style = MaterialTheme.typography.bodySmall)
                            }
                            GaleriaFotos(fotosUrlsString = prod.fotosUrls)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        cantidadTexto = it
                        errorCantidad = null
                    }
                },
                label = { Text("Cantidad a procesar") },
                isError = errorCantidad != null,
                supportingText = { errorCantidad?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = { if (errorCantidad != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalle del Cálculo Automático:", fontWeight = FontWeight.Bold)
                    Text("Precio Unitario Configurado ($tipoOperacion): $${String.format("%,.0f", precioUnitarioAuto)}")
                    Text("Cantidad seleccionada: $cantidadInt")
                    Text("Monto Total a Cobrar: $${String.format("%,.0f", montoTotalCalculado)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            AnimatedVisibility(
                visible = errorGeneral != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                errorGeneral?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(
                visible = exitoMensaje,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("$tipoOperacion registrada correctamente (Pendiente de Despacho).", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarRegistro() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Confirmar y Procesar $tipoOperacion")
            }
        }
    }
}
