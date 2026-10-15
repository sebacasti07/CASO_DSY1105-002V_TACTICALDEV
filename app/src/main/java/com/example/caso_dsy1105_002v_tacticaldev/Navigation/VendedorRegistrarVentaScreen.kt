@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }

    val precioUnitarioAuto = remember(productoSeleccionado, tipoOperacion) {
        if (productoSeleccionado == null) 0.0
        else if (tipoOperacion == "Arriendo") productoSeleccionado!!.precioArriendo
        else productoSeleccionado!!.precioUnitario
    }

    val cantidadInt = cantidadTexto.toIntOrNull() ?: 0
    val montoTotalCalculado = cantidadInt * precioUnitarioAuto

    fun procesarRegistro() {
        if (minimarketSeleccionado == null) {
            errorMensaje = "Selecciona un Minimarket / Cliente."
            return
        }
        if (productoSeleccionado == null) {
            errorMensaje = "Selecciona un producto o equipo del inventario."
            return
        }

        val stockActual = productoSeleccionado?.let { if (it.stockDisponible > 0) it.stockDisponible else it.cantidadDisponible } ?: 0

        if (cantidadInt <= 0 || cantidadInt > stockActual) {
            errorMensaje = "Cantidad no válida o stock insuficiente ($stockActual disp.)."
            return
        }

        if (precioUnitarioAuto <= 0) {
            errorMensaje = "El producto seleccionado no tiene configurado un precio válido para $tipoOperacion."
            return
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        val inventarioActualizado = productoSeleccionado?.let {
            val nuevoStock = stockActual - cantidadInt
            it.copy(
                stockDisponible = nuevoStock,
                cantidadDisponible = nuevoStock
            )
        }

        val nombreProd = productoSeleccionado?.let { if (it.nombreRepuesto.isNotBlank()) it.nombreRepuesto else it.nombre } ?: ""

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
            estado = "Completado",
            descripcion = productoSeleccionado?.descripcion ?: "",
            fotosUrls = productoSeleccionado?.fotosUrls ?: ""
        )

        onRegistrarOperacion(nuevaVenta, inventarioActualizado)
        exitoMensaje = true
        errorMensaje = null
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
                            onClick = { minimarketSeleccionado = mini }
                        )
                        Text("${mini.nombre} (${mini.rutComercial})")
                    }
                }
            }

            Text("Seleccionar Producto / Equipo (Stock Admin):", fontWeight = FontWeight.Bold)
            if (listaInventario.isEmpty()) {
                Text("No hay stock en inventario.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            } else {
                listaInventario.forEach { inv ->
                    val stockDisp = if (inv.stockDisponible > 0) inv.stockDisponible else inv.cantidadDisponible
                    val nombreMostrar = if (inv.nombreRepuesto.isNotBlank()) inv.nombreRepuesto else inv.nombre
                    val pVenta = inv.precioUnitario
                    val pArriendo = inv.precioArriendo

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (productoSeleccionado == inv),
                            onClick = { productoSeleccionado = inv }
                        )
                        Text("$nombreMostrar (Stock: $stockDisp | Venta: $${pVenta.toInt()} | Arriendo: $${pArriendo.toInt()})")
                    }
                }
            }

            productoSeleccionado?.let { prod ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Vista previa del equipo seleccionado:", fontWeight = FontWeight.Bold)
                        if (prod.descripcion.isNotBlank()) {
                            Text("Descripción: ${prod.descripcion}", style = MaterialTheme.typography.bodySmall)
                        }
                        GaleriaFotos(fotosUrlsString = prod.fotosUrls)
                    }
                }
            }

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = { if (it.all { char -> char.isDigit() }) cantidadTexto = it },
                label = { Text("Cantidad a procesar") },
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

            errorMensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            if (exitoMensaje) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("$tipoOperacion registrada correctamente. Equipo asignado al cliente.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarRegistro() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Confirmar y Procesar $tipoOperacion")
            }
        }
    }
}