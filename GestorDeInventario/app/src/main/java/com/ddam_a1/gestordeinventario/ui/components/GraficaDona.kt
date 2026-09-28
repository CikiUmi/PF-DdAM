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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
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
//  Tarjeta `surfaceContainer` radio 24 y relleno de 24 —no 16 como el resto,
//  el diseno lo pide asi—: a la izquierda el titulo, el total y la leyenda; a
//  la derecha la dona de 110.
//
//  EL TOTAL VA EN LA LEYENDA, NO DENTRO DEL ANILLO. Al centro solo cabe una
//  cifra corta ("$1.3k"): "$1,314.00" se sale del hueco y se encima con el
//  arco. La cifra exacta esta a un renglon de distancia, en "TOTAL: ...".
//
//  El porcentaje va en el TEXTO de la leyenda, no solo en el tamano del arco.
//  Una dona sin cifras obliga a estimar a ojo, y a quien no distingue los dos
//  colores no le dice nada.
//
//  CUANDO UNA PORCION ES NEGATIVA —el negocio perdio dinero— la dona deja de
//  ser un reparto: no existe el 171% de un pastel, ni un arco de -71 grados.
//  Ahi el anillo ensena solo lo que si es positivo y la leyenda cambia de
//  porcentajes a IMPORTES, con el negativo en rojo. Es la diferencia entre
//  ensenar un dato incomodo y ensenar un dato falso.
// ============================================================

@Composable
fun GraficaDona(
    titulo: String,
    porciones: List<PorcionDona>,
    /** El importe completo, para el renglon "TOTAL: ...". */
    totalTexto: String,
    /** El mismo importe, abreviado, para el centro del anillo. */
    totalCorto: String,
    formatearValor: (Double) -> String,
    modifier: Modifier = Modifier,
    /** 110 en telefono, 130 en tableta (Figma 84:4784). */
    diametro: Dp = 110.dp,
    /**
     * Agrega el importe a cada renglon de la leyenda: "Ganancia (63%) — $28,400".
     * Solo en pantallas anchas; en telefono no cabe y se corta.
     */
    conImportes: Boolean = false
) {
    val hayNegativos = porciones.any { it.valor < 0.0 }
    // El reparto se calcula SOLO sobre lo positivo: es lo unico que se puede
    // dibujar como arco.
    val sumaPositiva = porciones.filter { it.valor > 0.0 }.sumOf { it.valor }
        .coerceAtLeast(0.0001)

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
            .padding(Margenes.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Margenes.sm)
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.tituloMedio,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "TOTAL: " + totalTexto,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            porciones.forEach { porcion ->
                val negativa = porcion.valor < 0.0
                val porcentaje = (porcion.valor / sumaPositiva * 100).toInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
                ) {
                    Box(
                        Modifier
                            .clearAndSetSemantics { }
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (negativa) MaterialTheme.colorScheme.error
                                else porcion.color
                            )
                    )
                    Text(
                        when {
                            // Con una porcion negativa, NINGUNA lleva
                            // porcentaje: el de al lado seria un 100% que no
                            // significa nada.
                            hayNegativos -> porcion.etiqueta + ": " + formatearValor(porcion.valor)
                            conImportes ->
                                porcion.etiqueta + " (" + porcentaje + "%) — " +
                                    formatearValor(porcion.valor)
                            else -> porcion.etiqueta + " (" + porcentaje + "%)"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (negativa) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

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
                // Solo lo positivo: un barrido negativo dibuja el arco hacia
                // atras y pinta encima del anterior.
                porciones.filter { it.valor > 0.0 }.forEach { porcion ->
                    val barrido = (porcion.valor / sumaPositiva * 360.0).toFloat()
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
                    totalCorto,
                    style = MaterialTheme.typography.tituloMedio,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    "MXN",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
