package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Periodo
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.TarjetaMetrica
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/**
 * Pantalla 15 - Historial de ventas (RF12).
 *
 * El periodo elegido NO se queda aqui: sube al NavHost. Es la unica forma,
 * porque de el dependen la lista filtrada y las dos metricas, y esos los
 * calcula el ViewModel. Si el chip viviera aqui abajo, la pantalla tendria que
 * pedir el recalculo, y para eso necesitaria conocer al ViewModel.
 *
 * `nombreProducto` llega como funcion de busqueda: la pantalla arma el texto
 * del renglon (eso es presentacion) pero no sale a buscar los productos.
 */
@Composable
fun PantallaHistorialVentas(
    ventas: List<Venta>,
    nombreProducto: (String) -> String,
    ingresos: Double,
    ganancias: Double,
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    onNuevaVenta: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Marco(
        barra = { BarraSuperior("Ventas", ventas.size.toString() + " en el periodo") },
        pie = { BarraInferior(DestinoBarra.VENTAS, onDestino) }
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Hoy", periodo == Periodo.DIARIO) { onPeriodo(Periodo.DIARIO) }
                ChipFiltro("Semana", periodo == Periodo.SEMANAL) { onPeriodo(Periodo.SEMANAL) }
                ChipFiltro("Mes", periodo == Periodo.MENSUAL) { onPeriodo(Periodo.MENSUAL) }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    "Ingresos", dinero(ingresos),
                    ventas.count { !it.cancelada }.toString() + " ventas",
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.primary,
                    Modifier.weight(1f)
                )
                TarjetaMetrica(
                    "Ganancia", dinero(ganancias), null,
                    MaterialTheme.coloresExtra.correct.colorContainer,
                    MaterialTheme.coloresExtra.correct.color,
                    Modifier.weight(1f)
                )
            }
        }
        item { EncabezadoSeccion("Movimientos") }
        if (ventas.isEmpty()) {
            item { EstadoVacio("Sin ventas", "Toca el boton para registrar la primera") }
        } else {
            items(ventas.size) { i ->
                val venta = ventas[i]
                val detalle = venta.items.joinToString(", ") { item ->
                    nombreProducto(item.productoId) + " x" + item.cantidad
                }
                FilaLista(
                    detalle.ifBlank { "Venta" },
                    venta.fecha + (if (venta.cancelada) " - cancelada" else ""),
                    dinero(venta.total),
                    null,
                    if (venta.cancelada) MaterialTheme.colorScheme.outline
                    else MaterialTheme.coloresExtra.correct.color
                )
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nueva venta") { onNuevaVenta() }
        }
    }
}
