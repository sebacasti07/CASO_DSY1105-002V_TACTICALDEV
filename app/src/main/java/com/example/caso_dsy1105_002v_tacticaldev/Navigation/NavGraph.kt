@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.caso_dsy1105_002v_tacticaldev.Database.AppDatabase
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import com.example.caso_dsy1105_002v_tacticaldev.View.*
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val db = AppDatabase.getDatabase(context)
    val usuarioDao = db.usuarioDao()
    val repuestoDao = db.repuestoDao()
    val visitaDao = db.visitaDao()
    val ventaDao = db.ventaDao()
    val minimarketDao = db.minimarketDao()
    val asistenciaRemotaDao = db.asistenciaRemotaDao()
    val ordenCompraDao = db.ordenCompraDao()
    val equipoDao = db.equipoDao()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val usuario = (uiState as AuthUiState.Success).usuario
            val rutaDestino = when (usuario.rol) {
                Rol.ADMINISTRADOR -> Screen.AdminHome.route
                Rol.TECNICO -> Screen.TecnicoHome.route
                Rol.CLIENTE -> Screen.ClienteHome.route
                Rol.VENDEDOR -> Screen.VendedorHome.route
                Rol.TRANSPORTISTA -> Screen.TransportistaHome.route
            }
            navController.navigate(rutaDestino) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else if (uiState is AuthUiState.Idle) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { }
            )
        }

        // --- MÓDULOS DE ADMINISTRADOR ---
        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                onNavigateToUsuarios = { navController.navigate(Screen.AdminUsuarios.route) },
                onNavigateToInventario = { navController.navigate(Screen.AdminInventario.route) },
                onNavigateToVentas = { navController.navigate(Screen.AdminVentas.route) },
                onNavigateToCompraStock = { navController.navigate(Screen.AdminCompraStock.route) },
                onNavigateToVisitas = { navController.navigate(Screen.AdminVisitas.route) },
                onNavigateToVisitaRemota = { navController.navigate(Screen.AdminVisitaRemota.route) },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.AdminUsuarios.route) {
            val listaUsuarios: List<Usuario> by usuarioDao.obtenerTodosLosUsuarios().collectAsState(initial = emptyList())
            val listaMinimarkets: List<Minimarket> by minimarketDao.obtenerTodosLosMinimarkets().collectAsState(initial = emptyList())

            AdminUsuariosScreen(
                listaUsuarios = listaUsuarios,
                listaMinimarkets = listaMinimarkets,
                onAgregarUsuario = { nuevoUsuario, nuevoMinimarket ->
                    coroutineScope.launch {
                        usuarioDao.insertarUsuario(nuevoUsuario)
                        nuevoMinimarket?.let { minimarketDao.insertarMinimarket(it) }
                    }
                },
                onEliminarUsuario = { usuarioAEliminar ->
                    coroutineScope.launch { usuarioDao.eliminarUsuario(usuarioAEliminar) }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminInventario.route) {
            val listaInventario: List<InventarioRepuesto> by repuestoDao.obtenerTodosLosInventarios().collectAsState(initial = emptyList())

            AdminInventarioScreen(
                listaInventario = listaInventario,
                onAgregarStock = { nuevoStock: InventarioRepuesto ->
                    coroutineScope.launch { repuestoDao.insertarInventario(nuevoStock) }
                },
                onEliminarStock = { stockAEliminar: InventarioRepuesto ->
                    coroutineScope.launch { repuestoDao.eliminarInventario(stockAEliminar) }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminVentas.route) {
            val listaVentas by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            AdminVentasScreen(
                listaVentas = listaVentas,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminCompraStock.route) {
            val listaOrdenes by ordenCompraDao.obtenerTodasLasOrdenes().collectAsState(initial = emptyList())
            AdminCompraStockScreen(
                listaOrdenes = listaOrdenes,
                onRegistrarCompra = { nuevaOrden ->
                    coroutineScope.launch {
                        ordenCompraDao.insertarOrdenCompra(nuevaOrden)
                        val invExistente = repuestoDao.obtenerPorId(nuevaOrden.repuestoId)
                        if (invExistente != null) {
                            val stockActual = if (invExistente.stockDisponible > 0) invExistente.stockDisponible else invExistente.cantidadDisponible
                            val nuevoStockCalc = stockActual + nuevaOrden.cantidad
                            repuestoDao.actualizarInventario(
                                invExistente.copy(
                                    stockDisponible = nuevoStockCalc,
                                    cantidadDisponible = nuevoStockCalc
                                )
                            )
                        } else {
                            repuestoDao.insertarInventario(
                                InventarioRepuesto(
                                    inventarioId = "INV-" + System.currentTimeMillis(),
                                    repuestoId = nuevaOrden.repuestoId.ifBlank { "REP-" + System.currentTimeMillis() },
                                    nombre = nuevaOrden.nombreRepuesto,
                                    nombreRepuesto = nuevaOrden.nombreRepuesto,
                                    tipo = "General",
                                    tipoItem = "Repuesto",
                                    tipoEquipo = "",
                                    categoria = "General",
                                    cantidadDisponible = nuevaOrden.cantidad,
                                    stockDisponible = nuevaOrden.cantidad,
                                    precioUnitario = nuevaOrden.precioCosto * 1.3,
                                    precioArriendo = (nuevaOrden.precioCosto * 1.3) * 0.1,
                                    bodegaUbicacion = "Bodega Principal"
                                )
                            )
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminVisitas.route) {
            val listaVisitas by visitaDao.obtenerTodasLasVisitas().collectAsState(initial = emptyList())
            AdminVisitasScreen(
                listaVisitas = listaVisitas,
                onActualizarVisita = { visitaActualizada ->
                    coroutineScope.launch { visitaDao.actualizarVisita(visitaActualizada) }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminVisitaRemota.route) {
            val listaRemotas by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())
            AdminVisitaRemotaScreen(
                listaRemotas = listaRemotas,
                onActualizarRemota = { remotaActualizada ->
                    coroutineScope.launch { asistenciaRemotaDao.actualizarAsistencia(remotaActualizada) }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // --- MÓDULO DEL CLIENTE ---
        composable(Screen.ClienteHome.route) {
            ClienteHomeScreen(
                onNavigateToSolicitarCotizacion = { navController.navigate(Screen.ClienteSolicitarCotizacion.route) },
                onNavigateToSolicitarVisita = { navController.navigate(Screen.ClienteSolicitarVisita.route) },
                onNavigateToSolicitarVisitaRemota = { navController.navigate(Screen.ClienteSolicitarVisitaRemota.route) },
                onNavigateToMisSolicitudes = { navController.navigate(Screen.ClienteMisSolicitudes.route) },
                onNavigateToMisEquipos = { navController.navigate(Screen.ClienteMisEquipos.route) },
                onNavigateToCatalogoEquipos = { navController.navigate(Screen.ClienteCatalogoEquipos.route) },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.ClienteMisEquipos.route) {
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val minimarketId = minimarketCliente?.minimarketId ?: ""

            val equiposCliente by equipoDao.obtenerPorMinimarket(minimarketId).collectAsState(initial = emptyList())

            ClienteMisEquiposScreen(
                listaEquipos = equiposCliente,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteCatalogoEquipos.route) {
            val listaInventario by repuestoDao.obtenerTodosLosInventarios().collectAsState(initial = emptyList())

            ClienteCatalogoEquiposScreen(
                listaInventario = listaInventario,
                onSolicitarCotizacionClick = { navController.navigate(Screen.ClienteSolicitarCotizacion.route) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteSolicitarCotizacion.route) {
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val cotizacionesExistentes: List<Venta> by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())

            ClienteSolicitarCotizacionScreen(
                minimarketCliente = minimarketCliente,
                cotizacionesExistentes = cotizacionesExistentes,
                onSolicitarCotizacion = { nuevaCotizacion ->
                    coroutineScope.launch {
                        ventaDao.insertarVenta(nuevaCotizacion)
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteSolicitarVisita.route) {
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val visitasExistentes: List<Visita> by visitaDao.obtenerTodasLasVisitas().collectAsState(initial = emptyList())

            ClienteSolicitarVisitaScreen(
                minimarketCliente = minimarketCliente,
                visitasExistentes = visitasExistentes,
                onSolicitarVisita = { nuevaVisita ->
                    coroutineScope.launch {
                        visitaDao.insertarVisita(nuevaVisita)
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteSolicitarVisitaRemota.route) {
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val asistenciasExistentes: List<AsistenciaRemota> by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            ClienteSolicitarVisitaRemotaScreen(
                minimarketCliente = minimarketCliente,
                asistenciasExistentes = asistenciasExistentes,
                onSolicitarRemota = { nuevaAsistencia ->
                    coroutineScope.launch {
                        asistenciaRemotaDao.insertarAsistencia(nuevaAsistencia)
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteMisSolicitudes.route) {
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val idMinimarket = minimarketCliente?.minimarketId ?: ""

            val todasVentas: List<Venta> by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            val todasVisitas: List<Visita> by visitaDao.obtenerTodasLasVisitas().collectAsState(initial = emptyList())
            val todasRemotas: List<AsistenciaRemota> by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            val cotizacionesCliente = todasVentas.filter { it.minimarketId == idMinimarket }
            val visitasCliente = todasVisitas.filter { it.minimarketId == idMinimarket }
            val remotasCliente = todasRemotas.filter { it.minimarketId == idMinimarket }

            ClienteMisSolicitudesScreen(
                listaCotizaciones = cotizacionesCliente,
                listaVisitas = visitasCliente,
                listaRemotas = remotasCliente,
                onCancelarCotizacion = { cotizacion -> coroutineScope.launch { ventaDao.actualizarVenta(cotizacion) } },
                onCancelarVisita = { visita -> coroutineScope.launch { visitaDao.actualizarVisita(visita) } },
                onCancelarRemota = { asistencia -> coroutineScope.launch { asistenciaRemotaDao.actualizarAsistencia(asistencia) } },
                onReagendarCotizacion = { cotizacion -> coroutineScope.launch { ventaDao.actualizarVenta(cotizacion) } },
                onReagendarVisita = { visita -> coroutineScope.launch { visitaDao.actualizarVisita(visita) } },
                onReagendarRemota = { asistencia -> coroutineScope.launch { asistenciaRemotaDao.actualizarAsistencia(asistencia) } },
                onVolver = { navController.popBackStack() }
            )
        }

        // --- MÓDULOS DEL VENDEDOR ---
        composable(Screen.VendedorHome.route) {
            VendedorHomeScreen(
                onNavigateToCotizaciones = { navController.navigate(Screen.VendedorCotizaciones.route) },
                onNavigateToRegistrarVenta = { navController.navigate(Screen.VendedorRegistrarVenta.route) },
                onNavigateToInventario = { navController.navigate(Screen.VendedorInventario.route) },
                onNavigateToMisVentas = { navController.navigate(Screen.VendedorMisVentas.route) },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.VendedorCotizaciones.route) {
            val vendedorActual = authViewModel.usuarioActual
            val todasLasVentas by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            val cotizaciones = todasLasVentas.filter { it.tipoOperacion == "Cotización" || it.estado.contains("Cotización", ignoreCase = true) }

            VendedorCotizacionesScreen(
                vendedorActual = vendedorActual,
                listaCotizaciones = cotizaciones,
                onResponderCotizacion = { cotizacionActualizada ->
                    coroutineScope.launch { ventaDao.actualizarVenta(cotizacionActualizada) }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.VendedorRegistrarVenta.route) {
            val vendedorActual = authViewModel.usuarioActual
            val listaMinimarkets by minimarketDao.obtenerTodosLosMinimarkets().collectAsState(initial = emptyList())
            val listaInventario by repuestoDao.obtenerTodosLosInventarios().collectAsState(initial = emptyList())

            VendedorRegistrarVentaScreen(
                vendedorActual = vendedorActual,
                listaMinimarkets = listaMinimarkets,
                listaInventario = listaInventario,
                onRegistrarOperacion = { nuevaVenta, inventarioActualizado ->
                    coroutineScope.launch {
                        ventaDao.insertarVenta(nuevaVenta)
                        inventarioActualizado?.let { repuestoDao.actualizarInventario(it) }

                        // Generar N registros individuales de equipos para el cliente transfiriendo foto y descripción
                        repeat(nuevaVenta.cantidad) { index ->
                            val nuevoEquipo = EquipoRefrigeracion(
                                equipoId = "EQ-" + System.currentTimeMillis() + "-${index + 1}",
                                minimarketId = nuevaVenta.minimarketId,
                                nombre = if (nuevaVenta.cantidad > 1) "${nuevaVenta.productoNombre} (Unidad ${index + 1}/${nuevaVenta.cantidad})" else nuevaVenta.productoNombre,
                                tipo = nuevaVenta.tipoOperacion,
                                modelo = "Serie-" + (1000..9999).random(),
                                modalidad = if (nuevaVenta.tipoOperacion == "Arriendo") "Arriendo" else "Propiedad",
                                estadoOperativo = "Operativo",
                                fechaAdquisicion = nuevaVenta.fecha,
                                descripcion = nuevaVenta.descripcion,
                                fotosUrls = nuevaVenta.fotosUrls
                            )
                            equipoDao.insertarEquipo(nuevoEquipo)
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.VendedorInventario.route) {
            val listaInventario by repuestoDao.obtenerTodosLosInventarios().collectAsState(initial = emptyList())

            VendedorInventarioScreen(
                listaInventario = listaInventario,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.VendedorMisVentas.route) {
            val vendedorActual = authViewModel.usuarioActual
            val todasLasVentas by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            val misVentas = todasLasVentas.filter { it.vendedorId == vendedorActual?.usuarioId }

            VendedorMisVentasScreen(
                listaMisVentas = misVentas,
                onDarDeBajaArriendo = { arriendo ->
                    coroutineScope.launch {
                        // 1. Actualizar estado del contrato a "Arriendo Finalizado"
                        val ventaActualizada = arriendo.copy(estado = "Arriendo Finalizado")
                        ventaDao.actualizarVenta(ventaActualizada)

                        // 2. Reponer stock en el inventario
                        val invExistente = repuestoDao.obtenerPorId(arriendo.repuestoId)
                        if (invExistente != null) {
                            val stockActual = if (invExistente.stockDisponible > 0) invExistente.stockDisponible else invExistente.cantidadDisponible
                            val nuevoStock = stockActual + arriendo.cantidad
                            repuestoDao.actualizarInventario(
                                invExistente.copy(
                                    stockDisponible = nuevoStock,
                                    cantidadDisponible = nuevoStock
                                )
                            )
                        }

                        // 3. Marcar los equipos del cliente como "Devuelto"
                        try {
                            val listaEquipos = equipoDao.obtenerPorMinimarket(arriendo.minimarketId).first()
                            val equiposAModificar = listaEquipos.filter {
                                it.modalidad == "Arriendo" &&
                                        (it.nombre.contains(arriendo.productoNombre, ignoreCase = true) || arriendo.productoNombre.contains(it.nombre, ignoreCase = true)) &&
                                        !it.estadoOperativo.contains("Devuelto", ignoreCase = true)
                            }.take(arriendo.cantidad)

                            equiposAModificar.forEach { equipo ->
                                equipoDao.actualizarEquipo(equipo.copy(estadoOperativo = "Devuelto / Arriendo Finalizado"))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // --- MÓDULOS DEL TÉCNICO ---
        composable(Screen.TecnicoHome.route) {
            TecnicoHomeScreen(
                onNavigateToVisitas = { navController.navigate(Screen.TecnicoVisitas.route) },
                onNavigateToSalaRemota = { navController.navigate(Screen.TecnicoSalaRemota.route) },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.TecnicoVisitas.route) {
            val listaVisitas by visitaDao.obtenerTodasLasVisitas().collectAsState(initial = emptyList())
            val listaRemotas by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            TecnicoVisitasScreen(
                listaVisitas = listaVisitas,
                listaRemotas = listaRemotas,
                onActualizarVisita = { visita -> coroutineScope.launch { visitaDao.actualizarVisita(visita) } },
                onActualizarRemota = { asistencia -> coroutineScope.launch { asistenciaRemotaDao.actualizarAsistencia(asistencia) } },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.TecnicoSalaRemota.route) {
            val listaRemotas by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            TecnicoSalaRemotaScreen(
                listaRemotas = listaRemotas,
                onFinalizarReunion = { asistenciaAtendida ->
                    coroutineScope.launch { asistenciaRemotaDao.actualizarAsistencia(asistenciaAtendida) }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // --- DEMÁS ROLES ---
        composable(Screen.TransportistaHome.route) { TransportistaHomeScreen(onLogout = { authViewModel.cerrarSesion() }) }
    }
}