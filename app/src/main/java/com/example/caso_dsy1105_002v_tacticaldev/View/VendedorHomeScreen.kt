package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorHomeScreen(
    onNavigateToCotizaciones: () -> Unit,
    onNavigateToRegistrarVenta: () -> Unit,
    onNavigateToInventario: () -> Unit,
    onNavigateToMisVentas: () -> Unit,
    onLogout: () -> Unit
) {
    var mostrarDialogoLogout by remember { mutableStateOf(false) }
    var valorUf by remember { mutableStateOf("Cargando valor UF...") }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.externalApiService.obtenerIndicadores()
            }
            if (response.isSuccessful) {
                val json = response.body()
                val ufValor = json?.getAsJsonObject("uf")?.get("valor")?.asDouble
                valorUf = if (ufValor != null) "$$ufValor" else "Valor UF no disponible"
            } else {
                valorUf = "Valor UF no disponible"
            }
        } catch (e: Exception) {
            valorUf = "Valor UF no disponible (Sin conexión)"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Vendedor - TacticalDEV") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Indicador Económico del Día", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Valor UF: $valorUf", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Text(text = "Gestión Comercial y Ventas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            VendedorMenuCard(
                titulo = "Solicitudes de Cotización",
                descripcion = "Revisa y responde cotizaciones requeridas por los clientes en tiempo real.",
                icono = Icons.Default.ShoppingCart,
                onClick = onNavigateToCotizaciones
            )

            VendedorMenuCard(
                titulo = "Registrar Venta o Arriendo",
                descripcion = "Procesa ventas directas o contratos de arriendo de maquinaria refrigerada.",
                icono = Icons.Default.Edit,
                onClick = onNavigateToRegistrarVenta
            )

            VendedorMenuCard(
                titulo = "Gestión de Inventario",
                descripcion = "Consulta la disponibilidad de equipos, precios de venta y arriendo asignados.",
                icono = Icons.AutoMirrored.Filled.List,
                onClick = onNavigateToInventario
            )

            VendedorMenuCard(
                titulo = "Mis Ventas Realizadas",
                descripcion = "Consulta el historial de todas las operaciones de venta y arriendo que has procesado.",
                icono = Icons.Default.CheckCircle,
                onClick = onNavigateToMisVentas
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { mostrarDialogoLogout = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
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
private fun VendedorMenuCard(titulo: String, descripcion: String, icono: ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
