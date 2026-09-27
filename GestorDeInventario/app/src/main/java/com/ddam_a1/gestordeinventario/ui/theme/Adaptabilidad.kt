package com.ddam_a1.gestordeinventario.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================================
//  LOS TRES TAMANOS  (Figma: Compact 412 / Medium 700 / Tablet 1280)
//
//  Los cortes son los de Material 3, y no los anchos exactos del Figma: un
//  telefono girado mide ~700 y una tableta chica ~840. Si se cortara en 412 y
//  700 exactos, cualquier pantalla intermedia caeria en el cajon equivocado.
//
//  No se usa WindowSizeClass porque pedria otra dependencia; con el ancho
//  disponible alcanza y se mide en el mismo sitio donde se dibuja.
// ============================================================

enum class AnchoPantalla { COMPACTA, MEDIA, EXPANDIDA }

fun anchoPantallaDe(anchoDisponible: Dp): AnchoPantalla = when {
    anchoDisponible < 600.dp -> AnchoPantalla.COMPACTA
    anchoDisponible < 840.dp -> AnchoPantalla.MEDIA
    else -> AnchoPantalla.EXPANDIDA
}

object Anchos {
    /** En Medium el formulario no se estira: se queda en 400 y se centra (Figma 38:853). */
    val formularioMedio = 400.dp

    /** En Tablet el contenido va en una tarjeta centrada (Figma 38:968, 38:1000, 38:1036). */
    val tarjetaAcceso = 480.dp
    val tarjetaFormulario = 520.dp
    val tarjetaAncha = 680.dp
}
