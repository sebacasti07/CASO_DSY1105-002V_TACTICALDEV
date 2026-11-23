package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import com.example.caso_dsy1105_002v_tacticaldev.Repository.VisitaRepository
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.VisitaViewModel
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
class VisitaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeVisitaDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: VisitaRepository
    private lateinit var viewModel: VisitaViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeVisitaDao()
        fakeApiService = FakeTacticalApiService()
        repository = VisitaRepository(fakeDao, fakeApiService)
        viewModel = VisitaViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testRegistrarEInsertarVisita() = runTest(testDispatcher) {
        val v1 = Visita(visitaId = "VIS-VM-1", motivo = "Revision de Rutina")

        viewModel.registrarVisitaTerreno(v1)
        testDispatcher.scheduler.advanceUntilIdle()

        var lista = fakeDao.items.value
        assertEquals(1, lista.size)

        val v2 = Visita(visitaId = "VIS-VM-2", motivo = "Urgencia")
        viewModel.insertarVisita(v2)
        testDispatcher.scheduler.advanceUntilIdle()

        lista = fakeDao.items.value
        assertEquals(2, lista.size)
    }

    @Test
    fun testActualizarVisita() = runTest(testDispatcher) {
        val visita = Visita(visitaId = "VIS-VM-3", estado = "Programada")
        viewModel.insertarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.actualizarVisita(visita.copy(estado = "En Proceso"))
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = fakeDao.obtenerVisitaPorId("VIS-VM-3")
        assertEquals("En Proceso", obtenido?.estado)
    }

    @Test
    fun testEliminarVisita() = runTest(testDispatcher) {
        val visita = Visita(visitaId = "VIS-VM-4", motivo = "Mantencion")
        viewModel.insertarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.eliminarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = fakeDao.items.value
        assertEquals(0, lista.size)
    }

    @Test
    fun testObtenerVisitasPorMinimarket() = runTest(testDispatcher) {
        val v1 = Visita(visitaId = "VIS-M1", minimarketId = "MINI-ALPHA")
        val v2 = Visita(visitaId = "VIS-M2", minimarketId = "MINI-BETA")
        viewModel.insertarVisita(v1)
        viewModel.insertarVisita(v2)
        testDispatcher.scheduler.advanceUntilIdle()

        val deAlpha = viewModel.obtenerVisitasPorMinimarket("MINI-ALPHA").first()
        assertEquals(1, deAlpha.size)
        assertEquals("VIS-M1", deAlpha[0].visitaId)
    }
}