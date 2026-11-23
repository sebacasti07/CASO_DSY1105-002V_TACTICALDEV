package com.example.caso_dsy1105_002v_tacticaldev.Network

import com.example.caso_dsy1105_002v_tacticaldev.Model.*
import retrofit2.Response
import retrofit2.http.*

interface TacticalApiService {
    @GET("usuarios")
    suspend fun obtenerUsuarios(): Response<List<Usuario>>

    @POST("usuarios")
    suspend fun insertarUsuario(@Body usuario: Usuario): Response<Void>

    @PUT("usuarios")
    suspend fun actualizarUsuario(@Body usuario: Usuario): Response<Void>

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: String): Response<Void>

    @GET("inventario")
    suspend fun obtenerInventarios(): Response<List<InventarioRepuesto>>

    @POST("inventario")
    suspend fun insertarInventario(@Body item: InventarioRepuesto): Response<Void>

    @PUT("inventario")
    suspend fun actualizarInventario(@Body item: InventarioRepuesto): Response<Void>

    @DELETE("inventario/{id}")
    suspend fun eliminarInventario(@Path("id") id: String): Response<Void>

    @GET("ventas")
    suspend fun obtenerVentas(): Response<List<Venta>>

    @POST("ventas")
    suspend fun insertarVenta(@Body venta: Venta): Response<Void>

    @PUT("ventas")
    suspend fun actualizarVenta(@Body venta: Venta): Response<Void>

    @DELETE("ventas/{id}")
    suspend fun eliminarVenta(@Path("id") id: String): Response<Void>

    @GET("visitas")
    suspend fun obtenerVisitas(): Response<List<Visita>>

    @POST("visitas")
    suspend fun insertarVisita(@Body visita: Visita): Response<Void>

    @PUT("visitas")
    suspend fun actualizarVisita(@Body visita: Visita): Response<Void>

    @DELETE("visitas/{id}")
    suspend fun eliminarVisita(@Path("id") id: String): Response<Void>

    @GET("asistencias")
    suspend fun obtenerAsistencias(): Response<List<AsistenciaRemota>>

    @POST("asistencias")
    suspend fun insertarAsistencia(@Body asistencia: AsistenciaRemota): Response<Void>

    @PUT("asistencias")
    suspend fun actualizarAsistencia(@Body asistencia: AsistenciaRemota): Response<Void>

    @DELETE("asistencias/{id}")
    suspend fun eliminarAsistencia(@Path("id") id: String): Response<Void>

    @GET("minimarkets")
    suspend fun obtenerMinimarkets(): Response<List<Minimarket>>

    @POST("minimarkets")
    suspend fun insertarMinimarket(@Body minimarket: Minimarket): Response<Void>

    @PUT("minimarkets")
    suspend fun actualizarMinimarket(@Body minimarket: Minimarket): Response<Void>

    @DELETE("minimarkets/{id}")
    suspend fun eliminarMinimarket(@Path("id") id: String): Response<Void>

    @GET("equipos")
    suspend fun obtenerEquipos(): Response<List<EquipoRefrigeracion>>

    @POST("equipos")
    suspend fun insertarEquipo(@Body equipo: EquipoRefrigeracion): Response<Void>

    @PUT("equipos")
    suspend fun actualizarEquipo(@Body equipo: EquipoRefrigeracion): Response<Void>

    @DELETE("equipos/{id}")
    suspend fun eliminarEquipo(@Path("id") id: String): Response<Void>

    @GET("ordenes-compra")
    suspend fun obtenerOrdenesCompra(): Response<List<OrdenCompra>>

    @POST("ordenes-compra")
    suspend fun insertarOrdenCompra(@Body orden: OrdenCompra): Response<Void>

    @PUT("ordenes-compra")
    suspend fun actualizarOrdenCompra(@Body orden: OrdenCompra): Response<Void>

    @DELETE("ordenes-compra/{id}")
    suspend fun eliminarOrdenCompra(@Path("id") id: String): Response<Void>
}
