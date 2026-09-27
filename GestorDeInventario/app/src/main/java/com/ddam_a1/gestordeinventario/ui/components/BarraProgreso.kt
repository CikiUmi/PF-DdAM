package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

/**
 * Figma "Progress Row" (41:877).
 *
 * Nombre y cifra arriba, barra de 8 debajo. Se usa en "Productos más
 * vendidos": la barra compara de un vistazo, la cifra da el dato exacto.
 *
 * La barra es decorativa para el lector de pantalla: la cifra de arriba ya
 * dice el número, y anunciar además "62 por ciento" sería repetir lo mismo
 * con menos precisión.
 */
@Composable
fun BarraProgreso(
    etiqueta: String,
    valor: String,
    /** De 0 a 1. Se recorta por si llega algo fuera de rango. */
    proporcion: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                etiqueta,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                valor,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Box(
            Modifier
                .clearAndSetSemantics { }
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    // `layout` en vez de fillMaxWidth(fraccion): con fraccion 0
                    // Compose sigue midiendo, y aqui hace falta que un producto
                    // sin ventas no pinte nada.
                    .layout { medible, restricciones ->
                        val ancho = (restricciones.maxWidth * proporcion.coerceIn(0f, 1f)).toInt()
                        val colocado = medible.measure(
                            restricciones.copy(minWidth = ancho, maxWidth = ancho)
                        )
                        layout(ancho, colocado.height) { colocado.place(0, 0) }
                    }
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}
