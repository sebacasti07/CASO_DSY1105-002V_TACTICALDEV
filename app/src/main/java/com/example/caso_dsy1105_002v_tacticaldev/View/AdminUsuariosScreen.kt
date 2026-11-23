package com.example.caso_dsy1105_002v_tacticaldev.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Warning
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
import com.example.caso_dsy1105_002v_tacticaldev.Util.SecurityUtils
import com.example.caso_dsy1105_002v_tacticaldev.Util.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    listaUsuarios: List<Usuario>,
    listaMinimarkets: List<Minimarket> = emptyList(),
    onAgregarUsuario: (Usuario, Minimarket?, Minimarket?) -> Unit,
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
            onGuardar = { usuario, nuevoMinimarket, minimarketAEliminar ->
                onAgregarUsuario(usuario, nuevoMinimarket, minimarketAEliminar)
                mostrarDialogoCrear = false
            }
        )
    }

    usuarioAEditar?.let { usuario ->
        FormularioUsuarioDialog(
            usuarioExistente = usuario,
            listaMinimarkets = listaMinimarkets,
            onDismiss = { usuarioAEditar = null },
            onGuardar = { usuarioActualizado, nuevoMinimarket, minimarketAEliminar ->
                onAgregarUsuario(usuarioActualizado, nuevoMinimarket, minimarketAEliminar)
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
    onGuardar: (Usuario, Minimarket?, Minimarket?) -> Unit
) {
    val minimarketExistente = remember(usuarioExistente) {
        listaMinimarkets.find { it.rutCliente == usuarioExistente?.rut || it.rutComercial == usuarioExistente?.rut }
    }

    val esEdicion = usuarioExistente != null

    var rutCliente by remember { mutableStateOf(usuarioExistente?.rut ?: "") }
    var primerNombre by remember { mutableStateOf(usuarioExistente?.primerNombre ?: "") }
    var segundoNombre by remember { mutableStateOf(usuarioExistente?.segundoNombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioExistente?.apellido ?: "") }
    var correo by remember { mutableStateOf(usuarioExistente?.correo ?: "") }
    var telefono by remember { mutableStateOf(usuarioExistente?.telefono ?: "") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacionContrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuarioExistente?.rol ?: Rol.CLIENTE) }

    var rutComercialMinimarket by remember { mutableStateOf(minimarketExistente?.rutComercial ?: "") }
    var nombreMinimarket by remember { mutableStateOf(minimarketExistente?.nombre ?: "") }
    var direccionMinimarket by remember { mutableStateOf(minimarketExistente?.direccion ?: "") }

    var errorRut by remember { mutableStateOf<String?>(null) }
    var errorPrimerNombre by remember { mutableStateOf<String?>(null) }
    var errorApellido by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }
    var errorConfirmacion by remember { mutableStateOf<String?>(null) }

    var errorRutMinimarket by remember { mutableStateOf<String?>(null) }
    var errorNombreMinimarket by remember { mutableStateOf<String?>(null) }
    var errorDireccionMinimarket by remember { mutableStateOf<String?>(null) }

    fun validarFormulario(): Boolean {
        errorRut = ValidationUtils.validarRut(rutCliente)
        errorPrimerNombre = ValidationUtils.validarSoloLetras(primerNombre, "Primer Nombre")
        errorApellido = ValidationUtils.validarSoloLetras(apellido, "Apellido")
        errorTelefono = ValidationUtils.validarTelefono(telefono)
        errorCorreo = ValidationUtils.validarCorreoPorRol(correo, rolSeleccionado)

        errorContrasena = ValidationUtils.validarContrasena(contrasena, esEdicion = esEdicion)
        errorConfirmacion = if (!esEdicion || contrasena.trim().isNotEmpty()) {
            ValidationUtils.validarConfirmacionContrasena(contrasena, confirmacionContrasena)
        } else null

        if (rolSeleccionado == Rol.CLIENTE) {
            errorRutMinimarket = ValidationUtils.validarRut(rutComercialMinimarket)
            errorNombreMinimarket = if (nombreMinimarket.isBlank()) "Ingresa el nombre del Minimarket" else null
            errorDireccionMinimarket = if (direccionMinimarket.isBlank()) "Ingresa la dirección del Minimarket" else null
        } else {
            errorRutMinimarket = null
            errorNombreMinimarket = null
            errorDireccionMinimarket = null
        }

        return errorRut == null && errorPrimerNombre == null && errorApellido == null &&
                errorTelefono == null && errorCorreo == null && errorContrasena == null &&
                errorConfirmacion == null && errorRutMinimarket == null &&
                errorNombreMinimarket == null && errorDireccionMinimarket == null
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
                    onValueChange = { rutCliente = it; errorRut = null },
                    label = { Text("RUT Cliente (12345678-K)") },
                    isError = errorRut != null,
                    supportingText = { errorRut?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorRut != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = primerNombre,
                    onValueChange = { primerNombre = it; errorPrimerNombre = null },
                    label = { Text("Primer Nombre") },
                    isError = errorPrimerNombre != null,
                    supportingText = { errorPrimerNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorPrimerNombre != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = segundoNombre,
                    onValueChange = { segundoNombre = it },
                    label = { Text("Segundo Nombre (Opcional)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it; errorApellido = null },
                    label = { Text("Apellido") },
                    isError = errorApellido != null,
                    supportingText = { errorApellido?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorApellido != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; errorCorreo = null },
                    label = { Text("Correo Electrónico") },
                    isError = errorCorreo != null,
                    supportingText = { errorCorreo?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorCorreo != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            telefono = input
                            errorTelefono = null
                        }
                    },
                    label = { Text("Teléfono (Mínimo 8 dígitos)") },
                    isError = errorTelefono != null,
                    supportingText = { errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = { if (errorTelefono != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it; errorContrasena = null },
                    label = { Text(if (esEdicion) "Nueva Contraseña (opcional al editar)" else "Contraseña (Mín 8 caracteres)") },
                    isError = errorContrasena != null,
                    supportingText = { errorContrasena?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (errorContrasena != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                Text(if (mostrarContrasena) "Ocultar" else "Mostrar", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                )

                OutlinedTextField(
                    value = confirmacionContrasena,
                    onValueChange = { confirmacionContrasena = it; errorConfirmacion = null },
                    label = { Text("Confirmar Contraseña") },
                    isError = errorConfirmacion != null,
                    supportingText = { errorConfirmacion?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { if (errorConfirmacion != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                )

                Text("Rol:", fontWeight = FontWeight.Bold)
                Rol.entries.forEach { rol ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = (rolSeleccionado == rol), onClick = { rolSeleccionado = rol })
                        Text(text = rol.name)
                    }
                }

                AnimatedVisibility(
                    visible = rolSeleccionado == Rol.CLIENTE,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Datos del Minimarket:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = rutComercialMinimarket,
                            onValueChange = { rutComercialMinimarket = it; errorRutMinimarket = null },
                            label = { Text("RUT Comercial Minimarket") },
                            isError = errorRutMinimarket != null,
                            supportingText = { errorRutMinimarket?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            trailingIcon = { if (errorRutMinimarket != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = nombreMinimarket,
                            onValueChange = { nombreMinimarket = it; errorNombreMinimarket = null },
                            label = { Text("Nombre del Minimarket") },
                            isError = errorNombreMinimarket != null,
                            supportingText = { errorNombreMinimarket?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            trailingIcon = { if (errorNombreMinimarket != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = direccionMinimarket,
                            onValueChange = { direccionMinimarket = it; errorDireccionMinimarket = null },
                            label = { Text("Dirección del Minimarket") },
                            isError = errorDireccionMinimarket != null,
                            supportingText = { errorDireccionMinimarket?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            trailingIcon = { if (errorDireccionMinimarket != null) Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (validarFormulario()) {
                    val passFinal = if (esEdicion && contrasena.trim().isEmpty()) {
                        usuarioExistente!!.contrasena
                    } else {
                        SecurityUtils.hashPassword(contrasena.trim())
                    }

                    val usuarioGuardado = Usuario(
                        usuarioId = usuarioExistente?.usuarioId ?: ("USR-" + System.currentTimeMillis()),
                        rut = rutCliente.trim(),
                        primerNombre = primerNombre.trim(),
                        segundoNombre = segundoNombre.trim().ifBlank { null },
                        apellido = apellido.trim(),
                        correo = correo.trim().lowercase(),
                        telefono = telefono.trim(),
                        contrasena = passFinal,
                        rol = rolSeleccionado
                    )

                    val nuevoMinimarket = if (rolSeleccionado == Rol.CLIENTE) {
                        Minimarket(
                            minimarketId = minimarketExistente?.minimarketId ?: ("MIN-" + System.currentTimeMillis()),
                            rutCliente = rutCliente.trim(),
                            rutComercial = rutComercialMinimarket.trim(),
                            nombre = nombreMinimarket.trim(),
                            direccion = direccionMinimarket.trim(),
                            telefono = telefono.trim()
                        )
                    } else null

                    val minimarketAEliminar = if (rolSeleccionado != Rol.CLIENTE && minimarketExistente != null) {
                        minimarketExistente
                    } else null

                    onGuardar(usuarioGuardado, nuevoMinimarket, minimarketAEliminar)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
