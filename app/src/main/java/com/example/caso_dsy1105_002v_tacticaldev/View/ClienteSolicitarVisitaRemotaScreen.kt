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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Warning
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
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun guardarUriLocalRemota(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return uri.toString()
        val archivo = File(context.filesDir, "rem_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
        archivo.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
        archivo.toURI().toString()
    } catch (_: Exception) { uri.toString() }
}

private fun guardarBitmapLocalRemota(context: Context, bitmap: Bitmap): String {
    return try {
        val archivo = File(context.filesDir, "cam_rem_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
        archivo.outputStream().use { outputStream -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) }
        archivo.toURI().toString()
    } catch (_: Exception) { "" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteSolicitarVisitaRemotaScreen(
    minimarketCliente: Minimarket?,
    asistenciasExistentes: List<AsistenciaRemota> = emptyList(),
    onSolicitarRemota: (AsistenciaRemota) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var fechaSeleccionada by remember { mutableStateOf("") }
    var diaSemanaSeleccionado by remember { mutableIntStateOf(Calendar.MONDAY) }
    var bloqueHorario by remember { mutableStateOf("09:00 - 12:00") }
    var motivo by remember { mutableStateOf("") }
    var listaFotos by remember { mutableStateOf<List<String>>(emptyList()) }

    var errorFecha by remember { mutableStateOf<String?>(null) }
    var errorMotivo by remember { mutableStateOf<String?>(null) }
    var errorFotos by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    val diasAnticipacionMinimos = 1
    val calMinimo = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, diasAnticipacionMinimos)
    }
    val minTimeMillis = calMinimo.timeInMillis

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (utcTimeMillis < minTimeMillis) return false
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcTimeMillis }
                return cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
            }
        }
    )

    val opcionesBloques = remember(diaSemanaSeleccionado) {
        if (diaSemanaSeleccionado == Calendar.SATURDAY) listOf("09:00 - 11:00", "12:00 - 14:00")
        else listOf("09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00")
    }

    LaunchedEffect(opcionesBloques) {
        if (bloqueHorario !in opcionesBloques) {
            bloqueHorario = opcionesBloques.first()
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val path = guardarBitmapLocalRemota(context, bitmap)
            if (path.isNotBlank()) {
                listaFotos = listaFotos + path
                errorFotos = null
            }
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
            val nuevas = uris.map { guardarUriLocalRemota(context, it) }
            listaFotos = listaFotos + nuevas
            errorFotos = null
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

    fun validar(): Boolean {
        if (minimarketCliente == null) {
            errorGeneral = "No se encontraron datos del minimarket asociado al cliente."
            return false
        } else {
            errorGeneral = null
        }

        errorFecha = if (fechaSeleccionada.isBlank()) "Selecciona una fecha en el calendario" else null
        errorMotivo = if (motivo.isBlank()) "Ingresa la descripción del problema" else null
        errorFotos = if (listaFotos.isEmpty()) "Debes adjuntar al menos una foto de evidencia para el diagnóstico remoto" else null

        return errorFecha == null && errorMotivo == null && errorFotos == null && errorGeneral == null
    }

    fun procesarSolicitud() {
        if (!validar()) return

        val nuevaAsistencia = AsistenciaRemota(
            asistenciaId = "REM-" + System.currentTimeMillis(),
            minimarketId = minimarketCliente!!.minimarketId,
            clienteNombre = minimarketCliente.nombre,
            fecha = fechaSeleccionada.trim(),
            bloqueHorario = bloqueHorario,
            motivo = motivo.trim(),
            estado = "Pendiente",
            fotosUrls = listaFotos.joinToString(",")
        )

        onSolicitarRemota(nuevaAsistencia)
        exitoMensaje = true
        errorGeneral = null
        onVolver()
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
                        fechaSeleccionada = sdf.format(Date(millis))
                        diaSemanaSeleccionado = cal.get(Calendar.DAY_OF_WEEK)
                        errorFecha = null
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
                title = { Text("Solicitar Asistencia Remota") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
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
                        Text("Datos del Minimarket:", fontWeight = FontWeight.Bold)
                        Text("Nombre: ${mini.nombre} | Dirección: ${mini.direccion}")
                    }
                }
            }

            OutlinedTextField(
                value = fechaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha Solicitada") },
                isError = errorFecha != null,
                supportingText = { errorFecha?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (errorFecha != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        IconButton(onClick = { mostrarCalendario = true }) { Icon(Icons.Default.DateRange, contentDescription = null) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarCalendario = true }
            )

            Text("Bloques Horarios Disponibles:", fontWeight = FontWeight.Bold)
            opcionesBloques.forEach { bloque ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (bloqueHorario == bloque), onClick = { bloqueHorario = bloque })
                    Text(bloque)
                }
            }

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it; errorMotivo = null },
                label = { Text("Descripción del problema para el diagnóstico remoto") },
                isError = errorMotivo != null,
                supportingText = { errorMotivo?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = { if (errorMotivo != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Adjuntar Evidencia (Foto / Video del Estado del Equipo):", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { abrirCamara() }, modifier = Modifier.weight(1f)) {
                    Text("📷 Cámara")
                }
                OutlinedButton(onClick = { abrirGaleria() }, modifier = Modifier.weight(1f)) {
                    Text("🖼️ Galería")
                }
            }

            errorFotos?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
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
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = errorGeneral != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                errorGeneral?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
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
                        Text("Solicitud remota enviada al Técnico.", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { procesarSolicitud() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Enviar Solicitud al Técnico")
            }
        }
    }
}
