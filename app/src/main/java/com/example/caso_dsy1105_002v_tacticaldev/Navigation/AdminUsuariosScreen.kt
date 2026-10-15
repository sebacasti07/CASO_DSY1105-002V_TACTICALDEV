@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    listaUsuarios: List<Usuario>,
    listaMinimarkets: List<Minimarket> = emptyList(),
    onAgregarUsuario: (Usuario, Minimarket?) -> Unit,
    onEliminarUsuario: (Usuario) -> Unit,
    onVolver: () -> Unit
) {
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var usuarioAEditar by remember { mutableStateOf<Usuario?>(null) }
    var usuarioAEliminarConfirm by remember { mutableStateOf<Usuario?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Usuario")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (listaUsuarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios registrados.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaUsuarios) { usuario ->
                        val esAdminPrincipal = usuario.usuarioId == "USR-ADMIN-001"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${usuario.primerNombre} ${usuario.apellido}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("RUT Cliente: ${usuario.rut}", style = MaterialTheme.typography.bodySmall)
                                    Text("Correo: ${usuario.correo}", style = MaterialTheme.typography.bodySmall)
                                    Text("Teléfono: ${usuario.telefono}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = "Rol: ${usuario.rol}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (!esAdminPrincipal) {
                                    Row {
                                        IconButton(onClick = { usuarioAEditar = usuario }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { usuarioAEliminarConfirm = usuario }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                } else {
                                    AssistChip(onClick = {}, label = { Text("Admin Principal") })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoCrear) {
        FormularioUsuarioDialog(
            usuarioExistente = null,
            listaMinimarkets = listaMinimarkets,
            onDismiss = { mostrarDialogoCrear = false },
            onGuardar = { usuario, minimarket ->
                onAgregarUsuario(usuario, minimarket)
                mostrarDialogoCrear = false
            }
        )
    }

    usuarioAEditar?.let { usuario ->
        FormularioUsuarioDialog(
            usuarioExistente = usuario,
            listaMinimarkets = listaMinimarkets,
            onDismiss = { usuarioAEditar = null },
            onGuardar = { usuarioActualizado, minimarket ->
                onAgregarUsuario(usuarioActualizado, minimarket)
                usuarioAEditar = null
            }
        )
    }

    usuarioAEliminarConfirm?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioAEliminarConfirm = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar al usuario '${usuario.primerNombre} ${usuario.apellido}'?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        onEliminarUsuario(usuario)
                        usuarioAEliminarConfirm = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminarConfirm = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FormularioUsuarioDialog(
    usuarioExistente: Usuario?,
    listaMinimarkets: List<Minimarket>,
    onDismiss: () -> Unit,
    onGuardar: (Usuario, Minimarket?) -> Unit
) {
    val minimarketExistente = remember(usuarioExistente) {
        listaMinimarkets.find { it.rutCliente == usuarioExistente?.rut || it.rutComercial == usuarioExistente?.rut }
    }

    var rutCliente by remember { mutableStateOf(usuarioExistente?.rut ?: "") }
    var primerNombre by remember { mutableStateOf(usuarioExistente?.primerNombre ?: "") }
    var segundoNombre by remember { mutableStateOf(usuarioExistente?.segundoNombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioExistente?.apellido ?: "") }
    var correo by remember { mutableStateOf(usuarioExistente?.correo ?: "") }
    var telefono by remember { mutableStateOf(usuarioExistente?.telefono ?: "") }
    var contrasena by remember { mutableStateOf(usuarioExistente?.contrasena ?: "") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuarioExistente?.rol ?: Rol.CLIENTE) }

    var rutComercialMinimarket by remember { mutableStateOf(minimarketExistente?.rutComercial ?: "") }
    var nombreMinimarket by remember { mutableStateOf(minimarketExistente?.nombre ?: "") }
    var direccionMinimarket by remember { mutableStateOf(minimarketExistente?.direccion ?: "") }

    var errorMensaje by remember { mutableStateOf<String?>(null) }

    fun esContrasenaValida(pass: String): Boolean {
        if (pass.length < 6) return false
        val tieneLetra = pass.any { it.isLetter() }
        val tieneNumero = pass.any { it.isDigit() }
        return tieneLetra && tieneNumero
    }

    fun validarFormulario(): Boolean {
        val rutRegex = Regex("^[0-9]+-[0-9kK]$")
        val soloLetrasRegex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")

        if (!rutRegex.matches(rutCliente.trim())) {
            errorMensaje = "RUT Cliente inválido (ej: 12345678-K)."
            return false
        }

        if (!soloLetrasRegex.matches(primerNombre.trim()) || !soloLetrasRegex.matches(apellido.trim())) {
            errorMensaje = "Nombre y Apellido solo deben contener letras."
            return false
        }

        if (telefono.isBlank() || !telefono.all { it.isDigit() }) {
            errorMensaje = "El teléfono debe contener únicamente números."
            return false
        }

        if (!esContrasenaValida(contrasena.trim())) {
            errorMensaje = "La contraseña debe tener al menos 6 caracteres con combinación de letras y números."
            return false
        }

        val correoLimpio = correo.trim().lowercase()
        val dominioCorrecto = when (rolSeleccionado) {
            Rol.ADMINISTRADOR -> correoLimpio.endsWith("@admindev.cl")
            Rol.TECNICO -> correoLimpio.endsWith("@tecdev.cl")
            Rol.VENDEDOR -> correoLimpio.endsWith("@vendedordev.cl")
            Rol.TRANSPORTISTA -> correoLimpio.endsWith("@transportedev.cl")
            Rol.CLIENTE -> correoLimpio.endsWith("@usuariodev.cl")
        }

        if (!dominioCorrecto) {
            errorMensaje = "El dominio de correo no corresponde al rol elegido."
            return false
        }

        if (rolSeleccionado == Rol.CLIENTE) {
            if (!rutRegex.matches(rutComercialMinimarket.trim())) {
                errorMensaje = "RUT Comercial del Minimarket inválido."
                return false
            }
            if (nombreMinimarket.isBlank() || direccionMinimarket.isBlank()) {
                errorMensaje = "Ingresa Nombre y Dirección del Minimarket."
                return false
            }
        }

        errorMensaje = null
        return true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (usuarioExistente == null) "Crear Usuario" else "Editar Usuario") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rutCliente,
                    onValueChange = { rutCliente = it },
                    label = { Text("RUT Cliente (12345678-K)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = primerNombre,
                    onValueChange = { primerNombre = it },
                    label = { Text("Primer Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = segundoNombre,
                    onValueChange = { segundoNombre = it },
                    label = { Text("Segundo Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) telefono = input
                    },
                    label = { Text("Teléfono (Solo números)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña (Min 6, letras y números)") },
                    singleLine = true,
                    visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                            Text(
                                text = if (mostrarContrasena) "Ocultar" else "Mostrar",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                )

                Text("Rol:", fontWeight = FontWeight.Bold)
                Rol.entries.forEach { rol ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = (rolSeleccionado == rol), onClick = { rolSeleccionado = rol })
                        Text(text = rol.name)
                    }
                }

                if (rolSeleccionado == Rol.CLIENTE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Datos del Minimarket:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = rutComercialMinimarket,
                        onValueChange = { rutComercialMinimarket = it },
                        label = { Text("RUT Comercial Minimarket") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nombreMinimarket,
                        onValueChange = { nombreMinimarket = it },
                        label = { Text("Nombre del Minimarket") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = direccionMinimarket,
                        onValueChange = { direccionMinimarket = it },
                        label = { Text("Dirección del Minimarket") },
                        singleLine = true
                    )
                }

                errorMensaje?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (validarFormulario()) {
                    val usuarioGuardado = Usuario(
                        usuarioId = usuarioExistente?.usuarioId ?: ("USR-" + System.currentTimeMillis()),
                        rut = rutCliente.trim(),
                        primerNombre = primerNombre.trim(),
                        segundoNombre = segundoNombre.trim().ifBlank { null },
                        apellido = apellido.trim(),
                        correo = correo.trim().lowercase(),
                        telefono = telefono.trim(),
                        contrasena = contrasena.trim(),
                        rol = rolSeleccionado
                    )

                    val minimarketGuardado = if (rolSeleccionado == Rol.CLIENTE) {
                        Minimarket(
                            minimarketId = minimarketExistente?.minimarketId ?: ("MIN-" + System.currentTimeMillis()),
                            rutCliente = rutCliente.trim(),
                            rutComercial = rutComercialMinimarket.trim(),
                            nombre = nombreMinimarket.trim(),
                            direccion = direccionMinimarket.trim(),
                            telefono = telefono.trim()
                        )
                    } else null

                    onGuardar(usuarioGuardado, minimarketGuardado)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}