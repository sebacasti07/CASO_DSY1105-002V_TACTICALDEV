package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.*
import com.example.caso_dsy1105_002v_tacticaldev.Network.TacticalApiService
import retrofit2.Response

class FakeTacticalApiService : TacticalApiService {
    val usuarios = mutableListOf<Usuario>()
    val inventarios = mutableListOf<InventarioRepuesto>()
    val ventas = mutableListOf<Venta>()
    val visitas = mutableListOf<Visita>()
    val asistencias = mutableListOf<AsistenciaRemota>()
    val minimarkets = mutableListOf<Minimarket>()
    val equipos = mutableListOf<EquipoRefrigeracion>()
    val ordenes = mutableListOf<OrdenCompra>()

    var throwError = false

    override suspend fun obtenerUsuarios(): Response<List<Usuario>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(usuarios.toList())
    }

    override suspend fun insertarUsuario(usuario: Usuario): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        usuarios.removeAll { it.usuarioId == usuario.usuarioId }
        usuarios.add(usuario)
        return Response.success(null)
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        usuarios.removeAll { it.usuarioId == usuario.usuarioId }
        usuarios.add(usuario)
        return Response.success(null)
    }

    override suspend fun eliminarUsuario(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        usuarios.removeAll { it.usuarioId == id }
        return Response.success(null)
    }

    override suspend fun obtenerInventarios(): Response<List<InventarioRepuesto>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(inventarios.toList())
    }

    override suspend fun insertarInventario(item: InventarioRepuesto): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        inventarios.removeAll { it.inventarioId == item.inventarioId }
        inventarios.add(item)
        return Response.success(null)
    }

    override suspend fun actualizarInventario(item: InventarioRepuesto): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        inventarios.removeAll { it.inventarioId == item.inventarioId }
        inventarios.add(item)
        return Response.success(null)
    }

    override suspend fun eliminarInventario(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        inventarios.removeAll { it.inventarioId == id }
        return Response.success(null)
    }

    override suspend fun obtenerVentas(): Response<List<Venta>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(ventas.toList())
    }

    override suspend fun insertarVenta(venta: Venta): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        ventas.removeAll { it.ventaId == venta.ventaId }
        ventas.add(venta)
        return Response.success(null)
    }

    override suspend fun actualizarVenta(venta: Venta): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        ventas.removeAll { it.ventaId == venta.ventaId }
        ventas.add(venta)
        return Response.success(null)
    }

    override suspend fun eliminarVenta(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        ventas.removeAll { it.ventaId == id }
        return Response.success(null)
    }

    override suspend fun obtenerVisitas(): Response<List<Visita>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(visitas.toList())
    }

    override suspend fun insertarVisita(visita: Visita): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        visitas.removeAll { it.visitaId == visita.visitaId }
        visitas.add(visita)
        return Response.success(null)
    }

    override suspend fun actualizarVisita(visita: Visita): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        visitas.removeAll { it.visitaId == visita.visitaId }
        visitas.add(visita)
        return Response.success(null)
    }

    override suspend fun eliminarVisita(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        visitas.removeAll { it.visitaId == id }
        return Response.success(null)
    }

    override suspend fun obtenerAsistencias(): Response<List<AsistenciaRemota>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(asistencias.toList())
    }

    override suspend fun insertarAsistencia(asistencia: AsistenciaRemota): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        asistencias.removeAll { it.asistenciaId == asistencia.asistenciaId }
        asistencias.add(asistencia)
        return Response.success(null)
    }

    override suspend fun actualizarAsistencia(asistencia: AsistenciaRemota): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        asistencias.removeAll { it.asistenciaId == asistencia.asistenciaId }
        asistencias.add(asistencia)
        return Response.success(null)
    }

    override suspend fun eliminarAsistencia(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        asistencias.removeAll { it.asistenciaId == id }
        return Response.success(null)
    }

    override suspend fun obtenerMinimarkets(): Response<List<Minimarket>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(minimarkets.toList())
    }

    override suspend fun insertarMinimarket(minimarket: Minimarket): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        minimarkets.removeAll { it.minimarketId == minimarket.minimarketId }
        minimarkets.add(minimarket)
        return Response.success(null)
    }

    override suspend fun actualizarMinimarket(minimarket: Minimarket): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        minimarkets.removeAll { it.minimarketId == minimarket.minimarketId }
        minimarkets.add(minimarket)
        return Response.success(null)
    }

    override suspend fun eliminarMinimarket(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        minimarkets.removeAll { it.minimarketId == id }
        return Response.success(null)
    }

    override suspend fun obtenerEquipos(): Response<List<EquipoRefrigeracion>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(equipos.toList())
    }

    override suspend fun insertarEquipo(equipo: EquipoRefrigeracion): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        equipos.removeAll { it.equipoId == equipo.equipoId }
        equipos.add(equipo)
        return Response.success(null)
    }

    override suspend fun actualizarEquipo(equipo: EquipoRefrigeracion): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        equipos.removeAll { it.equipoId == equipo.equipoId }
        equipos.add(equipo)
        return Response.success(null)
    }

    override suspend fun eliminarEquipo(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        equipos.removeAll { it.equipoId == id }
        return Response.success(null)
    }

    override suspend fun obtenerOrdenesCompra(): Response<List<OrdenCompra>> {
        if (throwError) throw RuntimeException("Network Error")
        return Response.success(ordenes.toList())
    }

    override suspend fun insertarOrdenCompra(orden: OrdenCompra): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        ordenes.removeAll { it.ordenId == orden.ordenId }
        ordenes.add(orden)
        return Response.success(null)
    }

    override suspend fun actualizarOrdenCompra(orden: OrdenCompra): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        ordenes.removeAll { it.ordenId == orden.ordenId }
        ordenes.add(orden)
        return Response.success(null)
    }

    override suspend fun eliminarOrdenCompra(id: String): Response<Void> {
        if (throwError) throw RuntimeException("Network Error")
        ordenes.removeAll { it.ordenId == id }
        return Response.success(null)
    }
}