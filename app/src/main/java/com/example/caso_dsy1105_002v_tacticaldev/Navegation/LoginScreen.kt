@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthUiState
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthViewModel

@Composable
fun EyeIconCanvas(
    visible: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(2.dp.toPx(), h / 2)
            cubicTo(
                w / 4, h / 4,
                3 * w / 4, h / 4,
                w - 2.dp.toPx(), h / 2
            )
            cubicTo(
                3 * w / 4, 3 * h / 4,
                w / 4, 3 * h / 4,
                2.dp.toPx(), h / 2
            )
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color = tint, radius = 3.5.dp.toPx(), center = Offset(w / 2, h / 2))

        if (!visible) {
            drawLine(
                color = tint,
                start = Offset(4.dp.toPx(), 4.dp.toPx()),
                end = Offset(w - 4.dp.toPx(), h - 4.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (Rol) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val usuario = (uiState as AuthUiState.Success).usuario
            onLoginSuccess(usuario.rol)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "TacticalDEV",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Sistema de Gestión de Refrigeración",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Contraseña"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            EyeIconCanvas(visible = passwordVisible)
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        authViewModel.iniciarSesion(correo, contrasena)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uiState !is AuthUiState.Loading,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Iniciar Sesión", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}