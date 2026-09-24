package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Insignia(texto: String, color: Color, fondo: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(fondo)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
