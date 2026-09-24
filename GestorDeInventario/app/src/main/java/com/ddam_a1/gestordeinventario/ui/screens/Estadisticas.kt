package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Periodo
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.GraficaBarras
import com.ddam_a1.gestordeinventario.ui.components.GraficaDona
import com.ddam_a1.gestordeinventario.ui.components.TarjetaMetrica
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/**
 * Pantalla 5 - Rendimiento del negocio (RF23, RF24).
 *
 * Todo sale de una sola lista, las ventas del periodo. Y `costo` no se
 * consulta: es `ingresos - ganancia`.
 *
 * El periodo sube al NavHost, como en el historial, porque de el dependen los
 * cuatro numeros. Ojo que arranca en MENSUAL y el historial en DIARIO: son dos
 * estados distintos, no uno compartido.
 */
@Composable
fun PantallaEstadisticas(
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    ingresos: Double,
    ganancia: Double,
    costo: Double,
    masVendidos: List<VentaPorProducto>,
    onAtras: () -> Unit
) {
    Marco(barra = { BarraSuperior("Rendimiento", onAtras = onAtras) }) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Dia", periodo == Periodo.DIARIO) { onPeriodo(Periodo.DIARIO) }
                ChipFiltro("Semana", periodo == Periodo.SEMANAL) { onPeriodo(Periodo.SEMANAL) }
                ChipFiltro("Mes", periodo == Periodo.MENSUAL) { onPeriodo(Periodo.MENSUAL) }
            }
        }
        item {
            TarjetaSuave {
                Text("Ingresos y costo", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(14.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GraficaDona(
                        listOf(
                            MaterialTheme.coloresExtra.correct.color to ganancia.coerceAtLeast(0.0).toFloat(),
                            MaterialTheme.colorScheme.secondary to costo.toFloat()
                        ),
                        dinero(ingresos), "Ingresos"
                    )
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaMetrica("Ganancia", dinero(ganancia), null,
                        MaterialTheme.coloresExtra.correct.colorContainer,
                        MaterialTheme.coloresExtra.correct.color, Modifier.weight(1f))
                    TarjetaMetrica("Costo", dinero(costo), null,
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                }
            }
        }
        item {
            TarjetaSuave {
                Text("Productos mas vendidos", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                if (masVendidos.isEmpty()) {
                    Text("Sin datos en este periodo.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    GraficaBarras(
                        masVendidos.map { it.nombre.take(6) to it.piezas.toDouble() },
                        MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        items(masVendidos.size) { i ->
            val renglon = masVendidos[i]
            FilaLista(
                renglon.nombre,
                cant(renglon.precioUnitario) + " c/u",
                dinero(renglon.precioUnitario * renglon.piezas),
                renglon.piezas.toString() + " piezas"
            )
        }
    }
}
