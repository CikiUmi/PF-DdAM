package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

// ============================================================
//  TARJETA METRICA  (Figma 36:160)
//
//  Cambio de fondo: antes cada tarjeta se tenia de su color con un degradado.
//  El diseno las pone todas BLANCAS con una barrita de color arriba.
//
//  Y no es solo estetica: con el fondo de color, el numero iba sobre un tono
//  distinto en cada tarjeta y el contraste dependia de cual tocara. Con fondo
//  blanco el numero siempre es `onSurface` y el contraste esta garantizado;
//  el color pasa a la barrita, que no tiene que ser legible.
//
//  Del Figma: radio 16, sombra 2dp, padding 16, separacion 4,
//  barrita de 48x4, valor en Lora 28.
// ============================================================

@Composable
fun TarjetaMetrica(
    etiqueta: String,
    valor: String,
    nota: String? = null,
    colorAcento: Color = MaterialTheme.colorScheme.tertiary,
    modifier: Modifier = Modifier,
    /** Por omision el verde de `correct`: la nota suele ser una buena noticia. */
    colorNota: Color = MaterialTheme.coloresExtra.correct.color,
    /**
     * Cifra en 20 en vez de 28.
     *
     * Tres de estas caben de lado en una tableta, pero no con la cifra de 28:
     * un importe de negocio de verdad no entra. Es la MISMA tarjeta, solo con
     * el numero a la medida del hueco.
     */
    compacta: Boolean = false,
    /**
     * Renglones que puede ocupar la cifra. Uno por omision, que es lo que
     * quiere una fila de metricas; dos cuando el importe puede ser largo y
     * cortarlo cambiaria lo que dice.
     */
    maxLineasCifra: Int = 1,
    /**
     * Color de la cifra. Por omision el de siempre; se cambia cuando el
     * numero dice lo contrario de su etiqueta, como una "Ganancia" negativa.
     */
    colorValor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(Radios.campo))
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(Margenes.lg),
        verticalArrangement = Arrangement.spacedBy(Margenes.xs)
    ) {
        Box(
            Modifier
                .clearAndSetSemantics { }    // decorativo
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colorAcento)
        )
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            valor,
            style =
                if (compacta) MaterialTheme.typography.tituloMedio        // Lora 20
                else MaterialTheme.typography.headlineMedium,             // Lora 28
            color = colorValor,
            maxLines = maxLineasCifra,
            overflow = TextOverflow.Ellipsis
        )
        // Si la tarjeta se estiro para igualar a su vecina, el hueco se va
        // entre la cifra y su nota: la nota queda abajo y las cifras de todas
        // las tarjetas del renglon siguen alineadas entre si.
        Spacer(Modifier.weight(1f))

        if (nota != null) {
            Text(
                nota,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colorNota,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
