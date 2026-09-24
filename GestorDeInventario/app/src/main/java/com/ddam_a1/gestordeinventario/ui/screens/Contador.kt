package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.*

@Composable
fun Contador(n: Int, onMenos: () -> Unit, onMas: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (n > 0) {
            CajaIcono(Iconos.Quitar, "Quitar", onMenos)
            Text("$n", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.widthIn(min = 24.dp))
        }
        CajaIcono(Iconos.Agregar, "Agregar", onMas)
    }
}
