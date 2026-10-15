@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun guardarUriLocal(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return uri.toString()
        val archivo = File(context.filesDir, "vis_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
        archivo.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
        archivo.toURI().toString()
    } catch (_: Exception) { uri.toString() }
}

private fun guardarBitmapLocal(context: Context, bitmap: Bitmap): String {
    return try {
        val archivo = File(context.filesDir, "cam_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
        archivo.outputStream().use { outputStream -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) }
        archivo.toURI().toString()
    } catch (_: Exception) { "" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteSolicitarVisitaScreen(
    minimarketCliente: Minimarket?,
    visitasExistentes: List<Visita>,
    onSolicitarVisita: (Visita) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var fechaSeleccionada by remember { mutableStateOf("") }
    var fechaMillisSeleccionada by remember { mutableLongStateOf(0L) }
    var bloqueHorario by remember { mutableStateOf("09:00 - 12:00") }
    var motivo by remember { mutableStateOf("") }
    var listaFotos by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    val diasAnticipacionMinimos = 2
    val calMinimo = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, diasAnticipacionMinimos)
    }
    val minTimeMillis = calMinimo.timeInMillis

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= minTimeMillis
        }
    )

    // LAUNCHERS CÁMARA Y GALERÍA CON GESTIÓN DE PERMISOS
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val fotoPath = guardarBitmapLocal(context, bitmap)
            if (fotoPath.isNotBlank()) listaFotos = listaFotos + fotoPath
        }
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) launcherCamara.launch(null)
        else Toast.makeText(context, "Se requiere permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show()
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val nuevas = uris.map { guardarUriLocal(context, it) }
            listaFotos = listaFotos + nuevas
        }
    }

    val permisoGaleria = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    val launcherPermisoGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) launcherGaleria.launch("image/*")
        else Toast.makeText(context, "Se requiere permiso para acceder a las fotos", Toast.LENGTH_LONG).show()
    }

    fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) launcherCamara.launch(null)
        else launcherPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    fun abrirGaleria() {
        if (ContextCompat.checkSelfPermission(context, permisoGaleria) == PackageManager.PERMISSION_GRANTED) launcherGaleria.launch("image/*")
        else launcherPermisoGaleria.launch(permisoGaleria)
    }

    fun procesarSolicitud() {
        if (minimarketCliente == null) { errorMensaje = "No se encontraron datos del minimarket."; return }
        if (fechaSeleccionada.isBlank()) { errorMensaje = "Selecciona una fecha en el calendario."; return }
        if (motivo.isBlank()) { errorMensaje = "Ingresa la descripción del problema."; return }
        if (listaFotos.isEmpty()) { errorMensaje = "Debes adjuntar al menos una foto de evidencia para el técnico."; return }

        val nuevaVisita = Visita(
            visitaId = "VIS-" + System.currentTimeMillis(),
            minimarketId = minimarketCliente.minimarketId,
            clienteNombre = minimarketCliente.nombre,
            direccion = minimarketCliente.direccion,
            fecha = fechaSeleccionada.trim(),
            bloqueHorario = bloqueHorario,
            motivo = motivo.trim(),
            estado = "Pendiente",
            fotosUrls = listaFotos.joinToString(",")
        )

        onSolicitarVisita(nuevaVisita)
        exitoMensaje = true
        errorMensaje = null
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
                        fechaSeleccionada = sdf.format(Date(millis))
                        fechaMillisSeleccionada = millis
                    }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Visita Presencial") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            minimarketCliente?.let { mini ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Datos del Minimarket:", fontWeight = FontWeight.Bold)
                        Text("Nombre: ${mini.nombre} | Dirección: ${mini.direccion}")
                    }
                }
            }

            OutlinedTextField(
                value = fechaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha Solicitada (Mínimo $diasAnticipacionMinimos días anticipación)") },
                trailingIcon = { IconButton(onClick = { mostrarCalendario = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth().clickable { mostrarCalendario = true },
                enabled = false
            )

            Text("Bloque Horario Preferido:", fontWeight = FontWeight.Bold)
            listOf("09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00").forEach { bloque ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (bloqueHorario == bloque), onClick = { bloqueHorario = bloque })
                    Text(bloque)
                }
            }

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Descripción detallada del problema") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // SECCIÓN ADJUNTAR EVIDENCIA (CÁMARA Y GALERÍA)
            Text("Adjuntar Evidencia (Foto del Equipo / Problema):", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { abrirCamara() }, modifier = Modifier.weight(1f)) {
                    Text("📷 Cámara")
                }
                OutlinedButton(onClick = { abrirGaleria() }, modifier = Modifier.weight(1f)) {
                    Text("🖼️ Galería")
                }
            }

            if (listaFotos.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaFotos) { fotoUri ->
                        Box(modifier = Modifier.size(90.dp)) {
                            Card(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(model = fotoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            }
                            IconButton(
                                onClick = { listaFotos = listaFotos.filter { it != fotoUri } },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            errorMensaje?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }

            if (exitoMensaje) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Solicitud con fotos enviada a los técnicos.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(onClick = { procesarSolicitud() }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text("Enviar Solicitud al Técnico")
            }
        }
    }
}