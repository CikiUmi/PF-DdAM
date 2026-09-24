package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.datos.Notificaciones
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.modelo.Periodo
import com.ddam_a1.gestordeinventario.datos.RendimientoNegocio
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.datos.Ventas
import com.ddam_a1.gestordeinventario.ui.componentes.BarraInferior
import com.ddam_a1.gestordeinventario.ui.componentes.DestinoBarra

/** Pantalla 4 · Menú principal (RF21, RF23). */
@Composable
fun PantallaInicio(
    onAvisos: () -> Unit,
    onConfiguracion: () -> Unit,
    onEstadisticas: () -> Unit,
    onNuevaVenta: () -> Unit,
    onInventario: () -> Unit,
    onCatalogo: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    EstadoApp.version
    val fecha = hoy()
    val ventas = Ventas.obtenerHistorialVentas()
    val delMes = RendimientoNegocio.filtrarVentasPorPeriodo(ventas, Periodo.MENSUAL, fecha)
    val avisos = Notificaciones.revisarStockBajo() + Notificaciones.revisarCaducidadesProximas(fecha)
    val topes = RendimientoNegocio.productosMasVendidos(delMes, 3)

    Marco(
        barra = {
            BarraSuperior("Inicio", EstadoApp.usuario?.nombreUsuario) {
                BotonIcono(Iconos.Campana, "Avisos", { onAvisos() }, conPunto = avisos.isNotEmpty())
                BotonIcono(Iconos.Ajustes, "Configuración", { onConfiguracion() })
            }
        },
        pie = { BarraInferior(DestinoBarra.INICIO, onDestino) }
    ) {
        if (avisos.isNotEmpty()) {
            item {
                BannerAviso("${avisos.size} avisos del inventario",
                    avisos.first().mensaje, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer) { onAvisos() }
            }
        }
        item { EncabezadoSeccion("Métricas principales", "Estadísticas") { onEstadisticas() } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Ingresos del mes", dinero(RendimientoNegocio.calcularIngresos(delMes)),
                    "${delMes.size} ventas", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                TarjetaMetrica("Ganancia", dinero(RendimientoNegocio.calcularGanancias(delMes)),
                    null, MaterialTheme.coloresExtra.correct.colorContainer, MaterialTheme.coloresExtra.correct.color, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Materiales", InventarioMateriales.obtenerTodos().size.toString(),
                    "${InventarioMateriales.obtenerTodos().count { InventarioMateriales.esStockBajo(it) }} bajos",
                    MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary, Modifier.weight(1f)) { onInventario() }
                TarjetaMetrica("Productos", CatalogoProductos.obtenerTodos().size.toString(),
                    null, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f)) { onCatalogo() }
            }
        }
        item { EncabezadoSeccion("Más vendidos del mes") }
        if (topes.isEmpty()) {
            item { Text("Aún no hay ventas este mes.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(topes.size) { i ->
                val (id, n) = topes[i]
                FilaLista(CatalogoProductos.obtenerProductoPorId(id)?.nombre ?: "Producto",
                    valor = "$n", notaValor = "piezas")
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Registrar una venta") { onNuevaVenta() }
        }
    }
}
