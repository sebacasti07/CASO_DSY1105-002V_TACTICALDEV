@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")

    // Módulos Administrador
    object AdminHome : Screen("admin_home_screen")
    object AdminUsuarios : Screen("admin_usuarios_screen")
    object AdminInventario : Screen("admin_inventario_screen")
    object AdminVentas : Screen("admin_ventas_screen")
    object AdminCompraStock : Screen("admin_compra_stock_screen")
    object AdminVisitas : Screen("admin_visitas_screen")
    object AdminVisitaRemota : Screen("admin_visita_remota_screen")

    // Módulos Cliente
    object ClienteHome : Screen("cliente_home_screen")
    object ClienteSolicitarCotizacion : Screen("cliente_solicitar_cotizacion_screen")
    object ClienteSolicitarVisita : Screen("cliente_solicitar_visita_screen")
    object ClienteSolicitarVisitaRemota : Screen("cliente_solicitar_visita_remota_screen")
    object ClienteMisSolicitudes : Screen("cliente_mis_solicitudes_screen")
    object ClienteMisEquipos : Screen("cliente_mis_equipos_screen")
    object ClienteCatalogoEquipos : Screen("cliente_catalogo_equipos_screen")

    // Módulos Vendedor
    object VendedorHome : Screen("vendedor_home_screen")
    object VendedorCotizaciones : Screen("vendedor_cotizaciones_screen")
    object VendedorRegistrarVenta : Screen("vendedor_registrar_venta_screen")
    object VendedorInventario : Screen("vendedor_inventario_screen")
    object VendedorMisVentas : Screen("vendedor_mis_ventas_screen")

    // Módulos Técnico
    object TecnicoHome : Screen("tecnico_home_screen")
    object TecnicoVisitas : Screen("tecnico_visitas_screen")
    object TecnicoSalaRemota : Screen("tecnico_sala_remota_screen")

    // Módulo Transportista
    object TransportistaHome : Screen("transportista_home_screen")
}