package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.components.BannerAviso
import com.ddam_a1.gestordeinventario.ui.components.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero

/**
 * Pantalla 14 - Registrar venta (RF12, RF13, RF14).
 *
 * El ticket es estado de ESTA pantalla: mientras lo armas no existe para nadie
 * mas, y si te sales se tira. Solo al confirmar se entrega completo.
 *
 * `error` llega de afuera porque es la respuesta a la operacion: la pantalla no
 * sabe si fallo por materiales o por stock, solo lo muestra.
 */
@Composable
fun PantallaNuevaVenta(
    productos: List<Producto>,
    error: String?,
    onDescartarError: () -> Unit,
    onConfirmar: (Map<String, Int>) -> Unit,
    onAtras: () -> Unit
) {
    val ticket = remember { mutableStateMapOf<String, Int>() }
    var buscar by remember { mutableStateOf("") }

    val encontrados =
        if (buscar.isBlank()) productos
        else productos.filter { it.nombre.contains(buscar, ignoreCase = true) }

    // Sumar el ticket es aritmetica para mostrar: depende de lo que el usuario
    // acaba de tocar, no de la base.
    var total = 0.0
    for (entrada in ticket) {
        val producto = productos.find { it.id == entrada.key }
        total += (producto?.precioVenta ?: 0.0) * entrada.value
    }
    val piezas = ticket.values.sum()

    Marco(barra = { BarraSuperior("Nueva venta", onAtras = onAtras) }) {
        item { BarraBusqueda(buscar, "Agregar producto al ticket") { buscar = it } }
        if (encontrados.isEmpty()) {
            item { EstadoVacio("Sin productos", "Agrega productos al catalogo primero") }
        }
        items(encontrados.size) { i ->
            val producto = encontrados[i]
            val n = ticket[producto.id] ?: 0
            Row(
                Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(producto.nombre, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        dinero(producto.precioVenta) + " - " +
                            (if (producto.esBajoPedido) "descuenta materiales"
                             else "stock: " + producto.stockDisponible),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Contador(
                    n,
                    onMenos = { if (n > 1) ticket[producto.id] = n - 1 else ticket.remove(producto.id) },
                    onMas = { ticket[producto.id] = n + 1 }
                )
            }
        }
        if (error != null) {
            item {
                BannerAviso(
                    "No se pudo registrar", error,
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.errorContainer
                ) { onDescartarError() }
            }
        }
        item {
            TarjetaSuave {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(ticket.size.toString() + " productos - " + piezas + " piezas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Total", style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(dinero(total), style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { BotonSecundario("Cancelar") { onAtras() } }
                Box(Modifier.weight(2f)) {
                    BotonPrincipal("Confirmar venta", habilitado = ticket.isNotEmpty()) {
                        onConfirmar(ticket.toMap())
                    }
                }
            }
        }
    }
}
