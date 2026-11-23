package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Repository.EquipoRepository
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
class EquipoRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeEquipoDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: EquipoRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeEquipoDao()
        fakeApiService = FakeTacticalApiService()
        repository = EquipoRepository(fakeDao, fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInsertarYObtenerEquipo() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(
            equipoId = "EQ-1",
            minimarketId = "MINI-1",
            nombre = "Visicooler Dual",
            tipo = "Cooler",
            estadoOperativo = "Operativo"
        )

        repository.insertarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = repository.obtenerPorCodigo("EQ-1")
        assertNotNull(obtenido)
        assertEquals("Visicooler Dual", obtenido?.nombre)
        assertEquals(1, fakeApiService.equipos.size)
    }

    @Test
    fun testActualizarEquipo() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-2", nombre = "Freezer A")
        repository.insertarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        val actualizado = equipo.copy(nombre = "Freezer A Pro")
        repository.actualizarEquipo(actualizado)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = repository.obtenerPorCodigo("EQ-2")
        assertEquals("Freezer A Pro", obtenido?.nombre)
        assertEquals("Freezer A Pro", fakeApiService.equipos.firstOrNull { it.equipoId == "EQ-2" }?.nombre)
    }

    @Test
    fun testActualizarEstado() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-3", estadoOperativo = "Operativo")
        repository.insertarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.actualizarEstado("EQ-3", "En Mantencion")
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = repository.obtenerPorCodigo("EQ-3")
        assertEquals("En Mantencion", obtenido?.estadoOperativo)
    }

    @Test
    fun testEliminarEquipo() = runTest(testDispatcher) {
        val equipo = EquipoRefrigeracion(equipoId = "EQ-4", nombre = "Camara Fria")
        repository.insertarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.eliminarEquipo(equipo)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = repository.obtenerPorCodigo("EQ-4")
        assertNull(obtenido)
        assertEquals(0, fakeApiService.equipos.size)
    }

    @Test
    fun testObtenerPorMinimarket() = runTest(testDispatcher) {
        val e1 = EquipoRefrigeracion(equipoId = "EQ-10", minimarketId = "MINI-100")
        val e2 = EquipoRefrigeracion(equipoId = "EQ-20", minimarketId = "MINI-200")
        repository.insertarEquipo(e1)
        repository.insertarEquipo(e2)
        testDispatcher.scheduler.advanceUntilIdle()

        val listaMin100 = repository.obtenerPorMinimarket("MINI-100").first()
        assertEquals(1, listaMin100.size)
        assertEquals("EQ-10", listaMin100[0].equipoId)
    }

    @Test
    fun testSincronizarConApi() = runTest(testDispatcher) {
        val apiEquipo = EquipoRefrigeracion(equipoId = "EQ-API-1", nombre = "Vitrina Exhibidora")
        fakeApiService.equipos.add(apiEquipo)

        repository.sincronizarConApi()
        testDispatcher.scheduler.advanceUntilIdle()

        val listaLocal = repository.obtenerTodosLosEquipos().first()
        assertEquals(1, listaLocal.size)
        assertEquals("Vitrina Exhibidora", listaLocal[0].nombre)
    }
}