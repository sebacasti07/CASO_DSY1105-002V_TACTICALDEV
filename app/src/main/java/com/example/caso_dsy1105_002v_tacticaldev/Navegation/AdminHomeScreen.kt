package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onNavigateToUsuarios: () -> Unit,
    onNavigateToInventario: () -> Unit,
    onNavigateToVentas: () -> Unit,
    onNavigateToCompraStock: () -> Unit,
    onNavigateToVisitas: () -> Unit,
    onNavigateToVisitaRemota: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración Central") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
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
            Text(
                text = "Módulos de Gestión TacticalDEV",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // 1. Gestión de Usuarios
            AdminMenuCard(
                titulo = "Gestión de Usuarios",
                descripcion = "Crear, editar, filtrar y eliminar cuentas (Técnicos, Clientes, Vendedores, Transportistas).",
                icono = Icons.Default.Person,
                onClick = onNavigateToUsuarios
            )

            // 2. Gestión de Inventario
            AdminMenuCard(
                titulo = "Gestión de Inventario",
                descripcion = "Administrar repuestos, ubicaciones en bodegas y cantidades disponibles.",
                icono = Icons.Default.List,
                onClick = onNavigateToInventario
            )

            // 3. Gestión de Ventas
            AdminMenuCard(
                titulo = "Gestión de Ventas",
                descripcion = "Registrar ventas de repuestos, montos, clientes y consultar historial.",
                icono = Icons.Default.ShoppingCart,
                onClick = onNavigateToVentas
            )

            // 4. Gestión de Compra Stock
            AdminMenuCard(
                titulo = "Gestión de Compra Stock",
                descripcion = "Órdenes de compra a proveedores, recepción de repuestos y reabastecimiento.",
                icono = Icons.Default.AddCircle,
                onClick = onNavigateToCompraStock
            )

            // 5. Gestión de Visitas Terreno
            AdminMenuCard(
                titulo = "Gestión de Visitas Terreno",
                descripcion = "Supervisión de visitas técnicas, observaciones y fotografías registradas.",
                icono = Icons.Default.LocationOn,
                onClick = onNavigateToVisitas
            )

            // 6. Gestión de Visita Remota
            AdminMenuCard(
                titulo = "Gestión de Visita Remota",
                descripcion = "Salas virtuales de asistencia técnica a distancia y diagnósticos remotos.",
                icono = Icons.Default.Phone,
                onClick = onNavigateToVisitaRemota
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun AdminMenuCard(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}