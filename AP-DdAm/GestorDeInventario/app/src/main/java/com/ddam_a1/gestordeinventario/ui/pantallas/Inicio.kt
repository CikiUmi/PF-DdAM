package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.inventario.InventarioMateriales
import com.ddam_a1.gestordeinventario.notificaciones.Notificaciones
import com.ddam_a1.gestordeinventario.productos.CatalogoProductos
import com.ddam_a1.gestordeinventario.rendimiento.Periodo
import com.ddam_a1.gestordeinventario.rendimiento.RendimientoNegocio
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.ventas.Ventas

/** Pantalla 4 · Menú principal (RF21, RF23). */
@Composable
fun PantallaInicio(nav: Navegador) {
    EstadoApp.version
    val fecha = hoy()
    val ventas = Ventas.obtenerHistorialVentas()
    val delMes = RendimientoNegocio.filtrarVentasPorPeriodo(ventas, Periodo.MENSUAL, fecha)
    val avisos = Notificaciones.revisarStockBajo() + Notificaciones.revisarCaducidadesProximas(fecha)
    val topes = RendimientoNegocio.productosMasVendidos(delMes, 3)

    Marco(
        barra = {
            BarraSuperior("Inicio", EstadoApp.usuario?.nombreUsuario) {
                BotonIcono(Iconos.Campana, "Avisos", { nav.ir(Ruta.Avisos) }, conPunto = avisos.isNotEmpty())
                BotonIcono(Iconos.Ajustes, "Configuración", { nav.ir(Ruta.Configuracion) })
            }
        },
        pie = { BarraInferior(nav.actual) { nav.irARaiz(it) } }
    ) {
        if (avisos.isNotEmpty()) {
            item {
                BannerAviso("${avisos.size} avisos del inventario",
                    avisos.first().mensaje, Peligro, PeligroSuave) { nav.ir(Ruta.Avisos) }
            }
        }
        item { EncabezadoSeccion("Métricas principales", "Estadísticas") { nav.ir(Ruta.Estadisticas) } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Ingresos del mes", dinero(RendimientoNegocio.calcularIngresos(delMes)),
                    "${delMes.size} ventas", PrimarioSuave, Primario, Modifier.weight(1f))
                TarjetaMetrica("Ganancia", dinero(RendimientoNegocio.calcularGanancias(delMes)),
                    null, ExitoSuave, Exito, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Materiales", InventarioMateriales.obtenerTodos().size.toString(),
                    "${InventarioMateriales.obtenerTodos().count { InventarioMateriales.esStockBajo(it) }} bajos",
                    SecundarioSuave, Secundario, Modifier.weight(1f)) { nav.irARaiz(Ruta.Inventario) }
                TarjetaMetrica("Productos", CatalogoProductos.obtenerTodos().size.toString(),
                    null, AcentoSuave, Acento, Modifier.weight(1f)) { nav.irARaiz(Ruta.Catalogo) }
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
            BotonPrincipal("Registrar una venta") { nav.ir(Ruta.NuevaVenta) }
        }
    }
}

/** Pantalla 5 · Rendimiento del negocio (RF23, RF24). */
@Composable
fun PantallaEstadisticas(nav: Navegador) {
    EstadoApp.version
    var periodo by remember { mutableStateOf(Periodo.MENSUAL) }
    val fecha = hoy()
    val ventas = RendimientoNegocio.filtrarVentasPorPeriodo(Ventas.obtenerHistorialVentas(), periodo, fecha)
    val ingresos = RendimientoNegocio.calcularIngresos(ventas)
    val ganancia = RendimientoNegocio.calcularGanancias(ventas)
    val costo = (ingresos - ganancia).coerceAtLeast(0.0)
    val top = RendimientoNegocio.productosMasVendidos(ventas, 5)

    Marco(barra = { BarraSuperior("Rendimiento", onAtras = { nav.volver() }) }) {
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
                        listOf(Exito to ganancia.coerceAtLeast(0.0).toFloat(), Secundario to costo.toFloat()),
                        dinero(ingresos), "Ingresos"
                    )
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaMetrica("Ganancia", dinero(ganancia), null, ExitoSuave, Exito, Modifier.weight(1f))
                    TarjetaMetrica("Costo", dinero(costo), null, SecundarioSuave, Secundario, Modifier.weight(1f))
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
                    }, Primario)
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
