package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  NAVEGACION LATERAL  (Figma 45:385)
//
//  La misma navegacion que la barra de abajo, pero de pie. En tableta una
//  barra inferior queda lejisimos del pulgar y desperdicia el ancho.
//
//  Comparte el enum `DestinoBarra` con `BarraInferior` a proposito: son la
//  MISMA navegacion en dos formas. Si fueran dos listas separadas, agregar un
//  destino obligaria a acordarse de los dos sitios.
//
//  Del Figma: ancho 256, fondo surfaceContainerLowest, 16 de lado y 24
//  arriba/abajo, separacion 4, cabecera con el cuadro de 32 y "Gestor" en
//  Lora 22, y el activo en primaryContainer con radio 12.
// ============================================================

@Composable
fun PanelLateral(
    destinoActual: DestinoBarra,
    onDestino: (DestinoBarra) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .width(Medidas.panelLateral)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = Margenes.lg, vertical = Margenes.xl),
        verticalArrangement = Arrangement.spacedBy(Margenes.xs)
    ) {
        // Cabecera
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Margenes.md, vertical = Margenes.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            LogoApp(32.dp)
            Text(
                "Gestor",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(Margenes.lg))

        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            DestinoBarra.entries.forEach { d ->
                val activo = destinoActual == d
                val color = if (activo) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.onSurfaceVariant

                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = Medidas.areaToque)
                        .clip(RoundedCornerShape(Radios.fila))
                        .background(
                            if (activo) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .selectable(selected = activo, role = Role.Tab) { onDestino(d) }
                        .padding(Margenes.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Margenes.md)
                ) {
                    Icon(
                        d.icono,
                        contentDescription = null,   // la etiqueta de al lado lo nombra
                        tint = color,
                        modifier = Modifier.size(Medidas.icono)
                    )
                    Text(
                        d.etiqueta,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = color
                    )
                }
            }
        }
    }
}
