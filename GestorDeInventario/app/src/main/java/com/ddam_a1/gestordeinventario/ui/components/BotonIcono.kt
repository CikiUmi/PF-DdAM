package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas

@Composable
fun BotonIcono(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    tinte: Color? = null,
    conPunto: Boolean = false,
    /** Figma 41:722: en las cabeceras el icono va sobre un circulo gris. */
    conFondo: Boolean = false
) {
    Box(
        modifier = Modifier
            // 48 y no 44: es el minimo tocable de Material. El circulo pintado
            // mide 40 (lo que dibuja el Figma), el area tocable es mayor.
            .size(Medidas.areaToque)
            .clip(CircleShape)
            .clickable { onClick() }
            .then(
                if (conFondo) Modifier
                    .padding(Margenes.xs)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono,
            contentDescription = descripcion,
            tint = tinte ?: MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Medidas.icono)
        )
        if (conPunto) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}
