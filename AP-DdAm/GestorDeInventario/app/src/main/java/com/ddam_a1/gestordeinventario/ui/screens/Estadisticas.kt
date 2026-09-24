package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.data.CatalogoProductos
import com.ddam_a1.gestordeinventario.modelClasses.Periodo
import com.ddam_a1.gestordeinventario.data.RendimientoNegocio
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.components.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.data.Ventas

/** Pantalla 5 · Rendimiento del negocio (RF23, RF24). */
@Composable
fun PantallaEstadisticas(onAtras: () -> Unit) {
    EstadoApp.version
    var periodo by remember { mutableStateOf(Periodo.MENSUAL) }
    val fecha = hoy()
    val ventas = RendimientoNegocio.filtrarVentasPorPeriodo(Ventas.obtenerHistorialVentas(), periodo, fecha)
    val ingresos = RendimientoNegocio.calcularIngresos(ventas)
    val ganancia = RendimientoNegocio.calcularGanancias(ventas)
    val costo = (ingresos - ganancia).coerceAtLeast(0.0)
    val top = RendimientoNegocio.productosMasVendidos(ventas, 5)

    Marco(barra = { BarraSuperior("Rendimiento", onAtras = { onAtras() }) }) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Día", periodo == Periodo.DIARIO) { periodo = Periodo.DIARIO }
                ChipFiltro("Semana", periodo == Periodo.SEMANAL) { periodo = Periodo.SEMANAL }
                ChipFiltro("Mes", periodo == Periodo.MENSUAL) { periodo = Periodo.MENSUAL }
            }
        }
        item {
            TarjetaSuave {
                Text("Ingresos y costo", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(14.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GraficaDona(
                        listOf(MaterialTheme.coloresExtra.correct.color to ganancia.coerceAtLeast(0.0).toFloat(), MaterialTheme.colorScheme.secondary to costo.toFloat()),
                        dinero(ingresos), "Ingresos"
                    )
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaMetrica("Ganancia", dinero(ganancia), null, MaterialTheme.coloresExtra.correct.colorContainer, MaterialTheme.coloresExtra.correct.color, Modifier.weight(1f))
                    TarjetaMetrica("Costo", dinero(costo), null, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                }
            }
        }
        item {
            TarjetaSuave {
                Text("Productos más vendidos", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                if (top.isEmpty()) {
                    Text("Sin datos en este período.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    GraficaBarras(top.map { (id, n) ->
                        (CatalogoProductos.obtenerProductoPorId(id)?.nombre?.take(6) ?: "—") to n.toDouble()
                    }, MaterialTheme.colorScheme.primary)
                }
            }
        }
        items(top.size) { i ->
            val (id, n) = top[i]
            val p = CatalogoProductos.obtenerProductoPorId(id)
            FilaLista(p?.nombre ?: "Producto", "${cant((p?.precioVenta ?: 0.0))} c/u",
                dinero((p?.precioVenta ?: 0.0) * n), "$n piezas")
        }
    }
}
