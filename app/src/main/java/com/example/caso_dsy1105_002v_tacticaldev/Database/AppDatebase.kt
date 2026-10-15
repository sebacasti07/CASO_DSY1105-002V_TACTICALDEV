package com.example.caso_dsy1105_002v_tacticaldev.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import com.example.caso_dsy1105_002v_tacticaldev.Model.OrdenCompra
import com.example.caso_dsy1105_002v_tacticaldev.Model.Repuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Usuario::class,
        Minimarket::class,
        EquipoRefrigeracion::class,
        Repuesto::class,
        InventarioRepuesto::class,
        AsistenciaRemota::class,
        Visita::class,
        Venta::class,
        OrdenCompra::class
    ],
    version = 17,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun minimarketDao(): MinimarketDao
    abstract fun equipoDao(): EquipoDao
    abstract fun visitaDao(): VisitaDao
    abstract fun repuestoDao(): RepuestoDao
    abstract fun ventaDao(): VentaDao
    abstract fun ordenCompraDao(): OrdenCompraDao
    abstract fun asistenciaRemotaDao(): AsistenciaRemotaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tacticaldev_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        insertarAdminSinoExiste(database.usuarioDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        insertarAdminSinoExiste(database.usuarioDao())
                    }
                }
            }

            private suspend fun insertarAdminSinoExiste(usuarioDao: UsuarioDao) {
                if (usuarioDao.obtenerUsuarioPorCorreo("admin@admindev.cl") == null) {
                    usuarioDao.insertarUsuario(
                        Usuario(
                            usuarioId = "USR-ADMIN-001",
                            rut = "11111111-1",
                            primerNombre = "Carlos",
                            segundoNombre = "Andrés",
                            apellido = "Mendoza",
                            correo = "admin@admindev.cl",
                            telefono = "+56911112222",
                            contrasena = "Admin1234@",
                            rol = Rol.ADMINISTRADOR
                        )
                    )
                }


                if (usuarioDao.obtenerUsuarioPorCorreo("transporte@transportedev.cl") == null) {
                    usuarioDao.insertarUsuario(
                        Usuario(
                            usuarioId = "USR-TRANS-001",
                            rut = "22222222-2",
                            primerNombre = "Pedro",
                            segundoNombre = "Antonio",
                            apellido = "Soto",
                            correo = "transporte@transportedev.cl",
                            telefono = "+56922223333",
                            contrasena = "Trans1234@",
                            rol = Rol.TRANSPORTISTA
                        )
                    )
                }


                if (usuarioDao.obtenerUsuarioPorCorreo("vendedor@vendedordev.cl") == null) {
                    usuarioDao.insertarUsuario(
                        Usuario(
                            usuarioId = "USR-VEND-001",
                            rut = "33333333-3",
                            primerNombre = "María",
                            segundoNombre = "Jesús",
                            apellido = "Pérez",
                            correo = "vendedor@vendedordev.cl",
                            telefono = "+56933334444",
                            contrasena = "Vend1234@",
                            rol = Rol.VENDEDOR
                        )
                    )
                }


                if (usuarioDao.obtenerUsuarioPorCorreo("cliente@usuariodev.cl") == null) {
                    usuarioDao.insertarUsuario(
                        Usuario(
                            usuarioId = "USR-CLI-001",
                            rut = "44444444-4",
                            primerNombre = "Juan",
                            segundoNombre = "Alberto",
                            apellido = "Gómez",
                            correo = "cliente@usuariodev.cl",
                            telefono = "+56944445555",
                            contrasena = "Cliente1234@",
                            rol = Rol.CLIENTE
                        )
                    )
                }


                if (usuarioDao.obtenerUsuarioPorCorreo("tecnico@tecdev.cl") == null) {
                    usuarioDao.insertarUsuario(
                        Usuario(
                            usuarioId = "USR-TEC-001",
                            rut = "55555555-5",
                            primerNombre = "Luis",
                            segundoNombre = "Felipe",
                            apellido = "Rojas",
                            correo = "tecnico@tecdev.cl",
                            telefono = "+56955556666",
                            contrasena = "Tec1234@",
                            rol = Rol.TECNICO
                        )
                    )
                }
            }
        }

    }
}