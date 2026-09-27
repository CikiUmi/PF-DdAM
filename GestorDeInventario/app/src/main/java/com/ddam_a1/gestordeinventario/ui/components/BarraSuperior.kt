package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas

// ============================================================
//  APP BAR  (Figma 36:159)
//
//  Las tres variantes del diseno —Navegacion, Modal y Dashboard— son la misma
//  barra: alto 56, 16 de lado, 12 de separacion. Lo que cambia es que haya
//  flecha, que haya subtitulo y que haya una accion a la derecha, y eso ya
//  son los tres parametros que tenia.
//
//  El titulo va en Lora (la familia display); el subtitulo en Nunito 16.
// ============================================================

@Composable
fun BarraSuperior(
    titulo: String,
    subtitulo: String? = null,
    onAtras: (() -> Unit)? = null,
    acciones: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Medidas.barraSuperior)
            .padding(horizontal = Margenes.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onAtras != null) {
            BotonIcono(Iconos.Atras, "Atrás", onAtras)
            Spacer(Modifier.width(Margenes.md))
        }
        Column(Modifier.weight(1f)) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        acciones()
    }
}
