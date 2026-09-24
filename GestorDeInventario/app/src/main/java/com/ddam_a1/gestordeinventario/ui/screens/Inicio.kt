package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.ui.components.BannerAviso
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.TarjetaMetrica
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/**
 * Pantalla 4 - Menu principal (RF21, RF23).
 *
 * Es la que mas datos junta de toda la app: materiales, productos, ventas y
 * avisos. Y aun asi no pide ninguno: todos llegan ya calculados dentro de
 * `resumen`, y los avisos y el top ya resueltos.
 */
@Composable
fun PantallaInicio(
    nombreUsuario: String?,
    avisos: List<Aviso>,
    resumen: ResumenInicio,
    masVendidos: List<VentaPorProducto>,
    onAvisos: () -> Unit,
    onConfiguracion: () -> Unit,
    onEstadisticas: () -> Unit,
    onNuevaVenta: () -> Unit,
    onInventario: () -> Unit,
    onCatalogo: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Marco(
        barra = {
            BarraSuperior("Inicio", nombreUsuario) {
                BotonIcono(Iconos.Campana, "Avisos", { onAvisos() }, conPunto = avisos.isNotEmpty())
                BotonIcono(Iconos.Ajustes, "Configuracion", { onConfiguracion() })
            }
        },
        pie = { BarraInferior(DestinoBarra.INICIO, onDestino) }
    ) {
        if (avisos.isNotEmpty()) {
            item {
                BannerAviso(
                    avisos.size.toString() + " avisos del inventario",
                    avisos.first().mensaje,
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.errorContainer
                ) { onAvisos() }
            }
        }

        item { EncabezadoSeccion("Metricas principales", "Estadisticas") { onEstadisticas() } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    "Ingresos del mes", dinero(resumen.ingresosDelMes),
                    resumen.ventasDelMes.toString() + " ventas",
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.primary, Modifier.weight(1f)
                )
                TarjetaMetrica(
                    "Ganancia", dinero(resumen.gananciaDelMes), null,
                    MaterialTheme.coloresExtra.correct.colorContainer,
                    MaterialTheme.coloresExtra.correct.color, Modifier.weight(1f)
                )
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    "Materiales", resumen.totalMateriales.toString(),
                    resumen.materialesBajos.toString() + " bajos",
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.secondary, Modifier.weight(1f)
                ) { onInventario() }
                TarjetaMetrica(
                    "Productos", resumen.totalProductos.toString(), null,
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.tertiary, Modifier.weight(1f)
                ) { onCatalogo() }
            }
        }

        item { EncabezadoSeccion("Mas vendidos del mes") }
        if (masVendidos.isEmpty()) {
            item {
                Text("Aun no hay ventas este mes.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(masVendidos.size) { i ->
                val renglon = masVendidos[i]
                FilaLista(renglon.nombre, valor = renglon.piezas.toString(), notaValor = "piezas")
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Registrar una venta") { onNuevaVenta() }
        }
    }
}
