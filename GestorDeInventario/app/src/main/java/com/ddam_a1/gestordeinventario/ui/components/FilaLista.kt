package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  FILA DE LISTA  (Figma 45:515 "Fila de material")
//
//  Cambio de forma: antes era una fila con una raya debajo; el diseno la
//  convierte en TARJETA (fondo surfaceContainerHigh, radio 16, alto 75).
//  Por eso desaparecio la linea divisoria: la separacion la da el hueco de
//  12 entre tarjetas que ya pone `Marco`.
//
//  `mergeDescendants`: el lector de pantalla lee la tarjeta como UNA cosa
//  ("Harina, 2.50 por kg, stock bajo, 4 kg") en vez de cuatro textos sueltos.
// ============================================================

@Composable
fun FilaLista(
    titulo: String,
    subtitulo: String? = null,
    valor: String? = null,
    notaValor: String? = null,
    /** Punto de color a la izquierda. Decorativo: nunca es la unica senal. */
    colorPunto: Color? = null,
    /** Aviso corto en rojo junto al subtitulo, p. ej. "Stock bajo". */
    alerta: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 75.dp)
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        if (colorPunto != null) {
            Box(
                Modifier
                    .clearAndSetSemantics { }
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colorPunto)
            )
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                titulo,
                style = MaterialTheme.typography.tituloMedio,   // Lora SemiBold 20
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitulo != null || alerta != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(Margenes.sm)) {
                    if (subtitulo != null) {
                        Text(
                            subtitulo,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (alerta != null) {
                        // Texto, no solo color: quien no distingue el rojo
                        // igual lee "Stock bajo".
                        Text(
                            alerta,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        if (valor != null) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    valor,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (notaValor != null) {
                    Text(
                        notaValor,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
