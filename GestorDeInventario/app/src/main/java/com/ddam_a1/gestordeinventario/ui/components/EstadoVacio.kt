package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

/**
 * Figma "Estado vacío" (36:85).
 *
 * Separacion 16, 32 de lado y 40 arriba/abajo, circulo de 80, titulo en Lora
 * 22 y el texto centrado.
 *
 * El circulo del diseno es una elipse de un solo color: aqui se dibuja con un
 * Box y dentro va el icono de `Iconos`, que es el sistema de iconos de este
 * proyecto. No se descarga como imagen porque no es un dibujo, es una forma.
 *
 * `textoAccion` es opcional para no romper las cinco pantallas que ya llaman
 * a esto sin boton.
 */
@Composable
fun EstadoVacio(
    mensaje: String,
    sugerencia: String? = null,
    textoAccion: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Margenes.xxl, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Box(
            Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Iconos.Caja,
                contentDescription = null,   // el mensaje de abajo ya lo explica
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            mensaje,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (sugerencia != null) {
            Text(
                sugerencia,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (textoAccion != null && onAccion != null) {
            Box(Modifier.widthIn(max = 240.dp)) {
                BotonPrincipal(textoAccion, onClick = onAccion)
            }
        }
    }
}
