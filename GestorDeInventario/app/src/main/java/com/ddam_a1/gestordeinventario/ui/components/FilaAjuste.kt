package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  FILA DE CONFIGURACION  (Figma 36:213 / 36:217 / 36:221)
//
//  Tres tipos en el diseno —Toggle, Chevron y Valor— que son la misma fila
//  cambiando lo que va a la derecha. Aqui son tres funciones con el mismo
//  cuerpo compartido, y no un parametro `tipo`, porque cada una necesita
//  cosas distintas: la de toggle necesita `activo` y `onCambio`, la de
//  chevron un `onClick`, y la de valor ninguna de las dos.
//
//  Asi el compilador impide pedir una fila con toggle sin decir que hace al
//  encenderlo, que con un `tipo` seria posible.
//
//  Del Figma: alto 56, borde 1 de outlineVariant, 16 de lado, separacion 12.
// ============================================================

@Composable
private fun FilaBase(
    icono: ImageVector,
    titulo: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    derecha: @Composable () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = Medidas.fila)
            .border(
                Medidas.borde,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(Radios.fila)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = Margenes.lg, vertical = Margenes.sm)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Icon(
            icono,
            contentDescription = null,   // el titulo de al lado ya lo nombra
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Medidas.icono)
        )
        Text(
            titulo,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        derecha()
    }
}

/** Tipo=Toggle. Una opcion que se enciende y se apaga aqui mismo. */
@Composable
fun FilaAjusteInterruptor(
    icono: ImageVector,
    titulo: String,
    activo: Boolean,
    modifier: Modifier = Modifier,
    onCambio: (Boolean) -> Unit
) = FilaBase(icono, titulo, modifier) {
    Interruptor(activo, onCambio = onCambio)
}

/** Tipo=Chevron. Lleva a otra pantalla. */
@Composable
fun FilaAjusteNavega(
    icono: ImageVector,
    titulo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = FilaBase(icono, titulo, modifier, onClick = onClick) {
    Icon(
        Iconos.Siguiente,
        contentDescription = null,   // el `clickable` de la fila ya dice que se toca
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(Medidas.iconoChico)
    )
}

/** Tipo=Valor. Solo informa: muestra como esta algo, sin poder cambiarlo. */
@Composable
fun FilaAjusteValor(
    icono: ImageVector,
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) = FilaBase(icono, titulo, modifier) {
    Text(
        valor,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
