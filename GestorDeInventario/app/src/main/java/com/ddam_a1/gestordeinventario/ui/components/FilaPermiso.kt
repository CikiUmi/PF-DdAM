package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Fila de permiso" (36:231).
 *
 * Alto 56, fondo surfaceContainerHigh, radio 12, 24 a la izquierda y 16 a la
 * derecha — la sangria de la izquierda es mayor a proposito: los permisos van
 * colgando de un rol, y ese escalon lo hace ver.
 *
 * `soloLectura` para la pantalla de consulta: se ve como esta el permiso pero
 * no se puede tocar. El interruptor se deshabilita en vez de esconderse,
 * porque quitarlo dejaria la fila sin decir si el permiso esta dado o no.
 */
@Composable
fun FilaPermiso(
    texto: String,
    activo: Boolean,
    modifier: Modifier = Modifier,
    soloLectura: Boolean = false,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = Medidas.fila)
            .clip(RoundedCornerShape(Radios.fila))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(start = Margenes.xl, end = Margenes.lg, top = Margenes.md, bottom = Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Interruptor(activo, habilitado = !soloLectura, onCambio = onCambio)
    }
}
