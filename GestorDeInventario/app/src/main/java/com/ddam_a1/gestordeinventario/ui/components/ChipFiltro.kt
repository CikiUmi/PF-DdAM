package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Chip / Tipo=Filtro" (36:66, 36:68).
 *
 * Sin borde: el diseno distingue seleccionado y no seleccionado solo por el
 * relleno (primaryContainer contra surfaceContainerHigh). El borde que habia
 * antes competia con el fondo y ensuciaba la fila de filtros.
 */
@Composable
fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    val fondo = if (activo) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceContainerHigh
    val letra = if (activo) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radios.chip))
            .background(fondo)
            .clickable { onClick() }
            .padding(horizontal = Margenes.lg, vertical = Margenes.sm)
    ) {
        Text(
            texto,
            // Un chip con el texto partido en dos renglones ("Emplea / do")
            // se ve roto. Antes que eso, que la fila se desplace.
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = letra
        )
    }
}
