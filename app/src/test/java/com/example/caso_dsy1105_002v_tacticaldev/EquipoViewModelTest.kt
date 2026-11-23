package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Repository.EquipoRepository
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.EquipoViewModel
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
class EquipoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeEquipoDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: EquipoRepository
    private lateinit var viewModel: EquipoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeEquipoDao()
        fakeApiService = FakeTacticalApiService()
        repository = EquipoRepository(fakeDao, fakeApiService)
        viewModel = EquipoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAgregarEquipo() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-VM-1", nombre = "Cooler VM")

        viewModel.agregarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = fakeDao.items.value
        assertEquals(1, lista.size)
        assertEquals("Cooler VM", lista[0].nombre)
    }

    @Test
    fun testActualizarEquipo() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-VM-2", nombre = "Original")
        viewModel.agregarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.actualizarEquipo(equipo.copy(nombre = "Modificado"))
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = fakeDao.obtenerPorCodigo("EQ-VM-2")
        assertEquals("Modificado", obtenido?.nombre)
    }

    @Test
    fun testActualizarEstado() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-VM-3", estadoOperativo = "Operativo")
        viewModel.agregarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.actualizarEstado("EQ-VM-3", "Falla Critica")
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = fakeDao.obtenerPorCodigo("EQ-VM-3")
        assertEquals("Falla Critica", obtenido?.estadoOperativo)
    }

    @Test
    fun testEliminarEquipo() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-VM-4", nombre = "Para Eliminar")
        viewModel.agregarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.eliminarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = fakeDao.items.value
        assertEquals(0, lista.size)
    }

    @Test
    fun testObtenerPorMinimarket() = runTest(testDispatcher) {
        val e1 = EquipoRefrigeracion(equipoId = "EQ-M1", minimarketId = "MINI-X")
        val e2 = EquipoRefrigeracion(equipoId = "EQ-M2", minimarketId = "MINI-Y")
        viewModel.agregarEquipo(e1)
        viewModel.agregarEquipo(e2)
        testDispatcher.scheduler.advanceUntilIdle()

        val porMin = viewModel.obtenerPorMinimarket("MINI-X").first()
        assertEquals(1, porMin.size)
        assertEquals("EQ-M1", porMin[0].equipoId)
    }
}