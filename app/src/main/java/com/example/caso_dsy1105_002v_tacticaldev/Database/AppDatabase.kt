@file:Suppress("PackageName", "PackageDirectoryMismatch")
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
    version = 17, // <-- Incrementado a 17 para aplicar cambios de estructura (fotosUrls)
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
                try {
                    if (usuarioDao.obtenerUsuarioPorCorreo("admin@admindev.cl") == null) {
                        usuarioDao.insertarUsuario(
                            Usuario(
                                usuarioId = "USR-ADMIN-001",
                                rut = "11111111-1",
                                primerNombre = "Carlos",
                                segundoNombre = "Andrés",
                                apellido = "Mendoza",
                                correo = "admin@admindev.cl",
                                telefono = "911112222",
                                contrasena = "Admin1234@",
                                rol = Rol.ADMINISTRADOR
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}