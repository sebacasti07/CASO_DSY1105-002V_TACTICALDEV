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
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVentasScreen(
    listaVentas: List<Venta>,
    onVolver: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var filtroBusqueda by remember { mutableStateOf("") }

    val titulosTabs = listOf("Ventas y Arriendos", "Cotizaciones Separadas")

    val ventasYArriendos = remember(listaVentas) {
        listaVentas.filter { it.tipoOperacion != "Cotización" && !it.estado.contains("Cotización", ignoreCase = true) }
    }

    val cotizaciones = remember(listaVentas) {
        listaVentas.filter { it.tipoOperacion == "Cotización" || it.estado.contains("Cotización", ignoreCase = true) }
    }

    val listaMostrar = if (tabSeleccionada == 0) ventasYArriendos else cotizaciones

    val listaFiltrada = remember(filtroBusqueda, listaMostrar) {
        if (filtroBusqueda.isBlank()) listaMostrar
        else listaMostrar.filter {
            it.vendedorNombre.contains(filtroBusqueda, ignoreCase = true) ||
                    it.productoNombre.contains(filtroBusqueda, ignoreCase = true) ||
                    it.clienteNombre.contains(filtroBusqueda, ignoreCase = true) ||
                    it.tipoOperacion.contains(filtroBusqueda, ignoreCase = true)
        }
    }

    val totalRecaudado = remember(ventasYArriendos) {
        ventasYArriendos.sumOf { if (it.montoTotal > 0) it.montoTotal else it.total }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión Comercial Administrador") },
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
        ) {
            TabRow(selectedTabIndex = tabSeleccionada) {
                titulosTabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = { Text(titulo) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (tabSeleccionada == 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Resumen Ventas y Arriendos Efectuados", fontWeight = FontWeight.Bold)
                            Text("Total Registros CERRADOS: ${ventasYArriendos.size}")
                            Text("Monto Total Recaudado: $${String.format("%,.0f", totalRecaudado)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                OutlinedTextField(
                    value = filtroBusqueda,
                    onValueChange = { filtroBusqueda = it },
                    label = { Text("Buscar por Vendedor, Producto o Cliente") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (listaFiltrada.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (tabSeleccionada == 0) "No hay registros de ventas ni arriendos." else "No hay cotizaciones registradas.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listaFiltrada) { venta ->
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
                                        Text(
                                            text = "ID: ${venta.ventaId}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        AssistChip(onClick = {}, label = { Text(venta.tipoOperacion) })
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Vendedor: ${if (venta.vendedorNombre.isNotBlank()) venta.vendedorNombre else "Cliente Directo / Web"}", fontWeight = FontWeight.Bold)
                                    Text("Producto / Equipo: ${if (venta.productoNombre.isNotBlank()) venta.productoNombre else "Equipo de Refrigeración"}")
                                    Text("Cliente / Local: ${venta.clienteNombre}")
                                    Text("Cantidad: ${venta.cantidad} | Precio Unitario: $${String.format("%,.0f", venta.precioUnitario)}")
                                    Text("Monto Total: $${String.format("%,.0f", if (venta.montoTotal > 0) venta.montoTotal else venta.total)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Fecha: ${venta.fecha} | Estado: ${venta.estado}", style = MaterialTheme.typography.bodySmall)

                                    if (venta.descripcion.isNotBlank()) {
                                        Text("Descripción: ${venta.descripcion}", style = MaterialTheme.typography.bodySmall)
                                    }

                                    GaleriaFotos(fotosUrlsString = venta.fotosUrls)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}