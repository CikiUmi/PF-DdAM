package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.productos.CatalogoProductos
import com.ddam_a1.gestordeinventario.rendimiento.Periodo
import com.ddam_a1.gestordeinventario.rendimiento.RendimientoNegocio
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.ventas.ErrorVenta
import com.ddam_a1.gestordeinventario.ventas.ResultadoVenta
import com.ddam_a1.gestordeinventario.ventas.Ventas

/** Pantalla 14 · Registrar venta (RF12, RF13, RF14). */
@Composable
fun PantallaNuevaVenta(nav: Navegador) {
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

    Marco(barra = { BarraSuperior("Nueva venta", onAtras = { nav.volver() }) }) {
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
                BannerAviso("No se pudo registrar", error!!, Peligro, PeligroSuave) { error = null }
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
                Box(Modifier.weight(1f)) { BotonSecundario("Cancelar") { nav.volver() } }
                Box(Modifier.weight(2f)) {
                    BotonPrincipal("Confirmar venta", habilitado = ticket.isNotEmpty()) {
                        when (val r = Ventas.registrarVenta(hoy(), ticket.map { it.key to it.value })) {
                            is ResultadoVenta.Exito -> {
                                EstadoApp.datosCambiaron()
                                nav.irARaiz(Ruta.HistorialVentas)
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

@Composable
private fun Contador(n: Int, onMenos: () -> Unit, onMas: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (n > 0) {
            CajaIcono(Iconos.Quitar, "Quitar", onMenos)
            Text("$n", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.widthIn(min = 24.dp))
        }
        CajaIcono(Iconos.Agregar, "Agregar", onMas)
    }
}

@Composable
private fun CajaIcono(icono: androidx.compose.ui.graphics.vector.ImageVector, d: String, onClick: () -> Unit) {
    Box(
        Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Icon(icono, d, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) }
}

/** Pantalla 15 · Historial de ventas (RF12). */
@Composable
fun PantallaHistorialVentas(nav: Navegador) {
    EstadoApp.version
    var periodo by remember { mutableStateOf(Periodo.DIARIO) }
    val todas = Ventas.obtenerHistorialVentas().reversed()
    val filtradas = RendimientoNegocio.filtrarVentasPorPeriodo(todas, periodo, hoy())

    Marco(
        barra = { BarraSuperior("Ventas", "${filtradas.size} en el período") },
        pie = { BarraInferior(nav.actual) { nav.irARaiz(it) } }
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Hoy", periodo == Periodo.DIARIO) { periodo = Periodo.DIARIO }
                ChipFiltro("Semana", periodo == Periodo.SEMANAL) { periodo = Periodo.SEMANAL }
                ChipFiltro("Mes", periodo == Periodo.MENSUAL) { periodo = Periodo.MENSUAL }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Ingresos", dinero(RendimientoNegocio.calcularIngresos(filtradas)),
                    "${filtradas.count { !it.cancelada }} ventas", PrimarioSuave, Primario, Modifier.weight(1f))
                TarjetaMetrica("Ganancia", dinero(RendimientoNegocio.calcularGanancias(filtradas)),
                    null, ExitoSuave, Exito, Modifier.weight(1f))
            }
        }
        item { EncabezadoSeccion("Movimientos") }
        if (filtradas.isEmpty()) {
            item { EstadoVacio("Sin ventas", "Toca el botón para registrar la primera") }
        } else {
            items(filtradas.size) { i ->
                val v = filtradas[i]
                val detalle = v.items.joinToString(", ") { it ->
                    (CatalogoProductos.obtenerProductoPorId(it.productoId)?.nombre ?: "?") + " x${it.cantidad}"
                }
                FilaLista(
                    detalle.ifBlank { "Venta" },
                    v.fecha + if (v.cancelada) " · cancelada" else "",
                    dinero(v.total),
                    null,
                    if (v.cancelada) MaterialTheme.colorScheme.outline else Exito
                )
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nueva venta") { nav.ir(Ruta.NuevaVenta) }
        }
    }
}
