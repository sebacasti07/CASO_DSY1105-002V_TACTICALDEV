package com.example.caso_dsy1105_002v_tacticaldev.Navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.caso_dsy1105_002v_tacticaldev.Database.AppDatabase
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import com.example.caso_dsy1105_002v_tacticaldev.Network.RetrofitClient
import com.example.caso_dsy1105_002v_tacticaldev.Repository.*
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

    val usuarioRepository = UsuarioRepository(usuarioDao)
    val repuestoRepository = RepuestoRepository(repuestoDao)
    val visitaRepository = VisitaRepository(visitaDao)
    val equipoRepository = EquipoRepository(equipoDao)

    val viewModelFactory = ViewModelFactory(
        usuarioDao = usuarioDao,
        usuarioRepository = usuarioRepository,
        repuestoRepository = repuestoRepository,
        equipoRepository = equipoRepository,
        visitaRepository = visitaRepository
    )

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                usuarioRepository.sincronizarConApi()
                repuestoRepository.sincronizarConApi()
                visitaRepository.sincronizarConApi()
                equipoRepository.sincronizarConApi()

                val ventasRes = RetrofitClient.springBootService.obtenerVentas()
                if (ventasRes.isSuccessful) {
                    ventasRes.body()?.forEach { ventaDao.insertarVenta(it) }
                }

                val asistRes = RetrofitClient.springBootService.obtenerAsistencias()
                if (asistRes.isSuccessful) {
                    asistRes.body()?.forEach { asistenciaRemotaDao.insertarAsistencia(it) }
                }
                val miniRes = RetrofitClient.springBootService.obtenerMinimarkets()
                if (miniRes.isSuccessful) {
                    miniRes.body()?.forEach { minimarketDao.insertarMinimarket(it) }
                }

                val eqRes = RetrofitClient.springBootService.obtenerEquipos()
                if (eqRes.isSuccessful) {
                    eqRes.body()?.forEach { equipoDao.insertarEquipo(it) }
                }

                val ordRes = RetrofitClient.springBootService.obtenerOrdenesCompra()
                if (ordRes.isSuccessful) {
                    ordRes.body()?.forEach { ordenCompraDao.insertarOrdenCompra(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(uiState) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (uiState is AuthUiState.Success && currentRoute == Screen.Login.route) {
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
                launchSingleTop = true
            }
        } else if (uiState is AuthUiState.Idle && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = { fadeIn(tween(250)) },
        exitTransition = { fadeOut(tween(250)) }
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
                onNavigateToUsuarios = { navController.navigate(Screen.AdminUsuarios.route) { launchSingleTop = true } },
                onNavigateToInventario = { navController.navigate(Screen.AdminInventario.route) { launchSingleTop = true } },
                onNavigateToVentas = { navController.navigate(Screen.AdminVentas.route) { launchSingleTop = true } },
                onNavigateToCompraStock = { navController.navigate(Screen.AdminCompraStock.route) { launchSingleTop = true } },
                onNavigateToVisitas = { navController.navigate(Screen.AdminVisitas.route) { launchSingleTop = true } },
                onNavigateToVisitaRemota = { navController.navigate(Screen.AdminVisitaRemota.route) { launchSingleTop = true } },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.AdminUsuarios.route) {
            val adminUsuariosViewModel: AdminUsuariosViewModel = viewModel(factory = viewModelFactory)
            val listaUsuarios by adminUsuariosViewModel.usuarios.collectAsState()
            val listaMinimarkets: List<Minimarket> by minimarketDao.obtenerTodosLosMinimarkets().collectAsState(initial = emptyList())

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    usuarioRepository.sincronizarConApi()
                }
            }

            AdminUsuariosScreen(
                listaUsuarios = listaUsuarios,
                listaMinimarkets = listaMinimarkets,
                onAgregarUsuario = { nuevoUsuario, nuevoMinimarket, minimarketAEliminar ->
                    adminUsuariosViewModel.agregarUsuario(nuevoUsuario)
                    nuevoMinimarket?.let {
                        coroutineScope.launch {
                            minimarketDao.insertarMinimarket(it)
                            try {
                                RetrofitClient.springBootService.insertarMinimarket(it)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    minimarketAEliminar?.let {
                        coroutineScope.launch {
                            minimarketDao.eliminarMinimarket(it)
                            try {
                                RetrofitClient.springBootService.eliminarMinimarket(it.minimarketId)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                onEliminarUsuario = { usuarioAEliminar ->
                    adminUsuariosViewModel.eliminarUsuario(usuarioAEliminar)
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminInventario.route) {
            val repuestoViewModel: RepuestoViewModel = viewModel(factory = viewModelFactory)
            val listaInventario by repuestoViewModel.listaInventario.collectAsState()

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    repuestoRepository.sincronizarConApi()
                }
            }

            AdminInventarioScreen(
                listaInventario = listaInventario,
                onAgregarStock = { nuevoStock: InventarioRepuesto ->
                    repuestoViewModel.insertarRepuesto(nuevoStock)
                },
                onEliminarStock = { stockAEliminar: InventarioRepuesto ->
                    repuestoViewModel.eliminarRepuesto(stockAEliminar)
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
            val repuestoViewModel: RepuestoViewModel = viewModel(factory = viewModelFactory)
            val listaOrdenes by ordenCompraDao.obtenerTodasLasOrdenes().collectAsState(initial = emptyList())
            val listaInventario by repuestoViewModel.listaInventario.collectAsState()

            AdminCompraStockScreen(
                listaOrdenes = listaOrdenes,
                listaInventario = listaInventario,
                onRegistrarCompra = { nuevaOrden ->
                    coroutineScope.launch {
                        ordenCompraDao.insertarOrdenCompra(nuevaOrden)
                        try {
                            RetrofitClient.springBootService.insertarOrdenCompra(nuevaOrden)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val invExistente = repuestoRepository.obtenerPorId(nuevaOrden.repuestoId)
                        if (invExistente != null) {
                            val stockActual = invExistente.stockDisponible
                            val nuevoStockCalc = stockActual + nuevaOrden.cantidad
                            repuestoRepository.actualizarInventario(
                                invExistente.copy(
                                    stockDisponible = nuevoStockCalc,
                                    cantidadDisponible = nuevoStockCalc
                                )
                            )
                        } else {
                            repuestoRepository.insertarInventario(
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
            val visitaViewModel: VisitaViewModel = viewModel(factory = viewModelFactory)
            val listaVisitas by visitaViewModel.todasLasVisitas.collectAsState()
            AdminVisitasScreen(
                listaVisitas = listaVisitas,
                onActualizarVisita = { visitaActualizada ->
                    visitaViewModel.actualizarVisita(visitaActualizada)
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminVisitaRemota.route) {
            val listaRemotas by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())
            AdminVisitaRemotaScreen(
                listaRemotas = listaRemotas,
                onActualizarRemota = { remotaActualizada ->
                    coroutineScope.launch {
                        asistenciaRemotaDao.actualizarAsistencia(remotaActualizada)
                        try {
                            RetrofitClient.springBootService.actualizarAsistencia(remotaActualizada)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // --- MÓDULO DEL CLIENTE ---
        composable(Screen.ClienteHome.route) {
            ClienteHomeScreen(
                onNavigateToSolicitarCotizacion = { navController.navigate(Screen.ClienteSolicitarCotizacion.route) { launchSingleTop = true } },
                onNavigateToSolicitarVisita = { navController.navigate(Screen.ClienteSolicitarVisita.route) { launchSingleTop = true } },
                onNavigateToSolicitarVisitaRemota = { navController.navigate(Screen.ClienteSolicitarVisitaRemota.route) { launchSingleTop = true } },
                onNavigateToMisSolicitudes = { navController.navigate(Screen.ClienteMisSolicitudes.route) { launchSingleTop = true } },
                onNavigateToMisEquipos = { navController.navigate(Screen.ClienteMisEquipos.route) { launchSingleTop = true } },
                onNavigateToCatalogoEquipos = { navController.navigate(Screen.ClienteCatalogoEquipos.route) { launchSingleTop = true } },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.ClienteMisEquipos.route) {
            val equipoViewModel: EquipoViewModel = viewModel(factory = viewModelFactory)
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val minimarketId = minimarketCliente?.minimarketId ?: ""

            val equiposCliente by equipoViewModel.obtenerPorMinimarket(minimarketId).collectAsState(initial = emptyList())

            ClienteMisEquiposScreen(
                listaEquipos = equiposCliente,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteCatalogoEquipos.route) {
            val repuestoViewModel: RepuestoViewModel = viewModel(factory = viewModelFactory)
            val listaInventario by repuestoViewModel.listaInventario.collectAsState()

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    repuestoRepository.sincronizarConApi()
                }
            }

            ClienteCatalogoEquiposScreen(
                listaInventario = listaInventario,
                onSolicitarCotizacionClick = { navController.navigate(Screen.ClienteSolicitarCotizacion.route) { launchSingleTop = true } },
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
                        try {
                            RetrofitClient.springBootService.insertarVenta(nuevaCotizacion)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteSolicitarVisita.route) {
            val visitaViewModel: VisitaViewModel = viewModel(factory = viewModelFactory)
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val visitasExistentes by visitaViewModel.todasLasVisitas.collectAsState()

            ClienteSolicitarVisitaScreen(
                minimarketCliente = minimarketCliente,
                visitasExistentes = visitasExistentes,
                onSolicitarVisita = { nuevaVisita ->
                    visitaViewModel.insertarVisita(nuevaVisita)
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
                        try {
                            RetrofitClient.springBootService.insertarAsistencia(nuevaAsistencia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.ClienteMisSolicitudes.route) {
            val visitaViewModel: VisitaViewModel = viewModel(factory = viewModelFactory)
            val clienteActual = authViewModel.usuarioActual
            val minimarketCliente: Minimarket? by minimarketDao.obtenerMinimarketPorRut(clienteActual?.rut ?: "").collectAsState(initial = null)
            val idMinimarket = minimarketCliente?.minimarketId ?: ""

            val todasVentas: List<Venta> by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            val todasVisitas by visitaViewModel.todasLasVisitas.collectAsState()
            val todasRemotas: List<AsistenciaRemota> by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            val cotizacionesCliente = todasVentas.filter { it.minimarketId == idMinimarket }
            val visitasCliente = todasVisitas.filter { it.minimarketId == idMinimarket }
            val remotasCliente = todasRemotas.filter { it.minimarketId == idMinimarket }

            ClienteMisSolicitudesScreen(
                listaCotizaciones = cotizacionesCliente,
                listaVisitas = visitasCliente,
                listaRemotas = remotasCliente,
                onCancelarCotizacion = { cotizacion ->
                    coroutineScope.launch {
                        ventaDao.actualizarVenta(cotizacion)
                        try {
                            RetrofitClient.springBootService.actualizarVenta(cotizacion)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onCancelarVisita = { visita ->
                    visitaViewModel.actualizarVisita(visita)
                },
                onCancelarRemota = { asistencia ->
                    coroutineScope.launch {
                        asistenciaRemotaDao.actualizarAsistencia(asistencia)
                        try {
                            RetrofitClient.springBootService.actualizarAsistencia(asistencia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onReagendarCotizacion = { cotizacion ->
                    coroutineScope.launch {
                        ventaDao.actualizarVenta(cotizacion)
                        try {
                            RetrofitClient.springBootService.actualizarVenta(cotizacion)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onReagendarVisita = { visita ->
                    visitaViewModel.actualizarVisita(visita)
                },
                onReagendarRemota = { asistencia ->
                    coroutineScope.launch {
                        asistenciaRemotaDao.actualizarAsistencia(asistencia)
                        try {
                            RetrofitClient.springBootService.actualizarAsistencia(asistencia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // --- MÓDULOS DEL VENDEDOR ---
        composable(Screen.VendedorHome.route) {
            VendedorHomeScreen(
                onNavigateToCotizaciones = { navController.navigate(Screen.VendedorCotizaciones.route) { launchSingleTop = true } },
                onNavigateToRegistrarVenta = { navController.navigate(Screen.VendedorRegistrarVenta.route) { launchSingleTop = true } },
                onNavigateToInventario = { navController.navigate(Screen.VendedorInventario.route) { launchSingleTop = true } },
                onNavigateToMisVentas = { navController.navigate(Screen.VendedorMisVentas.route) { launchSingleTop = true } },
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
                    coroutineScope.launch {
                        ventaDao.actualizarVenta(cotizacionActualizada)
                        try {
                            RetrofitClient.springBootService.actualizarVenta(cotizacionActualizada)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.VendedorRegistrarVenta.route) {
            val equipoViewModel: EquipoViewModel = viewModel(factory = viewModelFactory)
            val repuestoViewModel: RepuestoViewModel = viewModel(factory = viewModelFactory)
            val vendedorActual = authViewModel.usuarioActual
            val listaMinimarkets by minimarketDao.obtenerTodosLosMinimarkets().collectAsState(initial = emptyList())
            val listaInventario by repuestoViewModel.listaInventario.collectAsState()

            VendedorRegistrarVentaScreen(
                vendedorActual = vendedorActual,
                listaMinimarkets = listaMinimarkets,
                listaInventario = listaInventario,
                onRegistrarOperacion = { nuevaVenta, inventarioActualizado ->
                    coroutineScope.launch {
                        ventaDao.insertarVenta(nuevaVenta)
                        try {
                            RetrofitClient.springBootService.insertarVenta(nuevaVenta)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        inventarioActualizado?.let { repuestoRepository.actualizarInventario(it) }

                        val invComprado = repuestoRepository.obtenerPorId(nuevaVenta.repuestoId)
                        val esEquipo = invComprado?.tipoItem?.equals("Equipo", ignoreCase = true) == true

                        if (esEquipo) {
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
                                equipoViewModel.agregarEquipo(nuevoEquipo)
                            }
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.VendedorInventario.route) {
            val repuestoViewModel: RepuestoViewModel = viewModel(factory = viewModelFactory)
            val listaInventario by repuestoViewModel.listaInventario.collectAsState()

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    repuestoRepository.sincronizarConApi()
                }
            }

            VendedorInventarioScreen(
                listaInventario = listaInventario,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.VendedorMisVentas.route) {
            val equipoViewModel: EquipoViewModel = viewModel(factory = viewModelFactory)
            val vendedorActual = authViewModel.usuarioActual
            val todasLasVentas by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            val misVentas = todasLasVentas.filter { it.vendedorId == vendedorActual?.usuarioId }

            VendedorMisVentasScreen(
                listaMisVentas = misVentas,
                onDarDeBajaArriendo = { arriendo ->
                    coroutineScope.launch {
                        val ventaActualizada = arriendo.copy(estado = "Arriendo Finalizado")
                        ventaDao.actualizarVenta(ventaActualizada)
                        try {
                            RetrofitClient.springBootService.actualizarVenta(ventaActualizada)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val invExistente = repuestoRepository.obtenerPorId(arriendo.repuestoId)
                        if (invExistente != null) {
                            val stockActual = invExistente.stockDisponible
                            val nuevoStock = stockActual + arriendo.cantidad
                            repuestoRepository.actualizarInventario(
                                invExistente.copy(
                                    stockDisponible = nuevoStock,
                                    cantidadDisponible = nuevoStock
                                )
                            )
                        }

                        try {
                            val listaEquipos = equipoViewModel.obtenerPorMinimarket(arriendo.minimarketId).first()
                            val equiposAModificar = listaEquipos.filter {
                                it.modalidad == "Arriendo" &&
                                        (it.nombre.contains(arriendo.productoNombre, ignoreCase = true) || arriendo.productoNombre.contains(it.nombre, ignoreCase = true)) &&
                                        !it.estadoOperativo.contains("Devuelto", ignoreCase = true)
                            }.take(arriendo.cantidad)

                            equiposAModificar.forEach { equipo ->
                                equipoViewModel.actualizarEquipo(equipo.copy(estadoOperativo = "Devuelto / Arriendo Finalizado"))
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
                onNavigateToVisitas = { navController.navigate(Screen.TecnicoVisitas.route) { launchSingleTop = true } },
                onNavigateToSalaRemota = { navController.navigate(Screen.TecnicoSalaRemota.route) { launchSingleTop = true } },
                onNavigateToInventarioRepuestos = { navController.navigate(Screen.TecnicoInventarioRepuestos.route) { launchSingleTop = true } },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }

        composable(Screen.TecnicoVisitas.route) {
            val visitaViewModel: VisitaViewModel = viewModel(factory = viewModelFactory)
            val listaVisitas by visitaViewModel.todasLasVisitas.collectAsState()
            val listaRemotas by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            TecnicoVisitasScreen(
                listaVisitas = listaVisitas,
                listaRemotas = listaRemotas,
                onActualizarVisita = { visita -> visitaViewModel.actualizarVisita(visita) },
                onActualizarRemota = { asistencia ->
                    coroutineScope.launch {
                        asistenciaRemotaDao.actualizarAsistencia(asistencia)
                        try {
                            RetrofitClient.springBootService.actualizarAsistencia(asistencia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.TecnicoSalaRemota.route) {
            val listaRemotas by asistenciaRemotaDao.obtenerTodasLasAsistencias().collectAsState(initial = emptyList())

            TecnicoSalaRemotaScreen(
                listaRemotas = listaRemotas,
                onFinalizarReunion = { asistenciaAtendida ->
                    coroutineScope.launch {
                        asistenciaRemotaDao.actualizarAsistencia(asistenciaAtendida)
                        try {
                            RetrofitClient.springBootService.actualizarAsistencia(asistenciaAtendida)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.TecnicoInventarioRepuestos.route) {
            val repuestoViewModel: RepuestoViewModel = viewModel(factory = viewModelFactory)
            val listaInventario by repuestoViewModel.listaInventario.collectAsState()

            TecnicoInventarioRepuestosScreen(
                listaInventario = listaInventario,
                onVolver = { navController.popBackStack() }
            )
        }

        // --- MÓDULO TRANSPORTISTA ---
        composable(Screen.TransportistaHome.route) {
            val listaVentas by ventaDao.obtenerTodasLasVentas().collectAsState(initial = emptyList())
            val listaMinimarkets by minimarketDao.obtenerTodosLosMinimarkets().collectAsState(initial = emptyList())

            TransportistaHomeScreen(
                listaDespachos = listaVentas,
                listaMinimarkets = listaMinimarkets,
                onActualizarEstadoDespacho = { despacho ->
                    coroutineScope.launch {
                        ventaDao.actualizarVenta(despacho)
                        try {
                            RetrofitClient.springBootService.actualizarVenta(despacho)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onLogout = { authViewModel.cerrarSesion() }
            )
        }
    }
}
