package com.example.caso_dsy1105_002v_tacticaldev.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AzulMarinoPrinciapl,
    onPrimary = Color.White,
    primaryContainer = ContenedorCelesteHielo,
    onPrimaryContainer = TextoContenedorAzul,

    secondary = AzulGlacialSecundario,
    onSecondary = Color.White,
    secondaryContainer = ContenedorAzulGlacial,
    onSecondaryContainer = Color(0xFF071E2B),

    tertiary = CianHieloTerciario,
    onTertiary = Color.White,
    tertiaryContainer = ContenedorCianHielo,
    onTertiaryContainer = Color(0xFF001F23),

    background = FondoBlancoGlacial,
    onBackground = Color(0xFF0F1D26),
    surface = SuperficieBlanca,
    onSurface = Color(0xFF0F1D26),
    surfaceVariant = VarianteSuperficieCeleste,
    onSurfaceVariant = Color(0xFF3F4850)
)

private val DarkColorScheme = darkColorScheme(
    primary = CelesteHieloDark,
    onPrimary = Color(0xFF003453),
    primaryContainer = ContenedorAzulDark,
    onPrimaryContainer = ContenedorCelesteHielo,

    secondary = Color(0xFFB1CBDC),
    onSecondary = Color(0xFF1C3443),

    background = FondoOscuroGlacial,
    onBackground = Color(0xFFDEE8F0),
    surface = SuperficieOscuraGlacial,
    onSurface = Color(0xFFDEE8F0)
)

@Composable
fun TacticalDevTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivado para forzar la paleta temática de Hielo
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}