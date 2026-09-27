package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  LISTA PEGADA  (Figma 48:1531 y 51:1608)
//
//  Una caja con los renglones pegados: la receta de un producto y los items de
//  una venta se dibujan igual, y son la misma idea — un desglose cerrado, que
//  ya no cambia.
//
//  La linea que separa un renglon del siguiente NO se traza: es el fondo de la
//  caja asomando por una separacion de 1. Menos que dibujar un divisor, y el
//  color siempre concuerda con el de la caja.
//
//  Las esquinas cuentan cuantos renglones hay: 12 por fuera y 4 por dentro,
//  asi el bloque se lee como uno solo y no como fichas sueltas.
// ============================================================

@Composable
fun ListaPegada(
    cuantos: Int,
    modifier: Modifier = Modifier,
    renglon: @Composable RowScope.(Int) -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        repeat(cuantos) { i ->
            val primero = i == 0
            val ultimo = i == cuantos - 1
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = if (primero) 12.dp else 4.dp,
                            topEnd = if (primero) 12.dp else 4.dp,
                            bottomStart = if (ultimo) 12.dp else 4.dp,
                            bottomEnd = if (ultimo) 12.dp else 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(Margenes.md)
                    .semantics(mergeDescendants = true) { },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                content = { renglon(i) }
            )
        }
    }
}
