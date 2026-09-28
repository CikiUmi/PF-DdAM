package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

// ============================================================
//  CHIP DE ESTADO  (Figma 36:70, 36:73, 36:76)
//
//  Los estados del inventario. Es un enum y no un composable por estado
//  porque el compilador obliga a cubrirlos todos en cada `when`: el dia que
//  se agregue uno, no se puede olvidar ninguna pantalla.
//
//  El enum vive aqui y la ETIQUETA vive dentro de el, no en quien lo pinta.
//  Hay dos formas de ensenarlo —este chip con punto y `PastillaEstado` con
//  icono— y si cada una escribiera su propio texto, la misma situacion
//  acabaria llamandose distinto segun la pantalla.
//
//  Accesibilidad: el punto de color NO es la unica senal. El texto
//  ("Disponible", "Stock bajo", "Vencido") dice lo mismo, asi que quien no
//  distingue verde de rojo igual se entera. El punto va marcado como
//  decorativo para que el lector de pantalla no lo anuncie dos veces.
//
//  `Disponible` usa `correct`, el color extra que no existe en Material y que
//  vive en ColoresExtra (ui/theme/Theme.kt).
// ============================================================

enum class EstadoInventario(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    STOCK_BAJO("Stock bajo"),
    /** Todavia da tiempo: por eso no es rojo, sino el rosa de `tertiary`. */
    POR_CADUCAR("Por caducar"),
    CADUCADO("Caducado")
}

@Composable
fun ChipEstado(estado: EstadoInventario, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct

    val fondo = when (estado) {
        EstadoInventario.DISPONIBLE -> correcto.colorContainer
        EstadoInventario.POR_CADUCAR -> cs.tertiaryContainer
        EstadoInventario.STOCK_BAJO, EstadoInventario.CADUCADO -> cs.errorContainer
    }
    val letra = when (estado) {
        EstadoInventario.DISPONIBLE -> correcto.onColorContainer
        EstadoInventario.POR_CADUCAR -> cs.onTertiaryContainer
        EstadoInventario.STOCK_BAJO -> cs.onErrorContainer
        EstadoInventario.CADUCADO -> cs.error
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Radios.chip))
            .background(fondo)
            .padding(horizontal = Margenes.lg, vertical = Margenes.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        Box(
            Modifier
                .clearAndSetSemantics { }   // decorativo: el texto ya lo dice
                .size(8.dp)
                .clip(CircleShape)
                .background(letra)
        )
        Text(
            estado.etiqueta,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = letra
        )
    }
}
