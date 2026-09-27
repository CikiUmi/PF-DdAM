package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Medidas

/**
 * Figma "Toggle" (36:80 / 36:82) y el interruptor de las filas 36:216 / 36:233.
 *
 * No se usa el `Switch` de Material porque trae su propia paleta y medidas
 * (52x32 y colores fijos del tema); el diseno pide 44x24 en `tertiary`.
 *
 * `toggleable(role = Switch)` le da el area tocable, el estado hablado
 * ("activado / desactivado") y el soporte de teclado sin escribir nada mas.
 * El `contentDescription` lo pone quien lo usa: el interruptor solo no sabe
 * de que es.
 */
@Composable
fun Interruptor(
    activo: Boolean,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onCambio: (Boolean) -> Unit
) {
    val ancho = Medidas.interruptorAncho
    val alto = Medidas.interruptorAlto
    val bolita = 18.dp
    val desplazamiento by animateDpAsState(
        targetValue = if (activo) ancho - bolita - 3.dp else 3.dp,
        label = "bolita"
    )

    Box(
        modifier
            // El area tocable son 48dp aunque se dibujen 24: el dedo necesita
            // mas de lo que el ojo ve.
            .size(Medidas.areaToque)
            .toggleable(
                value = activo,
                enabled = habilitado,
                role = Role.Switch,
                onValueChange = onCambio
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(ancho)
                .height(alto)
                .clip(CircleShape)
                .background(
                    if (activo) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.outlineVariant
                )
        ) {
            Box(
                Modifier
                    .offset(x = desplazamiento)
                    .align(Alignment.CenterStart)
                    .size(bolita)
                    .clip(CircleShape)
                    .background(
                        if (activo) MaterialTheme.colorScheme.onTertiary
                        else MaterialTheme.colorScheme.surfaceContainerLowest
                    )
            )
        }
    }
}
