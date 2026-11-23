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
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import com.example.caso_dsy1105_002v_tacticaldev.Util.ValidationUtils
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

    var errorProducto by remember { mutableStateOf<String?>(null) }
    var errorCantidad by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }

    fun validar(): Boolean {
        if (minimarketCliente == null) {
            errorGeneral = "No se encontraron datos del minimarket asociado al cliente."
            return false
        } else {
            errorGeneral = null
        }

        errorProducto = if (productoNombre.isBlank()) "Ingresa el equipo o repuesto a cotizar" else null

        val cant = cantidadTexto.toIntOrNull()
        errorCantidad = if (cant == null || cant <= 0) "Ingresa una cantidad válida mayor a 0" else null

        return errorProducto == null && errorCantidad == null && errorGeneral == null
    }

    fun procesarSolicitud() {
        if (!validar()) return

        val cant = cantidadTexto.toInt()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        val nuevaCotizacion = Venta(
            ventaId = "COT-" + System.currentTimeMillis(),
            vendedorId = "",
            vendedorNombre = "Sin Asignar",
            clienteNombre = minimarketCliente!!.nombre,
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
        errorGeneral = null
        onVolver()
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
                onValueChange = { productoNombre = it; errorProducto = null },
                label = { Text("Equipo o Repuesto a Cotizar") },
                isError = errorProducto != null,
                supportingText = { errorProducto?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = { if (errorProducto != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        cantidadTexto = it
                        errorCantidad = null
                    }
                },
                label = { Text("Cantidad Requerida") },
                isError = errorCantidad != null,
                supportingText = { errorCantidad?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = { if (errorCantidad != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
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
                        Text("Cotización enviada. Un vendedor responderá pronto.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarSolicitud() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Enviar Cotización")
            }
        }
    }
}
