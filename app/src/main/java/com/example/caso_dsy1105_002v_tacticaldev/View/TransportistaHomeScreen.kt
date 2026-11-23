package com.example.caso_dsy1105_002v_tacticaldev.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import java.util.concurrent.Executors

@SuppressLint("MissingPermission")
private fun obtenerUbicacionGpsRealTransportista(context: Context, onUbicacionObtenida: (String) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            onUbicacionObtenida("GPS Desactivado. Por favor activa la ubicación.")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val provider = if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
            locationManager.getCurrentLocation(
                provider,
                CancellationSignal(),
                Executors.newSingleThreadExecutor()
            ) { location: Location? ->
                if (location != null) {
                    onUbicacionObtenida("Lat: ${String.format("%.5f", location.latitude)}, Lon: ${String.format("%.5f", location.longitude)}")
                } else {
                    val fallback = locationManager.getLastKnownLocation(provider)
                    if (fallback != null) {
                        onUbicacionObtenida("Lat: ${String.format("%.5f", fallback.latitude)}, Lon: ${String.format("%.5f", fallback.longitude)}")
                    } else {
                        onUbicacionObtenida("Buscando señal GPS... Reintenta.")
                    }
                }
            }
        } else {
            val provider = if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
            locationManager.requestSingleUpdate(
                provider,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        onUbicacionObtenida("Lat: ${String.format("%.5f", location.latitude)}, Lon: ${String.format("%.5f", location.longitude)}")
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                    override fun onProviderEnabled(p0: String) {}
                    override fun onProviderDisabled(p0: String) {}
                },
                null
            )
        }
    } catch (e: Exception) {
        onUbicacionObtenida("Error GPS: ${e.localizedMessage}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportistaHomeScreen(
    listaDespachos: List<Venta> = emptyList(),
    listaMinimarkets: List<Minimarket> = emptyList(),
    onActualizarEstadoDespacho: (Venta) -> Unit = {},
    onLogout: () -> Unit
) {
    var filtroEstado by remember { mutableStateOf("Todos") }
    var busquedaTexto by remember { mutableStateOf("") }
    var despachoAConfirmarEntrega by remember { mutableStateOf<Venta?>(null) }
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    val soloDespachos = remember(listaDespachos) {
        listaDespachos.filter {
            it.tipoOperacion != "Cotización" && !it.estado.contains("Cotización", ignoreCase = true)
        }
    }

    val despachosFiltrados = remember(soloDespachos, filtroEstado, busquedaTexto) {
        soloDespachos.filter { despacho ->
            val coincideFiltro = when (filtroEstado) {
                "Pendiente de Despacho" -> despacho.estado.contains("Pendiente", ignoreCase = true)
                "En Ruta" -> despacho.estado.contains("En Ruta", ignoreCase = true)
                "Entregado" -> despacho.estado.contains("Entregado", ignoreCase = true)
                else -> true
            }
            val coincideBusqueda = busquedaTexto.isBlank() ||
                    despacho.ventaId.contains(busquedaTexto, ignoreCase = true) ||
                    despacho.productoNombre.contains(busquedaTexto, ignoreCase = true) ||
                    despacho.clienteNombre.contains(busquedaTexto, ignoreCase = true)

            coincideFiltro && coincideBusqueda
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Módulo de Logística y Despachos") },
                actions = {
                    IconButton(onClick = { mostrarDialogoLogout = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Hoja de Ruta del Transportista", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = busquedaTexto,
                onValueChange = { busquedaTexto = it },
                label = { Text("Buscar por ID, Producto o Cliente") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Todos", "Pendiente de Despacho", "En Ruta", "Entregado").forEach { estadoOption ->
                    FilterChip(
                        selected = (filtroEstado == estadoOption),
                        onClick = { filtroEstado = estadoOption },
                        label = { Text(estadoOption, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (despachosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay despachos registrados para este filtro.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(despachosFiltrados) { despacho ->
                        val minimarketAsociado = remember(listaMinimarkets, despacho.minimarketId) {
                            listaMinimarkets.find { it.minimarketId == despacho.minimarketId }
                        }

                        val direccion = minimarketAsociado?.direccion?.ifBlank { "Dirección no especificada" } ?: "Dirección no especificada"
                        val telefono = minimarketAsociado?.telefono?.ifBlank { "Teléfono no especificado" } ?: "Teléfono no especificado"

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
                                    Text("Orden ID: ${despacho.ventaId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(despacho.estado) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Producto / Equipo: ${despacho.productoNombre}", fontWeight = FontWeight.Bold)
                                Text("Cantidad: ${despacho.cantidad} unidad(es)")
                                Text("Tipo Operación: ${despacho.tipoOperacion}")

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Text("Datos de Entrega:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Minimarket: ${despacho.clienteNombre}")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Dirección: $direccion", fontWeight = FontWeight.Bold)
                                }
                                Text("Teléfono de Contacto: $telefono")

                                if (despacho.ubicacionGps.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📍 GPS Entrega: ${despacho.ubicacionGps}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }

                                if (despacho.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Observaciones: ${despacho.descripcion}", style = MaterialTheme.typography.bodySmall)
                                }

                                GaleriaFotos(fotosUrlsString = despacho.fotosUrls)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (despacho.estado.contains("Pendiente", ignoreCase = true)) {
                                        Button(
                                            onClick = {
                                                onActualizarEstadoDespacho(despacho.copy(estado = "En Ruta"))
                                            }
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Iniciar Ruta")
                                        }
                                    }

                                    if (despacho.estado.contains("En Ruta", ignoreCase = true)) {
                                        Button(
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            onClick = { despachoAConfirmarEntrega = despacho }
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Confirmar Entrega")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    despachoAConfirmarEntrega?.let { despacho ->
        DialogoConfirmarEntregaTransportista(
            despacho = despacho,
            onDismiss = { despachoAConfirmarEntrega = null },
            onConfirmarEntrega = { gpsEntrega ->
                onActualizarEstadoDespacho(despacho.copy(estado = "Entregado", ubicacionGps = gpsEntrega))
                despachoAConfirmarEntrega = null
            }
        )
    }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Está seguro de que desea cerrar sesión?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        mostrarDialogoLogout = false
                        onLogout()
                    }
                ) {
                    Text("Sí, Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DialogoConfirmarEntregaTransportista(
    despacho: Venta,
    onDismiss: () -> Unit,
    onConfirmarEntrega: (String) -> Unit
) {
    val context = LocalContext.current
    var gpsEntrega by remember { mutableStateOf(despacho.ubicacionGps) }

    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            obtenerUbicacionGpsRealTransportista(context) { loc -> gpsEntrega = loc }
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun solicitarGps() {
        val tieneFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val tieneCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (tieneFine || tieneCoarse) {
            obtenerUbicacionGpsRealTransportista(context) { loc -> gpsEntrega = loc }
        } else {
            launcherPermisoUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val esGpsValido = gpsEntrega.startsWith("Lat:", ignoreCase = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Entrega de Despacho") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "¿Confirmas que el producto '${despacho.productoNombre}' fue entregado en '${despacho.clienteNombre}'?\n\nEs obligatoria la captura de la coordenada GPS para registrar la entrega."
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (gpsEntrega.isNotBlank()) "GPS: $gpsEntrega" else "GPS: No capturado",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (esGpsValido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }

                OutlinedButton(
                    onClick = { solicitarGps() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📍 Capturar Ubicación GPS Actual")
                }
            }
        },
        confirmButton = {
            Button(
                enabled = esGpsValido,
                onClick = { onConfirmarEntrega(gpsEntrega) }
            ) {
                Text("Confirmar y Registrar Entrega")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
