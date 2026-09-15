package com.ddam_a1.gestordeinventario.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = Superficie,
    primaryContainer = PrimarioSuave,
    onPrimaryContainer = Primario,
    secondary = Secundario,
    onSecondary = Superficie,
    secondaryContainer = SecundarioSuave,
    onSecondaryContainer = Tinta,
    tertiary = Acento,
    onTertiary = Superficie,
    tertiaryContainer = AcentoSuave,
    onTertiaryContainer = Tinta,
    error = Peligro,
    onError = Superficie,
    errorContainer = PeligroSuave,
    onErrorContainer = Peligro,
    background = Fondo,
    onBackground = Tinta,
    surface = Superficie,
    onSurface = Tinta,
    surfaceVariant = SuperficieAlt,
    onSurfaceVariant = TintaSuave,
    outline = Borde,
    outlineVariant = Borde
)

private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioClaro,
    onPrimary = FondoOscuro,
    primaryContainer = Primario,
    onPrimaryContainer = TintaClara,
    secondary = Secundario,
    onSecondary = FondoOscuro,
    secondaryContainer = SuperficieAltOsc,
    onSecondaryContainer = TintaClara,
    tertiary = Acento,
    onTertiary = FondoOscuro,
    tertiaryContainer = SuperficieAltOsc,
    onTertiaryContainer = TintaClara,
    error = Peligro,
    onError = TintaClara,
    background = FondoOscuro,
    onBackground = TintaClara,
    surface = SuperficieOscura,
    onSurface = TintaClara,
    surfaceVariant = SuperficieAltOsc,
    onSurfaceVariant = TintaClaraSuave,
    outline = BordeOscuro,
    outlineVariant = BordeOscuro
)

@Composable
fun GestorDeInventarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Tipografia,
        content = content
    )
}
