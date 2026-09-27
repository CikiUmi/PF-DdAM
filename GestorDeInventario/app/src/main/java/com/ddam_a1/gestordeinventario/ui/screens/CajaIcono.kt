package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * El cuadrito blanco de + y - del contador (Figma 51:1410).
 *
 * Apagado se dibuja al 50% y deja de responder, en vez de desaparecer: si el
 * "-" se quitara al llegar a cero, los botones se moverian de sitio cada vez
 * que el contador pasa por ahi, y acabarias tocando el que no era.
 */
@Composable
fun CajaIcono(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    habilitado: Boolean = true
) {
    Box(
        Modifier
            .size(38.dp)
            .alpha(if (habilitado) 1f else 0.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable(enabled = habilitado, role = Role.Button, onClickLabel = descripcion) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono,
            contentDescription = descripcion,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
        )
    }
}
