package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  SELECTOR DE FORMATO  (Figma 36:234 / 36:239)
//
//  Una opcion de una lista donde solo se elige UNA. Se usa en "Elegir modo de
//  uso" y en "Exportar".
//
//  Sin borde, como el diseno: la elegida se distingue por el relleno
//  (primaryContainer contra surfaceContainer) y por el circulo lleno.
//
//  `selectable(role = Role.RadioButton)` en vez de `clickable`: asi el lector
//  de pantalla dice "seleccionado / no seleccionado" y el teclado y el
//  control por voz la tratan como lo que es, un radio. Con `clickable` seria
//  un boton mas y no habria forma de saber cual esta elegida sin ver el color.
// ============================================================

@Composable
fun OpcionSimple(titulo: String, detalle: String, activo: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(Radios.campo))
            .background(
                if (activo) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainer
            )
            .selectable(selected = activo, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Margenes.lg, vertical = Margenes.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        val colorCirculo = if (activo) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.outlineVariant

        Box(
            Modifier
                .size(Medidas.icono)
                .clip(CircleShape)
                .then(
                    if (activo) Modifier.background(colorCirculo)
                    else Modifier.border(Medidas.bordeGrueso, colorCirculo, CircleShape)
                )
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                titulo,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                detalle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
