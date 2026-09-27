package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

// ============================================================
//  FILA DE CHIPS
//
//  Una fila de chips que, cuando no caben, SE DESPLAZA de lado en vez de
//  apretarlos.
//
//  Sin esto, tres chips como "Administrador · Encargado · Empleado" en un
//  telefono de 412 no entran: Compose reparte el hueco que queda y el ultimo
//  acaba partiendo su texto en dos renglones ("Emplea / do"). El chip se ve
//  roto y la fila deja de estar alineada.
//
//  Desplazar es mejor que envolver en dos renglones: los chips son un solo
//  grupo de opciones y en dos filas se leen como dos grupos distintos.
// ============================================================

@Composable
fun FilaChips(
    modifier: Modifier = Modifier,
    contenido: @Composable RowScope.() -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.sm),
        content = contenido
    )
}
