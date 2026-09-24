package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun GraficaDona(
    porciones: List<Pair<Color, Float>>,
    textoCentro: String,
    etiquetaCentro: String,
    modifier: Modifier = Modifier
) {
    val total = porciones.sumOf { it.second.toDouble() }.toFloat().coerceAtLeast(0.0001f)
    Box(modifier.size(190.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val grosor = 26.dp.toPx()
            val radio = size.minDimension / 2 - grosor / 2
            val esquina = Offset(size.width / 2 - radio, size.height / 2 - radio)
            val medida = Size(radio * 2, radio * 2)
            var inicio = -90f
            porciones.forEach { (color, valor) ->
                val barrido = valor / total * 360f
                drawArc(
                    color = color,
                    startAngle = inicio + 1.2f,
                    sweepAngle = (barrido - 2.4f).coerceAtLeast(0f),
                    useCenter = false,
                    topLeft = esquina,
                    size = medida,
                    style = Stroke(width = grosor, cap = StrokeCap.Round)
                )
                inicio += barrido
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                etiquetaCentro,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                textoCentro,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
