package com.ddam_a1.gestordeinventario.ui.componentes

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.navegacion.RUTA_CATALOGO
import com.ddam_a1.gestordeinventario.ui.navegacion.RUTA_HISTORIAL_VENTAS
import com.ddam_a1.gestordeinventario.ui.navegacion.RUTA_INICIO
import com.ddam_a1.gestordeinventario.ui.navegacion.RUTA_INVENTARIO

/**
 * Los cuatro destinos de la barra de abajo.
 *
 * Es un `enum` y no una lista de rutas sueltas porque la pantalla necesita
 * decir CUAL de los cuatro es ella, y con un enum eso es un valor que el
 * compilador revisa. Cada entrada carga su propia ruta, su icono y su texto.
 */
enum class DestinoBarra(val ruta: String, val icono: ImageVector, val etiqueta: String) {
    INICIO(RUTA_INICIO, Iconos.Inicio, "Inicio"),
    INVENTARIO(RUTA_INVENTARIO, Iconos.Inventario, "Inventario"),
    CATALOGO(RUTA_CATALOGO, Iconos.Catalogo, "Catalogo"),
    VENTAS(RUTA_HISTORIAL_VENTAS, Iconos.Ventas, "Ventas")
}

/**
 * La barra de abajo.
 *
 * No conoce al NavController: recibe cual esta activo y un callback. Quien sabe
 * navegar es el NavHost.
 */
@Composable
fun BarraInferior(destinoActual: DestinoBarra, onDestino: (DestinoBarra) -> Unit) {
    Column {
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
        Row(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DestinoBarra.entries.forEach { d ->
                val activo = destinoActual == d
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                        .clickable { onDestino(d) }.padding(vertical = 6.dp),
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
