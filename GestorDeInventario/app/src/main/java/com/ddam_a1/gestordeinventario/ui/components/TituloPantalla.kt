package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Figma "Título de pantalla" (14:8727).
 *
 * El titulo que va DENTRO del contenido, debajo de la App Bar: la barra dice
 * en que pantalla estas, este dice que vas a hacer aqui.
 *
 * `heading()` para que el lector de pantalla lo anuncie como encabezado y se
 * pueda saltar de seccion en seccion.
 */
@Composable
fun TituloPantalla(
    titulo: String,
    subtitulo: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,   // Lora SemiBold 20
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics { heading() }
        )
        if (subtitulo != null) {
            Text(
                subtitulo,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
