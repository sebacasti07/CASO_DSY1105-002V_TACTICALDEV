@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecnicoInventarioRepuestosScreen(
    listaInventario: List<InventarioRepuesto>,
    onVolver: () -> Unit
) {
    // FILTRADO EXCLUSIVO DE REPUESTOS
    val repuestosSolamente = remember(listaInventario) {
        listaInventario.filter { item ->
            val esRepuesto = item.tipoItem.equals("Repuesto", ignoreCase = true) || item.tipoEquipo.isBlank()
            esRepuesto
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario de Repuestos (Técnico)") },
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
            if (repuestosSolamente.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay repuestos registrados en el inventario.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(repuestosSolamente) { rep ->
                        val stock = if (rep.stockDisponible > 0) rep.stockDisponible else rep.cantidadDisponible
                        val nombreRep = rep.nombreRepuesto.ifBlank { rep.nombre }

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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(nombreRep, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Stock: $stock un.") }
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Categoría: ${rep.categoria.ifBlank { rep.tipo }}", style = MaterialTheme.typography.bodySmall)
                                Text("Ubicación Bodega: ${rep.bodegaUbicacion.ifBlank { "Bodega Central" }}", style = MaterialTheme.typography.bodySmall)

                                if (rep.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Descripción: ${rep.descripcion}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}