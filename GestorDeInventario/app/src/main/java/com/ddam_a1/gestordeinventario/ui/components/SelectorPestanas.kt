package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Selector de pestañas" (36:200 Segmento / 36:205 Temporal).
 *
 * Las dos variantes del diseno son la misma cosa con dos o tres opciones, asi
 * que aqui es una lista: sirve para Usuarios/Permisos y para Dia/Semana/Mes
 * sin escribir dos componentes.
 *
 * Del Figma: alto 48, fondo surfaceContainerHigh, radio 24, 4 de relleno, y la
 * elegida en primaryContainer con radio 20 y texto en negrita.
 *
 * `selectableGroup` + `role = Tab`: el lector de pantalla lo anuncia como
 * pestanas y dice cual esta activa. Sin eso, la unica pista seria el color de
 * fondo.
 */
@Composable
fun SelectorPestanas(
    opciones: List<String>,
    indiceActivo: Int,
    modifier: Modifier = Modifier,
    onElegir: (Int) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(Medidas.control)
            .clip(RoundedCornerShape(Radios.pestanas))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(Margenes.xs)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        opciones.forEachIndexed { i, texto ->
            val activa = i == indiceActivo
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(Radios.pestanaActiva))
                    .background(
                        if (activa) MaterialTheme.colorScheme.primaryContainer
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .selectable(selected = activa, role = Role.Tab) { onElegir(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    texto,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (activa) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
