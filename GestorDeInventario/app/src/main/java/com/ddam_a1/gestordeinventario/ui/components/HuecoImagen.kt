package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Medidas

// ============================================================
//  HUECO DE IMAGEN  (Figma 48:1414)
//
//  El rectangulo azul donde algun dia ira la foto del producto.
//
//  Existe como componente propio y no como un Box suelto dentro de la tarjeta
//  para que el dia que haya fotos se cambie en UN sitio: aqui dentro, por un
//  AsyncImage o lo que se use, y todas las pantallas se enteran solas.
//
//  El color es `primaryContainer`, el mismo del Figma. No es un gris de
//  "cargando": es el hueco a proposito, y el icono de catalogo dice que ahi
//  va un producto.
//
//  Marcado como decorativo: no aporta nada que el nombre de al lado no diga.
// ============================================================

@Composable
fun HuecoImagen(modifier: Modifier = Modifier, alto: Dp = 100.dp) {
    Box(
        modifier
            .fillMaxWidth()
            .height(alto)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Iconos.Catalogo,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.35f),
            modifier = Modifier.size(Medidas.icono)
        )
    }
}
