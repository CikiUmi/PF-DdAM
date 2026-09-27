package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.navigation.RUTA_CATALOGO
import com.ddam_a1.gestordeinventario.ui.navigation.RUTA_HISTORIAL_VENTAS
import com.ddam_a1.gestordeinventario.ui.navigation.RUTA_INICIO
import com.ddam_a1.gestordeinventario.ui.navigation.RUTA_INVENTARIO

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
 * La barra de abajo  (Figma 45:223).
 *
 * No conoce al NavController: recibe cual esta activo y un callback. Quien sabe
 * navegar es el NavHost.
 *
 * Del Figma: alto 64, fondo `surface`, 12 arriba y 8 abajo, los cuatro
 * destinos repartidos por igual, icono 24 y etiqueta de 12.
 *
 * El activo se marca por DOS cosas a la vez, no solo por color: el texto pasa
 * a negrita ademas de ponerse en `tertiary`. Asi se distingue sin depender de
 * ver el color.
 */
@Composable
fun BarraInferior(destinoActual: DestinoBarra, onDestino: (DestinoBarra) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .height(Medidas.barraInferior)
            .padding(start = Margenes.lg, end = Margenes.lg, top = Margenes.md, bottom = Margenes.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DestinoBarra.entries.forEach { d ->
            val activo = destinoActual == d
            val color = if (activo) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.onSurfaceVariant

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // El area tocable es toda la columna, no solo el icono:
                    // asi cada destino tiene bastante mas de los 48dp minimos.
                    .clickable(onClickLabel = d.etiqueta) { onDestino(d) }
                    .semantics { if (activo) selected = true },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Margenes.xs, Alignment.CenterVertically)
            ) {
                Icon(
                    d.icono,
                    contentDescription = null,   // la etiqueta de abajo ya lo nombra
                    tint = color,
                    modifier = Modifier.size(Medidas.icono)
                )
                Text(
                    d.etiqueta,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = color
                )
            }
        }
    }
}
