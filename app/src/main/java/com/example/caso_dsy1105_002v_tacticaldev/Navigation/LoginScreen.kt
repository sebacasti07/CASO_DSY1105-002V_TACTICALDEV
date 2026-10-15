package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthUiState
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf<String?>(null) }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    val dominiosValidos = listOf("@admindev.cl", "@tecdev.cl", "@vendedordev.cl", "@transportedev.cl", "@usuariodev.cl")

    fun validarFormulario(): Boolean {
        val correoLimpio = correo.trim().lowercase()

        if (correoLimpio.isBlank()) {
            errorLocal = "Por favor, ingresa tu correo electrónico."
            return false
        }

        if (dominiosValidos.none { correoLimpio.endsWith(it) }) {
            errorLocal = "Dominio de correo no permitido."
            return false
        }

        if (contrasena.length < 8) {
            errorLocal = "La contraseña debe tener al menos 8 caracteres."
            return false
        }

        errorLocal = null
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "TacticalDEV", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Mantención de Refrigeración",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                errorLocal = null
            },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                errorLocal = null
            },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    EyeIconCanvas(isVisible = passwordVisible, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorLocal?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }

        if (uiState is AuthUiState.Error && errorLocal == null) {
            Text(
                text = (uiState as AuthUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (validarFormulario()) {
                    authViewModel.iniciarSesion(correo.trim(), contrasena)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = uiState !is AuthUiState.Loading
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Iniciar Sesión")
            }
        }
    }
}

@Composable
private fun EyeIconCanvas(isVisible: Boolean, tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val eyePath = Path().apply {
            moveTo(0f, h * 0.5f)
            cubicTo(w * 0.25f, h * 0.15f, w * 0.75f, h * 0.15f, w, h * 0.5f)
            cubicTo(w * 0.75f, h * 0.85f, w * 0.25f, h * 0.85f, 0f, h * 0.5f)
            close()
        }
        drawPath(path = eyePath, color = tint, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color = tint, radius = w * 0.18f, center = center)
        if (!isVisible) {
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.85f),
                end = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.15f),
                strokeWidth = 2.5f.dp.toPx()
            )
        }
    }
}