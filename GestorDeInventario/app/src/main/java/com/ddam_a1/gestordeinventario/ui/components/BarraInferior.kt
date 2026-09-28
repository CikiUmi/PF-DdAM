package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
    VENTAS(RUTA_HISTORIAL_VENTAS, Iconos.Carrito, "Ventas")
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
            // La zona segura se calcula arriba, en Marco, que es quien
            // conoce la pantalla entera. Esta llamada queda por si algun dia
            // la barra se usa fuera de Marco: Compose descuenta los insets ya
            // aplicados por un ancestro, asi que aqui dentro vale cero y no
            // se suma dos veces.
            .navigationBarsPadding()
            // Alto MINIMO y no alto fijo: 64 es lo que pedia el Figma con
            // etiquetas de 12. Con las de 16 y su renglon de 20, el contenido
            // necesita 68 (12 + 24 de icono + 4 + 20 + 8) y en una caja de 64
            // el texto se salia por abajo. Asi la barra crece lo que haga
            // falta, y tambien cuando el usuario agranda la letra del sistema.
            .heightIn(min = Medidas.barraInferior)
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
                    // El area tocable es la columna entera y no solo el
                    // icono: 48 de alto (icono, hueco y etiqueta) por un
                    // cuarto del ancho, bastante mas que el minimo tocable.
                    //
                    // Llenar el alto seria un error ahora: la barra ya no
                    // tiene alto fijo, asi que el alto disponible es lo que
                    // queda de PANTALLA y la barra se la comeria entera.
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
