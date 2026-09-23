package com.ddam_a1.gestordeinventario.ui.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos

private data class Destino(val ruta: Ruta, val icono: ImageVector, val etiqueta: String)

private val destinos = listOf(
    Destino(Ruta.Inicio, Iconos.Inicio, "Inicio"),
    Destino(Ruta.Inventario, Iconos.Inventario, "Inventario"),
    Destino(Ruta.Catalogo, Iconos.Catalogo, "Catálogo"),
    Destino(Ruta.HistorialVentas, Iconos.Ventas, "Ventas")
)

@Composable
fun BarraInferior(actual: Ruta, onIr: (Ruta) -> Unit) {
    Column {
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
        Row(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinos.forEach { d ->
                val activo = actual == d.ruta
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                        .clickable { onIr(d.ruta) }.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.clip(RoundedCornerShape(14.dp))
                            .background(if (activo) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                    ) {
                        Icon(d.icono, d.etiqueta,
                            tint = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(d.etiqueta, style = MaterialTheme.typography.labelSmall,
                        color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
