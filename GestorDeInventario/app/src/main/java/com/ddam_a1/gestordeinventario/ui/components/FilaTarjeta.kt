package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  FILA CON ICONO  (Figma 71:6579, 71:7242)
//
//  Tarjeta blanca con sombra: cuadro de icono a la izquierda, titulo y
//  subtitulo en medio, y lo que haga falta a la derecha.
//
//  Es la misma pieza en las dos pantallas de administracion: en Avisos lleva
//  una X para descartar, en Configuracion un chevron que navega. Por eso lo
//  de la derecha es un hueco y no un parametro fijo.
//
//  Distinta de FilaAjuste (icono suelto sin cuadro, sin sombra, para listas
//  densas) y de FilaLista (sin icono). Esta es para pocas filas que se miran.
// ============================================================

@Composable
fun FilaTarjeta(
    icono: ImageVector,
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    fondoIcono: Color = MaterialTheme.colorScheme.surfaceContainer,
    tintaIcono: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    colorTitulo: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    accion: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .shadow(2.dp, RoundedCornerShape(Radios.campo))
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .then(
                if (onClick != null)
                    Modifier.clickable(role = Role.Button) { onClick() }
                else Modifier
            )
            .padding(horizontal = Margenes.md, vertical = Margenes.sm)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Box(
            Modifier
                .size(Medidas.avatar)
                .clip(RoundedCornerShape(Radios.fila))
                .background(fondoIcono),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icono,
                contentDescription = null,   // el titulo de al lado lo nombra
                tint = tintaIcono,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                titulo,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = colorTitulo
            )
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        accion()
    }
}
