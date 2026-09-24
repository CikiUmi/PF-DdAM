package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BotonSecundario(
    texto: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    /** Igual que en BotonPrincipal, para que los dos se comporten parejo. */
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    val base = color ?: MaterialTheme.colorScheme.primary
    val c = if (habilitado) base else MaterialTheme.colorScheme.outline
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, c, RoundedCornerShape(16.dp))
            .clickable(enabled = habilitado) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = c)
    }
}
