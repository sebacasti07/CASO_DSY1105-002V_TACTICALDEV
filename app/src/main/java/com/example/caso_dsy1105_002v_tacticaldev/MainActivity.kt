package com.example.caso_dsy1105_002v_tacticaldev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.caso_dsy1105_002v_tacticaldev.Database.AppDatabase
import com.example.caso_dsy1105_002v_tacticaldev.Navigation.AppNavGraph
import com.example.caso_dsy1105_002v_tacticaldev.Repository.EquipoRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.RepuestoRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.UsuarioRepository
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthViewModel
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.ViewModelFactory
import com.example.caso_dsy1105_002v_tacticaldev.ui.theme.TacticalDevTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val usuarioDao = database.usuarioDao()
        val repuestoDao = database.repuestoDao()
        val equipoDao = database.equipoDao()

        val usuarioRepository = UsuarioRepository(usuarioDao)
        val repuestoRepository = RepuestoRepository(repuestoDao)
        val equipoRepository = EquipoRepository(equipoDao)

        val factory = ViewModelFactory(
            usuarioDao = usuarioDao,
            repuestoRepository = repuestoRepository,
            equipoRepository = equipoRepository,
            usuarioRepository = usuarioRepository
        )

        val authViewModel: AuthViewModel by viewModels { factory }

        setContent {
            TacticalDevTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}