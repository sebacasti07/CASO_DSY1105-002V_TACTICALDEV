@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import java.io.File
import java.util.Locale

private fun guardarImagenLocal(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return uri.toString()
        val archivo = File(context.filesDir, "img_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
        archivo.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        archivo.toURI().toString()
    } catch (_: Exception) {
        uri.toString()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventarioScreen(
    listaInventario: List<InventarioRepuesto>,
    onAgregarStock: (InventarioRepuesto) -> Unit,
    onEliminarStock: (InventarioRepuesto) -> Unit,
    onVolver: () -> Unit
) {
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var itemAEditar by remember { mutableStateOf<InventarioRepuesto?>(null) }
    var itemAEliminarConfirm by remember { mutableStateOf<InventarioRepuesto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Inventario y Stock") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Stock")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (listaInventario.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay repuestos ni equipos en el inventario.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaInventario) { item ->
                        val stockActual = if (item.stockDisponible > 0) item.stockDisponible else item.cantidadDisponible
                        val nombreMostrar = item.nombreRepuesto.ifBlank { item.nombre }
                        val esEquipo = item.tipoItem.equals("Equipo", ignoreCase = true)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // 1. FOTO GRANDE Y INTERACTIVA ARRIBA
                                GaleriaFotos(fotosUrlsString = item.fotosUrls)

                                Spacer(modifier = Modifier.height(12.dp))

                                // 2. ENCABEZADO CON NOMBRE, TIPO Y BOTONES DE ACCIÓN
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = nombreMostrar.ifBlank { "Ítem Sin Nombre" },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            AssistChip(
                                                onClick = {},
                                                label = { Text(if (esEquipo) "Equipo: ${item.tipoEquipo.ifBlank { "Refrigeración" }}" else "Repuesto") }
                                            )
                                        }
                                        Text("ID Inventario: ${item.inventarioId}", style = MaterialTheme.typography.bodySmall)
                                        Text("Categoría: ${item.categoria.ifBlank { item.tipo }}", style = MaterialTheme.typography.bodySmall)
                                    }

                                    Row {
                                        IconButton(onClick = { itemAEditar = item }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { itemAEliminarConfirm = item }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }

                                if (item.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Descripción: ${item.descripcion}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // 3. INFORMACIÓN DE PRECIOS Y STOCK DISPONIBLE
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Precio Venta: $${String.format(Locale.getDefault(), "%,.0f", item.precioUnitario)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (item.precioArriendo > 0) {
                                            Text(
                                                text = "Precio Arriendo: $${String.format(Locale.getDefault(), "%,.0f", item.precioArriendo)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Stock: $stockActual un.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stockActual < 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoCrear) {
        DialogoFormularioInventario(
            itemExistente = null,
            onDismiss = { mostrarDialogoCrear = false },
            onGuardar = { nuevo ->
                onAgregarStock(nuevo)
                mostrarDialogoCrear = false
            }
        )
    }

    itemAEditar?.let { item ->
        DialogoFormularioInventario(
            itemExistente = item,
            onDismiss = { itemAEditar = null },
            onGuardar = { itemActualizado ->
                onAgregarStock(itemActualizado)
                itemAEditar = null
            }
        )
    }

    itemAEliminarConfirm?.let { item ->
        AlertDialog(
            onDismissRequest = { itemAEliminarConfirm = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar '${item.nombreRepuesto.ifBlank { item.nombre }}' del inventario?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        onEliminarStock(item)
                        itemAEliminarConfirm = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemAEliminarConfirm = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DialogoFormularioInventario(
    itemExistente: InventarioRepuesto?,
    onDismiss: () -> Unit,
    onGuardar: (InventarioRepuesto) -> Unit
) {
    val context = LocalContext.current

    var tipoItem by remember { mutableStateOf(itemExistente?.tipoItem?.ifBlank { "Repuesto" } ?: "Repuesto") }
    var tipoEquipo by remember { mutableStateOf(itemExistente?.tipoEquipo?.ifBlank { "Visicooler" } ?: "Visicooler") }
    var nombre by remember { mutableStateOf(itemExistente?.let { it.nombreRepuesto.ifBlank { it.nombre } } ?: "") }
    var categoria by remember { mutableStateOf(itemExistente?.let { it.categoria.ifBlank { it.tipo } } ?: "General") }
    var cantidadTexto by remember { mutableStateOf(itemExistente?.let { if (it.stockDisponible > 0) it.stockDisponible.toString() else it.cantidadDisponible.toString() } ?: "") }
    var precioVentaTexto by remember { mutableStateOf(itemExistente?.precioUnitario?.let { if (it > 0) it.toInt().toString() else "" } ?: "") }
    var precioArriendoTexto by remember { mutableStateOf(itemExistente?.precioArriendo?.let { if (it > 0) it.toInt().toString() else "" } ?: "") }
    var bodega by remember { mutableStateOf(itemExistente?.bodegaUbicacion?.ifBlank { "Bodega Central" } ?: "Bodega Central") }
    var descripcion by remember { mutableStateOf(itemExistente?.descripcion ?: "") }

    var listaFotos by remember {
        mutableStateOf(
            itemExistente?.fotosUrls?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    // LAUNCHER PARA SELECCIONAR MÚLTIPLES FOTOS
    val launcherFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val nuevasFotos = uris.map { uri -> guardarImagenLocal(context, uri) }
            listaFotos = listaFotos + nuevasFotos
        }
    }

    // PERMISO DE ALMACENAMIENTO / GALERÍA SEGÚN LA VERSIÓN DE ANDROID
    val permisoGaleria = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // LAUNCHER PARA SOLICITAR PERMISO DEL SISTEMA
    val launcherPermisoGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            launcherFotos.launch("image/*")
        } else {
            Toast.makeText(context, "Se requiere permiso para acceder a las fotos de la galería", Toast.LENGTH_LONG).show()
        }
    }

    fun solicitarPermisoYSeleccionarFotos() {
        val tienePermiso = ContextCompat.checkSelfPermission(context, permisoGaleria) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) {
            launcherFotos.launch("image/*")
        } else {
            launcherPermisoGaleria.launch(permisoGaleria)
        }
    }

    var errorMensaje by remember { mutableStateOf<String?>(null) }
    val tiposEquipoOpciones = listOf("Visicooler", "Congeladora Horizontal", "Vitrina Refrigerada", "Conservadora", "Cámara Fría", "Otro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (itemExistente == null) "Agregar al Inventario" else "Editar Inventario / Precios") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Clasificación del Ítem:", fontWeight = FontWeight.Bold)
                Row {
                    FilterChip(
                        selected = (tipoItem == "Repuesto"),
                        onClick = { tipoItem = "Repuesto" },
                        label = { Text("Repuesto") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = (tipoItem == "Equipo"),
                        onClick = { tipoItem = "Equipo" },
                        label = { Text("Equipo Refrigeración") }
                    )
                }

                if (tipoItem == "Equipo") {
                    Text("Tipo de Equipo:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    tiposEquipoOpciones.forEach { opcion ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (tipoEquipo == opcion),
                                onClick = { tipoEquipo = opcion }
                            )
                            Text(opcion)
                        }
                    }
                }

                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Producto / Equipo") }, singleLine = true)
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, singleLine = true)
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) cantidadTexto = it },
                    label = { Text("Stock Disponible") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = precioVentaTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) precioVentaTexto = it },
                    label = { Text("Precio Venta Unitario ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = precioArriendoTexto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) precioArriendoTexto = it },
                    label = { Text("Precio Arriendo Unitario ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(value = bodega, onValueChange = { bodega = it }, label = { Text("Bodega / Ubicación") }, singleLine = true)

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del Equipo / Repuesto") },
                    minLines = 2,
                    maxLines = 4
                )

                Text("Fotos del Equipo (Galería):", fontWeight = FontWeight.Bold)

                // BOTÓN CON SOLICITUD DE PERMISO NATIVO AL SISTEMA
                OutlinedButton(
                    onClick = { solicitarPermisoYSeleccionarFotos() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📷 Seleccionar Fotos de la Galería")
                }

                if (listaFotos.isNotEmpty()) {
                    Text("Fotos Adjuntadas (${listaFotos.size}):", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listaFotos) { fotoUri ->
                            Box(modifier = Modifier.size(80.dp)) {
                                Card(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = fotoUri,
                                        contentDescription = "Foto elegida",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                IconButton(
                                    onClick = { listaFotos = listaFotos.filter { it != fotoUri } },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Eliminar foto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                errorMensaje?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val cant = cantidadTexto.toIntOrNull() ?: 0
                val pVenta = precioVentaTexto.toDoubleOrNull() ?: 0.0
                val pArriendo = precioArriendoTexto.toDoubleOrNull() ?: 0.0

                if (nombre.isBlank() || cant <= 0 || pVenta <= 0) {
                    errorMensaje = "Ingresa nombre, stock y precio de venta válido mayor a 0."
                    return@Button
                }

                val itemGuardado = InventarioRepuesto(
                    inventarioId = itemExistente?.inventarioId ?: ("INV-" + System.currentTimeMillis()),
                    repuestoId = itemExistente?.repuestoId ?: ("REP-" + System.currentTimeMillis()),
                    nombre = nombre.trim(),
                    nombreRepuesto = nombre.trim(),
                    tipo = categoria.trim(),
                    tipoItem = tipoItem,
                    tipoEquipo = if (tipoItem == "Equipo") tipoEquipo else "",
                    categoria = categoria.trim(),
                    cantidadDisponible = cant,
                    stockDisponible = cant,
                    precioUnitario = pVenta,
                    precioArriendo = pArriendo,
                    bodegaUbicacion = bodega.trim(),
                    descripcion = descripcion.trim(),
                    fotosUrls = listaFotos.joinToString(",")
                )
                onGuardar(itemGuardado)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}