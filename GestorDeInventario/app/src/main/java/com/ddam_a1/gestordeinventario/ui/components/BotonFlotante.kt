package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Medidas

/**
 * Figma "FAB" (36:42). 56 de lado, circulo, `tertiary`, sombra 4/8 al 15%.
 *
 * `descripcion` es obligatoria y no tiene valor por omision: un boton que solo
 * muestra un dibujo no dice nada a quien usa lector de pantalla, y dejarlo
 * opcional es la forma mas facil de olvidarlo.
 */
@Composable
fun BotonFlotante(
    icono: ImageVector,
    descripcion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .size(Medidas.fab)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiary)
            .clickable(role = Role.Button, onClickLabel = descripcion) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono,
            contentDescription = descripcion,
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.size(Medidas.icono)
        )
    }
}
