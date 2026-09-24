package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.components.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.Insignia
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/**
 * Pantalla 9 - Catalogo de productos (RF8).
 *
 * `costos` llega ya calculado, uno por id. Antes la pantalla llamaba a
 * `calcularCostoProduccion` dentro del bucle de la lista: una consulta por
 * renglon, en cada recomposicion.
 */
@Composable
fun PantallaCatalogo(
    productos: List<Producto>,
    costos: Map<String, Double>,
    onProducto: (String) -> Unit,
    onNuevoProducto: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(0) } // 0 todos - 1 con stock - 2 bajo pedido

    val base =
        if (texto.isBlank()) productos
        else productos.filter { it.nombre.contains(texto, ignoreCase = true) }

    val lista = when (filtro) {
        1 -> base.filter { producto -> !producto.esBajoPedido }
        2 -> base.filter { producto -> producto.esBajoPedido }
        else -> base
    }

    Marco(
        barra = { BarraSuperior("Catalogo", base.size.toString() + " productos") },
        pie = { BarraInferior(DestinoBarra.CATALOGO, onDestino) }
    ) {
        item { BarraBusqueda(texto, "Buscar y filtrar producto", { texto = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Todos", filtro == 0, { filtro = 0 })
                ChipFiltro("Con stock", filtro == 1, { filtro = 1 })
                ChipFiltro("Bajo pedido", filtro == 2, { filtro = 2 })
            }
        }
        if (lista.isEmpty()) {
            item { EstadoVacio("Sin productos", "Agrega el primero para poder vender") }
        } else {
            items(lista.size) { i ->
                val producto = lista[i]
                val costo = costos[producto.id] ?: 0.0
                TarjetaSuave(onClick = { onProducto(producto.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Costo " + dinero(costo) + " - Precio " + dinero(producto.precioVenta),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (producto.esBajoPedido) {
                            Insignia("Bajo pedido",
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.tertiaryContainer)
                        } else {
                            Insignia(producto.stockDisponible.toString() + " pza",
                                MaterialTheme.coloresExtra.correct.color,
                                MaterialTheme.coloresExtra.correct.colorContainer)
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nuevo producto") { onNuevoProducto() }
        }
    }
}
