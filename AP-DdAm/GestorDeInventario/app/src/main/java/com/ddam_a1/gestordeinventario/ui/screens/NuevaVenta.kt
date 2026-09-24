package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.datos.ErrorVenta
import com.ddam_a1.gestordeinventario.datos.ResultadoVenta
import com.ddam_a1.gestordeinventario.datos.Ventas

/** Pantalla 14 · Registrar venta (RF12, RF13, RF14). */
@Composable
fun PantallaNuevaVenta(onVentaRegistrada: () -> Unit, onAtras: () -> Unit) {
    EstadoApp.version
    val ticket = remember { mutableStateMapOf<String, Int>() }
    var buscar by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val productos = if (buscar.isBlank()) CatalogoProductos.obtenerTodos()
    else CatalogoProductos.buscarProducto(buscar)
    val total = ticket.entries.sumOf { (id, n) ->
        (CatalogoProductos.obtenerProductoPorId(id)?.precioVenta ?: 0.0) * n
    }
    val piezas = ticket.values.sum()

    Marco(barra = { BarraSuperior("Nueva venta", onAtras = { onAtras() }) }) {
        item { BarraBusqueda(buscar, "Agregar producto al ticket") { buscar = it } }
        if (productos.isEmpty()) {
            item { EstadoVacio("Sin productos", "Agrega productos al catálogo primero") }
        }
        items(productos.size) { i ->
            val p = productos[i]
            val n = ticket[p.id] ?: 0
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(p.nombre, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        dinero(p.precioVenta) + " · " +
                            if (p.esBajoPedido) "descuenta materiales" else "stock: ${p.stockDisponible}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Contador(n,
                    onMenos = { if (n > 1) ticket[p.id] = n - 1 else ticket.remove(p.id) },
                    onMas = { ticket[p.id] = n + 1 })
            }
        }
        if (error != null) {
            item {
                BannerAviso("No se pudo registrar", error!!, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer) { error = null }
            }
        }
        item {
            TarjetaSuave {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${ticket.size} productos · $piezas piezas",
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
                        when (val r = Ventas.registrarVenta(hoy(), ticket.map { it.key to it.value })) {
                            is ResultadoVenta.Exito -> {
                                EstadoApp.datosCambiaron()
                                onVentaRegistrada()
                            }
                            is ResultadoVenta.Fallo -> error = when (r.motivo) {
                                ErrorVenta.MATERIALES_INSUFICIENTES -> "No alcanzan los materiales para todo el ticket."
                                ErrorVenta.STOCK_INSUFICIENTE -> "No hay stock suficiente de alguno de los productos."
                                ErrorVenta.CANTIDAD_INVALIDA -> "Hay una cantidad inválida."
                                ErrorVenta.PRODUCTO_NO_EXISTE -> "Un producto del ticket ya no existe."
                                ErrorVenta.TICKET_VACIO -> "El ticket está vacío."
                            }
                        }
                    }
                }
            }
        }
    }
}
