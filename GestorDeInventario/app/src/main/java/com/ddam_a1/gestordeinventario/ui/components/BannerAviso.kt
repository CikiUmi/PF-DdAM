package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Alert Banner" (41:729).
 *
 * Fondo `errorContainer` con borde de `error`, radio 20, alto 54.
 *
 * Tres senales, no una: color, icono de alerta y el texto que dice el numero.
 * Quien no distingue el rojo lee "3 materiales con stock bajo" igual.
 */
@Composable
fun BannerAviso(
    texto: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(RoundedCornerShape(Radios.accion))
            .background(MaterialTheme.colorScheme.errorContainer)
            .border(
                Medidas.borde,
                MaterialTheme.colorScheme.error,
                RoundedCornerShape(Radios.accion)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Icon(
            Iconos.Alerta,
            contentDescription = null,   // el texto ya dice que es un aviso
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(Medidas.iconoBusqueda)
        )
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
        if (onClick != null) {
            Icon(
                Iconos.Siguiente,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Medidas.iconoChico)
            )
        }
    }
}
