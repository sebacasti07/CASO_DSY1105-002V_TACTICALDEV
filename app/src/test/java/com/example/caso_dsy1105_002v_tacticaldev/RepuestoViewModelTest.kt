package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Repuesto
import com.example.caso_dsy1105_002v_tacticaldev.Repository.RepuestoRepository
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.RepuestoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepuestoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeRepuestoDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: RepuestoRepository
    private lateinit var viewModel: RepuestoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeRepuestoDao()
        fakeApiService = FakeTacticalApiService()
        repository = RepuestoRepository(fakeDao, fakeApiService)
        viewModel = RepuestoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAgregarEInsertarRepuesto() = runTest(testDispatcher) {
        val item = InventarioRepuesto(inventarioId = "INV-VM-1", nombre = "Bomba Agua")

        viewModel.agregarRepuesto(item)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = fakeDao.items.value
        assertEquals(1, lista.size)
        assertEquals("Bomba Agua", lista[0].nombre)

        val repuestoObj = Repuesto(repuestoId = "REP-OBJ-1", nombre = "Aceite", categoria = "Lubricante", precio = 1000.0, stock = 10)
        viewModel.agregarRepuesto(repuestoObj)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, fakeDao.items.value.size)
    }

    @Test
    fun testActualizarStock() = runTest(testDispatcher) {
        val item = InventarioRepuesto(inventarioId = "INV-VM-2", stockDisponible = 5)
        viewModel.agregarRepuesto(item)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.actualizarStock("INV-VM-2", 30)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = fakeDao.obtenerPorId("INV-VM-2")
        assertEquals(30, obtenido?.stockDisponible)
    }

    @Test
    fun testEliminarRepuesto() = runTest(testDispatcher) {
        val item = InventarioRepuesto(inventarioId = "INV-VM-3", nombre = "Filtro Aire")
        viewModel.agregarRepuesto(item)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.eliminarRepuesto(item)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = fakeDao.items.value
        assertEquals(0, lista.size)
    }

    @Test
    fun testObtenerStockConParametro() = runTest(testDispatcher) {
        val item1 = InventarioRepuesto(inventarioId = "INV-101", nombre = "Empacadura")
        val item2 = InventarioRepuesto(inventarioId = "INV-102", nombre = "Tornillo")
        viewModel.agregarRepuesto(item1)
        viewModel.agregarRepuesto(item2)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultado = viewModel.obtenerStock("Empacadura").first()
        assertEquals(1, resultado.size)
        assertEquals("Empacadura", resultado[0].nombre)
    }
}