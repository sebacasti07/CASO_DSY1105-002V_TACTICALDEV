@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteCatalogoEquiposScreen(
    listaInventario: List<InventarioRepuesto>,
    onSolicitarCotizacionClick: () -> Unit,
    onVolver: () -> Unit
) {
    var filtroTexto by remember { mutableStateOf("") }

    val listaFiltrada = remember(filtroTexto, listaInventario) {
        if (filtroTexto.isBlank()) listaInventario
        else listaInventario.filter {
            val nombre = it.nombreRepuesto.ifBlank { it.nombre }
            nombre.contains(filtroTexto, ignoreCase = true) ||
                    it.categoria.contains(filtroTexto, ignoreCase = true) ||
                    it.tipo.contains(filtroTexto, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vitrina de Equipos y Repuestos") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = filtroTexto,
                onValueChange = { filtroTexto = it },
                label = { Text("Buscar en el catálogo...") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay equipos o repuestos disponibles en el catálogo.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaFiltrada) { item ->
                        val nombreMostrar = item.nombreRepuesto.ifBlank { item.nombre }
                        val stockDisp = if (item.stockDisponible > 0) item.stockDisponible else item.cantidadDisponible

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // 1. FOTO GRANDE Y NAVEGABLE ARRIBA
                                GaleriaFotos(fotosUrlsString = item.fotosUrls)

                                Spacer(modifier = Modifier.height(12.dp))

                                // 2. INFORMACIÓN DETALLADA ABAJO
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = nombreMostrar,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(if (stockDisp > 0) "Disponible" else "Agotado") }
                                    )
                                }

                                Text("Categoría: ${item.categoria.ifBlank { item.tipo }}", style = MaterialTheme.typography.bodySmall)

                                if (item.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Precio Venta: $${String.format(Locale.getDefault(), "%,.0f", item.precioUnitario)}",
                                            style = MaterialTheme.typography.titleSmall,
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
                                    Text("Stock: $stockDisp un.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = onSolicitarCotizacionClick,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Solicitar Cotización")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}