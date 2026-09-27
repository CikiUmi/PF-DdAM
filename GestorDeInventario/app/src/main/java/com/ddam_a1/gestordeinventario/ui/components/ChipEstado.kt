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
//  Los tres estados del inventario. Es un enum y no tres composables porque
//  un material esta en UNO de los tres, y con enum el compilador obliga a
//  cubrirlos todos en cada `when`.
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
    VENCIDO("Vencido")
}

@Composable
fun ChipEstado(estado: EstadoInventario, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct

    val fondo = when (estado) {
        EstadoInventario.DISPONIBLE -> correcto.colorContainer
        EstadoInventario.STOCK_BAJO, EstadoInventario.VENCIDO -> cs.errorContainer
    }
    val letra = when (estado) {
        EstadoInventario.DISPONIBLE -> correcto.onColorContainer
        EstadoInventario.STOCK_BAJO -> cs.onErrorContainer
        EstadoInventario.VENCIDO -> cs.error
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
