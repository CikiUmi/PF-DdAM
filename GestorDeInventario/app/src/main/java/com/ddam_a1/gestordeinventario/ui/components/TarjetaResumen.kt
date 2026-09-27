package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

/**
 * Figma "Shortcut Materiales / Productos" (41:746, 41:750).
 *
 * Un atajo con un numero: cuantos hay y si algo anda mal. Fondo
 * `surfaceContainer`, radio 20, etiqueta en MAYUSCULAS.
 *
 * Distinta de `TarjetaMetrica` a proposito: aquella es dinero y lleva barrita
 * de color; esta es un conteo y lleva a una lista.
 */
@Composable
fun TarjetaResumen(
    etiqueta: String,
    valor: String,
    nota: String,
    modifier: Modifier = Modifier,
    /** Rojo cuando la nota es un problema ("3 con stock bajo"). */
    notaEsAlerta: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier
            .clip(RoundedCornerShape(Radios.accion))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(
                if (onClick != null) Modifier.clickable(role = Role.Button) { onClick() }
                else Modifier
            )
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            etiqueta.uppercase(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            valor,
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        // Igual que en TarjetaMetrica: el sobrante separa la cifra de su nota
        // en vez de acumularse al final.
        Spacer(Modifier.weight(1f))

        Text(
            nota,
            style = MaterialTheme.typography.bodyLarge,
            color = if (notaEsAlerta) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
