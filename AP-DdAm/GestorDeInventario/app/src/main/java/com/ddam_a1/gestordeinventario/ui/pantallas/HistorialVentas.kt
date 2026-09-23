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
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.modelo.Periodo
import com.ddam_a1.gestordeinventario.datos.RendimientoNegocio
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.datos.ErrorVenta
import com.ddam_a1.gestordeinventario.datos.ResultadoVenta
import com.ddam_a1.gestordeinventario.datos.Ventas
import com.ddam_a1.gestordeinventario.ui.navegacion.BarraInferior
import com.ddam_a1.gestordeinventario.ui.navegacion.Navegador
import com.ddam_a1.gestordeinventario.ui.navegacion.Ruta

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
                    "${filtradas.count { !it.cancelada }} ventas", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                TarjetaMetrica("Ganancia", dinero(RendimientoNegocio.calcularGanancias(filtradas)),
                    null, MaterialTheme.coloresExtra.correct.colorContainer, MaterialTheme.coloresExtra.correct.color, Modifier.weight(1f))
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
                    if (v.cancelada) MaterialTheme.colorScheme.outline else MaterialTheme.coloresExtra.correct.color
                )
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nueva venta") { nav.ir(Ruta.NuevaVenta) }
        }
    }
}
