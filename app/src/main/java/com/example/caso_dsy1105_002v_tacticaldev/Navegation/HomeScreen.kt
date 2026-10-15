package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthUiState
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    // Obtener el usuario desde el estado Success o desde la propiedad usuarioActual
    val usuario = (uiState as? AuthUiState.Success)?.usuario ?: authViewModel.usuarioActual

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (usuario != null) {
                Text(
                    text = "Bienvenido, ${usuario.primerNombre} ${usuario.apellido}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "RUT: ${usuario.rut}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Rol: ${usuario.rol.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Cargando datos de usuario...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.cerrarSesion()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}