package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

/**
 * Figma "Action Registrar Venta / Entrada Inventario" (41:735, 41:740).
 *
 * La tarjeta grande de color que abre lo que mas se hace en el dia. Fondo
 * `tertiary`, radio 20, cuadro blanco de 40 con el icono, y la etiqueta en
 * Lora 20 blanca.
 *
 * `role = Button`: aunque se dibuje como tarjeta, para el lector de pantalla
 * es un boton, no un bloque de texto que da la casualidad de responder.
 */
@Composable
fun AccionRapida(
    icono: ImageVector,
    etiqueta: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier
            .shadow(4.dp, RoundedCornerShape(Radios.accion))
            .clip(RoundedCornerShape(Radios.accion))
            .background(MaterialTheme.colorScheme.tertiary)
            .clickable(role = Role.Button) { onClick() }
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Radios.fila))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icono,
                contentDescription = null,   // la etiqueta de abajo lo nombra
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(Medidas.icono)
            )
        }

        // Si la tarjeta se estiro para igualar a su vecina, el hueco se va
        // entre el icono y la etiqueta: los iconos quedan arriba alineados
        // entre si y las etiquetas abajo, aunque una ocupe dos renglones.
        Spacer(Modifier.weight(1f))

        Text(
            etiqueta,
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
