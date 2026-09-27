package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Campo de entrada / Tipo=Search" (36:61).
 *
 * Es el mismo campo que `CampoTexto` pero sin etiqueta arriba.
 *
 * Radio 16 y no 28: el componente suelto (36:61) lo dibuja redondisimo, pero
 * las pantallas de lista —Inventario, Catalogo, Nueva venta— lo ponen con la
 * misma esquina que los demas campos. Mandan las pantallas.
 */
@Composable
fun BarraBusqueda(texto: String, marcador: String, onCambio: (String) -> Unit) {
    val interacciones = remember { MutableInteractionSource() }
    val enfocado by interacciones.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Medidas.control)
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                if (enfocado) Medidas.bordeGrueso else Medidas.borde,
                if (enfocado) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(Radios.campo)
            )
            .padding(horizontal = Margenes.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Icon(
            Iconos.Buscar,
            contentDescription = null,   // el marcador ya dice que es una busqueda
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Medidas.iconoBusqueda)
        )
        Box(Modifier.weight(1f)) {
            if (texto.isEmpty()) {
                Text(
                    marcador,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BasicTextField(
                value = texto,
                onValueChange = onCambio,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                interactionSource = interacciones,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
