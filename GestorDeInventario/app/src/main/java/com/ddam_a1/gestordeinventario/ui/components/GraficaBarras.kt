package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  GRAFICA DE BARRAS  (Figma 41:823)
//
//  Tarjeta `surfaceContainer` radio 24, titulo Lora 20, cuerpo de 120 de
//  alto, barras de 20 de ancho con las esquinas de ARRIBA redondeadas.
//
//  POR QUE NO ES UN Canvas:
//  la version anterior dibujaba todo a mano y no podia poner texto encima de
//  las barras. Con Column/Row cada barra es un elemento de verdad, el lector
//  de pantalla lo puede leer, y el valor encima es un Text normal.
//
//  LA CIFRA ENCIMA:
//  el diseno la muestra solo en dos barras de siete, las mas altas. La regla
//  es "a partir del 80% del maximo": asi salen las que importan sin tener que
//  elegirlas a mano, y con datos distintos la grafica sigue teniendo sentido.
// ============================================================

@Composable
fun GraficaBarras(
    titulo: String,
    datos: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    formatearValor: (Double) -> String = { it.toInt().toString() }
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(0.0001)

    Column(
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
        verticalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (datos.isEmpty()) {
            Text(
                "Sin datos en este período",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        Row(
            Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            datos.forEach { (etiqueta, valor) ->
                val proporcion = (valor / maximo).toFloat().coerceIn(0f, 1f)
                val destacada = valor >= maximo * 0.8 && valor > 0.0

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Margenes.xs, Alignment.Bottom),
                    // Una barra sin texto no dice nada: aqui se junta lo que
                    // significa en una sola frase.
                    modifier = Modifier.semantics {
                        contentDescription = etiqueta + ": " + formatearValor(valor)
                    }
                ) {
                    if (destacada) {
                        Text(
                            formatearValor(valor),
                            style = MaterialTheme.typography.bodyLarge
                                .copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Box(
                        Modifier
                            .width(20.dp)
                            // 72 y no 120: el alto del cuerpo se reparte entre
                            // la barra, su etiqueta y la cifra de encima.
                            .height((72 * proporcion).dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 6.dp, topEnd = 6.dp,
                                    bottomStart = 0.dp, bottomEnd = 0.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                    Text(
                        etiqueta,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
