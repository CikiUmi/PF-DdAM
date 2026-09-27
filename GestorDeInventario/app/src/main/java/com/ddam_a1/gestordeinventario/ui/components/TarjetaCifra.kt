package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

/**
 * Figma "Metric Card" de Rendimiento (41:865, 41:868, 41:871).
 *
 * Tres de estas caben en una fila. Distinta de `TarjetaMetrica`: aquella es
 * blanca con barrita y va sola; esta lleva el color EN el fondo porque su
 * trabajo es que ganancia, costo y pérdidas se distingan entre sí de un golpe.
 *
 * Los dos colores llegan de fuera: quien la usa sabe si una cifra es buena
 * noticia o mala, el componente no.
 */
@Composable
fun TarjetaCifra(
    etiqueta: String,
    valor: String,
    fondo: Color,
    contenido: Color,
    modifier: Modifier = Modifier,
    colorValor: Color = contenido,
    /**
     * Renglones que puede ocupar la cifra.
     *
     * Por omision uno, que es lo que quiere Rendimiento: tres cifras cortas
     * alineadas. El detalle de producto pide dos para la ganancia, porque
     * "$12.50 (50%)" no cabe en un tercio de un telefono y recortarlo con
     * puntos suspensivos escondería justo el porcentaje.
     */
    maxLineas: Int = 1
) {
    Column(
        modifier
            .clip(RoundedCornerShape(Radios.campo))
            .background(fondo)
            .padding(Margenes.md),
        verticalArrangement = Arrangement.spacedBy(Margenes.xs)
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = contenido,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            valor,
            style = MaterialTheme.typography.tituloMedio,
            color = colorValor,
            maxLines = maxLineas,
            overflow = TextOverflow.Ellipsis
        )
    }
}
