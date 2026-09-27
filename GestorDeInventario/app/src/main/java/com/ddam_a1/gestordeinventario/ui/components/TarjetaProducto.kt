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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Tarjeta producto" (36:165).
 *
 * Alto 88, fondo blanco con sombra 2, miniatura de 64 en `primaryContainer`,
 * precio en Lora 20.
 *
 * Se diferencia de `FilaLista` a proposito: esta es para el catalogo, donde
 * el producto es lo que se mira; la otra es para listas densas de materiales.
 * Por eso la miniatura grande y el precio en la tipografia display.
 */
@Composable
fun TarjetaProducto(
    nombre: String,
    detalle: String,
    precio: String,
    modifier: Modifier = Modifier,
    alerta: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .shadow(2.dp, RoundedCornerShape(Radios.campo))
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = Margenes.lg, vertical = Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Box(
            Modifier
                .size(Medidas.miniatura)
                .clip(RoundedCornerShape(Radios.miniatura))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Iconos.Catalogo,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(Medidas.icono)
            )
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            Text(
                nombre,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                detalle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (alerta != null) {
                Text(
                    alerta,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 1
                )
            }
        }

        Text(
            precio,
            style = MaterialTheme.typography.titleLarge,   // Lora SemiBold 20
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
