package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Repuesto
import com.example.caso_dsy1105_002v_tacticaldev.Repository.RepuestoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepuestoRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeRepuestoDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: RepuestoRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeRepuestoDao()
        fakeApiService = FakeTacticalApiService()
        repository = RepuestoRepository(fakeDao, fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInsertarYObtenerRepuesto() = runTest(testDispatcher) {
        val inventario = InventarioRepuesto(
            inventarioId = "INV-1",
            repuestoId = "REP-1",
            nombre = "Filtro",
            nombreRepuesto = "Filtro",
            categoria = "Repuestos",
            stockDisponible = 10,
            cantidadDisponible = 10
        )

        repository.insertarRepuesto(inventario)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultado = repository.obtenerPorId("INV-1")
        assertNotNull(resultado)
        assertEquals("Filtro", resultado?.nombre)
        assertEquals(1, fakeApiService.inventarios.size)
    }

    @Test
    fun testInsertarRepuestoDesdeObjetoRepuesto() = runTest(testDispatcher) {
        val repuesto = Repuesto(
            repuestoId = "REP-100",
            nombre = "Compresor",
            categoria = "Motor",
            precio = 50000.0,
            stock = 5
        )

        repository.insertarRepuesto(repuesto)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultado = repository.obtenerPorId("REP-100")
        assertNotNull(resultado)
        assertEquals("Compresor", resultado?.nombre)
    }

    @Test
    fun testActualizarInventario() = runTest(testDispatcher) {
        val inventario = InventarioRepuesto(
            inventarioId = "INV-2",
            repuestoId = "REP-2",
            nombre = "Valvula",
            stockDisponible = 5,
            cantidadDisponible = 5
        )
        repository.insertarRepuesto(inventario)
        testDispatcher.scheduler.advanceUntilIdle()

        val modificado = inventario.copy(stockDisponible = 15, cantidadDisponible = 15)
        repository.actualizarInventario(modificado)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultado = repository.obtenerPorId("INV-2")
        assertEquals(15, resultado?.stockDisponible)
        assertEquals(15, fakeApiService.inventarios.firstOrNull { it.inventarioId == "INV-2" }?.stockDisponible)
    }

    @Test
    fun testActualizarStockPorId() = runTest(testDispatcher) {
        val inventario = InventarioRepuesto(
            inventarioId = "INV-3",
            stockDisponible = 5,
            cantidadDisponible = 5
        )
        repository.insertarRepuesto(inventario)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.actualizarStock("INV-3", 20)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultado = repository.obtenerPorId("INV-3")
        assertEquals(20, resultado?.stockDisponible)
    }

    @Test
    fun testEliminarInventario() = runTest(testDispatcher) {
        val inventario = InventarioRepuesto(inventarioId = "INV-4", nombre = "Termostato")
        repository.insertarRepuesto(inventario)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.eliminarInventario(inventario)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultado = repository.obtenerPorId("INV-4")
        assertNull(resultado)
        assertEquals(0, fakeApiService.inventarios.size)
    }

    @Test
    fun testSincronizarConApi() = runTest(testDispatcher) {
        val apiItem = InventarioRepuesto(inventarioId = "INV-API-1", nombre = "Sensor Temp")
        fakeApiService.inventarios.add(apiItem)

        repository.sincronizarConApi()
        testDispatcher.scheduler.advanceUntilIdle()

        val listaLocal = repository.obtenerTodosLosInventarios().first()
        assertEquals(1, listaLocal.size)
        assertEquals("Sensor Temp", listaLocal[0].nombre)
    }

    @Test
    fun testObtenerStockConFiltro() = runTest(testDispatcher) {
        val i1 = InventarioRepuesto(inventarioId = "INV-10", nombre = "Motor A")
        val i2 = InventarioRepuesto(inventarioId = "INV-20", nombre = "Ventilador")
        repository.insertarRepuesto(i1)
        repository.insertarRepuesto(i2)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtrado = repository.obtenerStock("Motor").first()
        assertEquals(1, filtrado.size)
        assertEquals("Motor A", filtrado[0].nombre)
    }
}