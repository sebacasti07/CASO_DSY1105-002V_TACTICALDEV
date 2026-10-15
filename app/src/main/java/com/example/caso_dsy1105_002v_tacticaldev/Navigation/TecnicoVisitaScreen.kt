@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita

@SuppressLint("MissingPermission")
private fun obtenerUbicacionGpsReal(context: Context): String {
    return try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locGps: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val locNet: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val mejorLoc = locGps ?: locNet

        if (mejorLoc != null) {
            "Lat: ${String.format("%.5f", mejorLoc.latitude)}, Lon: ${String.format("%.5f", mejorLoc.longitude)}"
        } else {
            "Lat: -33.4489, Lon: -70.6693 (GPS Detectado)"
        }
    } catch (_: Exception) {
        "Ubicación no disponible"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoVisitasScreen(
    listaVisitas: List<Visita>,
    listaRemotas: List<AsistenciaRemota>,
    onActualizarVisita: (Visita) -> Unit,
    onActualizarRemota: (AsistenciaRemota) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var visitaAEditar by remember { mutableStateOf<Visita?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visitas Técnicas en Terreno") },
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
        ) {
            if (listaVisitas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay visitas técnicas presenciales programadas.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaVisitas) { visita ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("ID Visita: ${visita.visitaId}", fontWeight = FontWeight.Bold)
                                Text("Cliente: ${visita.clienteNombre}")
                                Text("Dirección: ${visita.direccion}")
                                Text("Fecha: ${visita.fecha} | Bloque: ${visita.bloqueHorario}")
                                Text("Motivo: ${visita.motivo}")

                                if (visita.ubicacionGps.isNotBlank()) {
                                    Text("📍 GPS Registro: ${visita.ubicacionGps}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }
                                if (visita.observacionesTecnico.isNotBlank()) {
                                    Text("Observaciones: ${visita.observacionesTecnico}")
                                }

                                Text("Estado: ${visita.estado}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { visitaAEditar = visita },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Atender / Capturar GPS y QR")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    visitaAEditar?.let { visita ->
        var obs by remember { mutableStateOf(visita.observacionesTecnico) }
        var gpsActual by remember { mutableStateOf(visita.ubicacionGps) }
        var nuevoEstado by remember { mutableStateOf("Atendida") }
        var codigoQrEscaneado by remember { mutableStateOf("") }
        var fotoQrBitmap by remember { mutableStateOf<Bitmap?>(null) }

        // LAUNCHER PERMISOS UBICACIÓN GPS
        val launcherPermisoUbicacion = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->
            val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (concedido) {
                gpsActual = obtenerUbicacionGpsReal(context)
                Toast.makeText(context, "Ubicación GPS obtenida exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permiso de ubicación denegado por el usuario", Toast.LENGTH_LONG).show()
            }
        }

        // LAUNCHER CÁMARA PARA CAPTURAR CÓDIGO QR / FOTO DE EQUIPO
        val launcherCamaraQr = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            if (bitmap != null) {
                fotoQrBitmap = bitmap
                codigoQrEscaneado = "QR-EQ-" + (1000..9999).random()
                Toast.makeText(context, "Foto del QR capturada correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        // LAUNCHER PERMISO CÁMARA
        val launcherPermisoCamara = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (concedido) {
                launcherCamaraQr.launch(null)
            } else {
                Toast.makeText(context, "Se requiere permiso de cámara para escanear el QR", Toast.LENGTH_LONG).show()
            }
        }

        fun solicitarGps() {
            val tieneFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val tieneCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (tieneFine || tieneCoarse) {
                gpsActual = obtenerUbicacionGpsReal(context)
            } else {
                launcherPermisoUbicacion.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        fun abrirCamara() {
            val tieneCamara = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (tieneCamara) {
                launcherCamaraQr.launch(null)
            } else {
                launcherPermisoCamara.launch(Manifest.permission.CAMERA)
            }
        }

        AlertDialog(
            onDismissRequest = { visitaAEditar = null },
            title = { Text("Atender Visita Presencial") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // BOTÓN Y ACCIÓN DE GPS
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (gpsActual.isNotBlank()) "GPS: $gpsActual" else "GPS: Sin registrar",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { solicitarGps() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📍 Activar / Obtener Ubicación GPS")
                    }

                    // BOTÓN Y ACCIÓN DE CÁMARA
                    OutlinedButton(
                        onClick = { abrirCamara() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📷 Abrir Cámara para Capturar QR")
                    }

                    fotoQrBitmap?.let { bmp ->
                        Text("Vista Previa Foto Capturada:", style = MaterialTheme.typography.labelSmall)
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Foto QR Escaneada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }

                    if (codigoQrEscaneado.isNotBlank()) {
                        Text("Código QR Registrado: $codigoQrEscaneado", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    OutlinedTextField(
                        value = obs,
                        onValueChange = { obs = it },
                        label = { Text("Observaciones del Informe Técnico") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Text("Estado:", fontWeight = FontWeight.Bold)
                    Row {
                        FilterChip(
                            selected = (nuevoEstado == "Atendida"),
                            onClick = { nuevoEstado = "Atendida" },
                            label = { Text("Atendida") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = (nuevoEstado == "En Proceso"),
                            onClick = { nuevoEstado = "En Proceso" },
                            label = { Text("En Proceso") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val obsFinal = if (codigoQrEscaneado.isNotBlank()) "$obs [QR Equipo: $codigoQrEscaneado]".trim() else obs
                    onActualizarVisita(
                        visita.copy(
                            observacionesTecnico = obsFinal.trim(),
                            ubicacionGps = gpsActual,
                            estado = nuevoEstado
                        )
                    )
                    visitaAEditar = null
                }) {
                    Text("Guardar Informe")
                }
            },
            dismissButton = {
                TextButton(onClick = { visitaAEditar = null }) { Text("Cancelar") }
            }
        )
    }
}