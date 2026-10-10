package com.example.rentail_conteo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val EsquemaOscuro = darkColorScheme(
    primary = AcentoSuaveInv,
    onPrimary = Negro,
    secondary = AcentoOscuroInv,
    onSecondary = TextoPrimarioInv,
    background = ColorFondoInv,
    onBackground = TextoPrimarioInv,
    surface = Negro,
    onSurface = TextoPrimarioInv,
    surfaceVariant = ColorFondoInv,
    onSurfaceVariant = TextoSecundarioInv
)

private val EsquemaClaro = lightColorScheme(
    primary = AcentoOscuro,
    onPrimary = Blanco,
    secondary = AcentoSuave,
    onSecondary = TextoPrimario,
    background = ColorFondo,
    onBackground = TextoPrimario,
    surface = Blanco,
    onSurface = TextoPrimario,
    surfaceVariant = ColorFondo,
    onSurfaceVariant = TextoSecundario
)

private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun Rentail_ConteoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Tipografia,
        shapes = Formas,
        content = content
    )
}