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
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteSolicitarCotizacionScreen(
    minimarketCliente: Minimarket?,
    cotizacionesExistentes: List<Venta>,
    onSolicitarCotizacion: (Venta) -> Unit,
    onVolver: () -> Unit
) {
    var productoNombre by remember { mutableStateOf("") }
    var cantidadTexto by remember { mutableStateOf("1") }
    var detalle by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }

    fun procesarSolicitud() {
        if (minimarketCliente == null) {
            errorMensaje = "No se encontraron datos del minimarket."
            return
        }
        if (productoNombre.isBlank()) {
            errorMensaje = "Ingresa el equipo o repuesto a cotizar."
            return
        }

        val cant = cantidadTexto.toIntOrNull() ?: 0
        if (cant <= 0) {
            errorMensaje = "Ingresa una cantidad válida mayor a 0."
            return
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        val nuevaCotizacion = Venta(
            ventaId = "COT-" + System.currentTimeMillis(),
            vendedorId = "",
            vendedorNombre = "Sin Asignar",
            clienteNombre = minimarketCliente.nombre,
            minimarketId = minimarketCliente.minimarketId,
            repuestoId = "",
            productoNombre = productoNombre.trim(),
            tipoOperacion = "Cotización",
            cantidad = cant,
            precioUnitario = 0.0,
            total = 0.0,
            montoTotal = 0.0,
            fecha = fechaActual,
            estado = "Cotización Pendiente",
            detalle = detalle.trim(),
            descripcion = detalle.trim(),
            fotosUrls = ""
        )

        onSolicitarCotizacion(nuevaCotizacion)
        exitoMensaje = true
        errorMensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Cotización") },
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
                        Text("Cliente: ${mini.nombre}", fontWeight = FontWeight.Bold)
                        Text("RUT Comercial: ${mini.rutComercial}")
                    }
                }
            }

            OutlinedTextField(
                value = productoNombre,
                onValueChange = { productoNombre = it },
                label = { Text("Equipo o Repuesto a Cotizar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = { if (it.all { char -> char.isDigit() }) cantidadTexto = it },
                label = { Text("Cantidad Requerida") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = detalle,
                onValueChange = { detalle = it },
                label = { Text("Detalles Adicionales o Especificaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            errorMensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            if (exitoMensaje) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cotización enviada. Un vendedor responderá pronto.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarSolicitud() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enviar Cotización")
            }
        }
    }
}