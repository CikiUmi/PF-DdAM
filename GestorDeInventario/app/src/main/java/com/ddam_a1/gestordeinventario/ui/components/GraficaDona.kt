package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

/** Una porción de la dona, con el nombre que va en la leyenda. */
data class PorcionDona(val etiqueta: String, val valor: Double, val color: Color)

// ============================================================
//  DONA CON LEYENDA  (Figma 41:849)
//
//  Tarjeta `surfaceContainer` radio 24: dona de 110 a la izquierda con el
//  total al centro, y a la derecha el titulo y la leyenda.
//
//  El porcentaje va en el TEXTO de la leyenda, no solo en el tamaño del arco.
//  Una dona sin cifras obliga a estimar a ojo, y a quien no distingue los dos
//  colores no le dice nada.
// ============================================================

@Composable
fun GraficaDona(
    titulo: String,
    porciones: List<PorcionDona>,
    totalTexto: String,
    modifier: Modifier = Modifier,
    /** 110 en telefono, 130 en tableta (Figma 84:4784). */
    diametro: Dp = 110.dp,
    /**
     * Si se pasa, la leyenda agrega el importe: "Ganancia (63%) — $28,400".
     * Solo en pantallas anchas; en telefono no cabe y se corta.
     */
    formatearValor: ((Double) -> String)? = null
) {
    val total = porciones.sumOf { it.valor }.coerceAtLeast(0.0001)

    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.dialogo))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            // El borde separa la tarjeta del fondo: `surfaceContainer` y
            // `surface` son casi el mismo gris y sin linea se confunden.
            .border(
                Medidas.borde,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(Radios.dialogo)
            )
            .padding(Margenes.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Box(Modifier.size(diametro), contentAlignment = Alignment.Center) {
            val vacia = MaterialTheme.colorScheme.surfaceContainerHigh
            Canvas(
                Modifier.fillMaxSize().clearAndSetSemantics { }
            ) {
                val grosor = 18.dp.toPx()
                val esquina = Offset(grosor / 2, grosor / 2)
                val medida = Size(size.width - grosor, size.height - grosor)

                // El aro de fondo primero: si no hay datos, se ve la dona
                // vacia en vez de un hueco.
                drawArc(
                    color = vacia, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    topLeft = esquina, size = medida,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(grosor)
                )
                var inicio = -90f
                porciones.forEach { porcion ->
                    val barrido = (porcion.valor / total * 360.0).toFloat()
                    drawArc(
                        color = porcion.color, startAngle = inicio, sweepAngle = barrido,
                        useCenter = false, topLeft = esquina, size = medida,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(grosor)
                    )
                    inicio += barrido
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    totalTexto,
                    style = MaterialTheme.typography.tituloMedio,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "TOTAL",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Margenes.sm)
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.tituloMedio,
                color = MaterialTheme.colorScheme.onSurface
            )
            porciones.forEach { porcion ->
                val porcentaje = (porcion.valor / total * 100).toInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
                ) {
                    Box(
                        Modifier
                            .clearAndSetSemantics { }
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(porcion.color)
                    )
                    Text(
                        porcion.etiqueta + " (" + porcentaje + "%)" +
                            (formatearValor?.let { " — " + it(porcion.valor) } ?: ""),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
