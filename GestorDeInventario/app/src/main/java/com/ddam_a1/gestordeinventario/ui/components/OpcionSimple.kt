package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun OpcionSimple(titulo: String, detalle: String, activo: Boolean, onClick: () -> Unit) {
    val borde = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (activo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .border(1.5.dp, borde, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            Modifier.size(22.dp).clip(CircleShape).border(2.dp, borde, CircleShape),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            if (activo) androidx.compose.foundation.layout.Box(
                Modifier.size(11.dp).clip(CircleShape).background(borde)
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.width(14.dp))
        Column {
            androidx.compose.material3.Text(titulo, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface)
            androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
            androidx.compose.material3.Text(detalle, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
