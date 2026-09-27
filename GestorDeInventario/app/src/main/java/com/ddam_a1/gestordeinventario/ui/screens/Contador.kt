package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

/**
 * Menos, cuantos, mas (Figma 51:1409).
 *
 * Las tres piezas estan SIEMPRE, incluso en cero: antes el "-" y el numero
 * solo aparecian a partir de uno, y el renglon se reacomodaba con cada toque.
 * En cero el "-" se apaga, que dice lo mismo sin mover nada.
 */
@Composable
fun Contador(n: Int, onMenos: () -> Unit, onMas: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        CajaIcono(Iconos.Quitar, "Quitar uno", onMenos, habilitado = n > 0)
        Text(
            n.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color =
                if (n > 0) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 24.dp)
        )
        CajaIcono(Iconos.Agregar, "Agregar uno", onMas)
    }
}
