@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorInventarioScreen(
    listaInventario: List<InventarioRepuesto>,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario Asignado por Admin") },
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
            if (listaInventario.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay repuestos o equipos en el inventario.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaInventario) { inv ->
                        val stockDisp = if (inv.stockDisponible > 0) inv.stockDisponible else inv.cantidadDisponible
                        val nombreMostrar = inv.nombreRepuesto.ifBlank { inv.nombre }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // 1. FOTO ARRIBA
                                GaleriaFotos(fotosUrlsString = inv.fotosUrls)

                                Spacer(modifier = Modifier.height(12.dp))

                                // 2. DETALLES ABAJO
                                Text(nombreMostrar, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Categoría / Tipo: ${inv.categoria.ifBlank { inv.tipo }}", style = MaterialTheme.typography.bodySmall)

                                if (inv.descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Descripción: ${inv.descripcion}", style = MaterialTheme.typography.bodyMedium)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Stock Disponible: $stockDisp unidades",
                                    fontWeight = FontWeight.Bold,
                                    color = if (stockDisp < 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                Text("Precio Venta: $${String.format(Locale.getDefault(), "%,.0f", inv.precioUnitario)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text("Precio Arriendo: $${String.format(Locale.getDefault(), "%,.0f", inv.precioArriendo)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }
    }
}