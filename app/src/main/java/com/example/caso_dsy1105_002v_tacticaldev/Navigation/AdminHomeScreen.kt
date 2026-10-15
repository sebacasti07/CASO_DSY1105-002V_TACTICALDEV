@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    authViewModel: AuthViewModel,
    onNavigateTo: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = authViewModel.usuarioActual

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.cerrarSesion()
                        onCerrarSesion()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido, ${usuario?.primerNombre ?: "Administrador"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gestión centralizada del sistema TacticalDEV",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = "Módulos Principales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            AdminMenuOption(
                titulo = "Gestión de Usuarios",
                subtitulo = "Crear, editar y eliminar cuentas de usuarios",
                icono = Icons.Default.Person,
                onClick = { onNavigateTo(Screen.AdminUsuarios.route) }
            )

            AdminMenuOption(
                titulo = "Gestión de Inventario",
                subtitulo = "Control de repuestos, stock y precios",
                icono = Icons.Default.Build,
                onClick = { onNavigateTo(Screen.AdminInventario.route) }
            )

            AdminMenuOption(
                titulo = "Registro de Compras / Stock",
                subtitulo = "Ingreso de nuevos repuestos al almacén",
                icono = Icons.Default.ShoppingCart,
                onClick = { onNavigateTo(Screen.AdminCompraStock.route) }
            )

            AdminMenuOption(
                titulo = "Supervisión de Ventas",
                subtitulo = "Historial global de ventas realizadas",
                icono = Icons.Default.DateRange,
                onClick = { onNavigateTo(Screen.AdminVentas.route) }
            )

            AdminMenuOption(
                titulo = "Visitas Técnicas",
                subtitulo = "Control e historial de mantenimientos en terreno",
                icono = Icons.Default.LocationOn,
                onClick = { onNavigateTo(Screen.AdminVisitas.route) }
            )

            AdminMenuOption(
                titulo = "Monitoreo Remoto",
                subtitulo = "Salas remotas de soporte para técnicos",
                icono = Icons.Default.PlayArrow,
                onClick = { onNavigateTo(Screen.AdminVisitasRemotas.route) }
            )
        }
    }
}

@Composable
fun AdminMenuOption(
    titulo: String,
    subtitulo: String,
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
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}