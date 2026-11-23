package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import com.example.caso_dsy1105_002v_tacticaldev.Repository.VisitaRepository
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
class VisitaRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeVisitaDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: VisitaRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeVisitaDao()
        fakeApiService = FakeTacticalApiService()
        repository = VisitaRepository(fakeDao, fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInsertarYObtenerVisitas() = runTest(testDispatcher) {
        val visita = Visita(
            visitaId = "VIS-1",
            minimarketId = "MINI-1",
            motivo = "Preventiva",
            estado = "Pendiente"
        )

        repository.insertarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        val todas = repository.obtenerTodasLasVisitas().first()
        assertEquals(1, todas.size)
        assertEquals("VIS-1", todas[0].visitaId)
        assertEquals(1, fakeApiService.visitas.size)
    }

    @Test
    fun testActualizarVisita() = runTest(testDispatcher) {
        val visita = Visita(visitaId = "VIS-2", estado = "Pendiente")
        repository.insertarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        val actualizada = visita.copy(estado = "Completada")
        repository.actualizarVisita(actualizada)
        testDispatcher.scheduler.advanceUntilIdle()

        val todas = repository.obtenerTodasLasVisitas().first()
        assertEquals("Completada", todas[0].estado)
        assertEquals("Completada", fakeApiService.visitas.firstOrNull { it.visitaId == "VIS-2" }?.estado)
    }

    @Test
    fun testEliminarVisita() = runTest(testDispatcher) {
        val visita = Visita(visitaId = "VIS-3", motivo = "Correctiva")
        repository.insertarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.eliminarVisita(visita)
        testDispatcher.scheduler.advanceUntilIdle()

        val todas = repository.obtenerTodasLasVisitas().first()
        assertEquals(0, todas.size)
        assertEquals(0, fakeApiService.visitas.size)
    }

    @Test
    fun testObtenerVisitasPorMinimarket() = runTest(testDispatcher) {
        val v1 = Visita(visitaId = "VIS-10", minimarketId = "M-1")
        val v2 = Visita(visitaId = "VIS-20", minimarketId = "M-2")
        repository.insertarVisita(v1)
        repository.insertarVisita(v2)
        testDispatcher.scheduler.advanceUntilIdle()

        val deM1 = repository.obtenerVisitasPorMinimarket("M-1").first()
        assertEquals(1, deM1.size)
        assertEquals("VIS-10", deM1[0].visitaId)
    }

    @Test
    fun testSincronizarConApi() = runTest(testDispatcher) {
        val apiVisita = Visita(visitaId = "VIS-API-1", motivo = "Instalacion")
        fakeApiService.visitas.add(apiVisita)

        repository.sincronizarConApi()
        testDispatcher.scheduler.advanceUntilIdle()

        val todas = repository.obtenerTodasLasVisitas().first()
        assertEquals(1, todas.size)
        assertEquals("Instalacion", todas[0].motivo)
    }
}