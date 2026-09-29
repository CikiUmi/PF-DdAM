package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import com.ddam_a1.gestordeinventario.modelClasses.enums.Periodo
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.FilaChips
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.GraficaBarras
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.PanelLateral
import com.ddam_a1.gestordeinventario.ui.components.TarjetaMetrica
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.tituloDeVentas
import com.ddam_a1.gestordeinventario.ui.fechaYHora
import com.ddam_a1.gestordeinventario.ui.folioDe
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 15 - HISTORIAL DE VENTAS   (Figma 51:1467 / 51:1724 / 52:1575)
//  RF12
//
//  El periodo elegido NO se queda aqui: sube al NavHost. Es la unica forma,
//  porque de el dependen la lista filtrada y las dos metricas, y esos los
//  calcula el ViewModel. Si el chip viviera aqui abajo, la pantalla tendria
//  que pedir el recalculo, y para eso necesitaria conocer al ViewModel.
//
//  Los nombres de producto NO hacen falta aqui: el renglon ensena folio, fecha
//  y cuantos productos lleva, y el desglose vive en la pantalla 16.
//
//  LA GRAFICA es la misma `GraficaBarras` de Rendimiento, en las tres
//  medidas. El Figma dibujaba en compacta unas barras sueltas de 20 de ancho
//  sobre una caja, sin eje ni valores; con pocas ventas se veian perdidas y no
//  se parecian a la de Rendimiento, que es la misma informacion. Una sola
//  grafica para los dos sitios: se lee igual y se arregla en un sitio.
// ============================================================

@Composable
fun PantallaHistorialVentas(
    ventas: List<Venta>,
    ventasPorDia: List<Pair<String, Double>>,
    ingresos: Double,
    ganancias: Double,
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    onVenta: (String) -> Unit,
    onNuevaVenta: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    BoxWithConstraints {
        if (anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA) {
            VentasConPanel(
                ventas, ventasPorDia, ingresos, ganancias,
                periodo, onPeriodo, onVenta, onNuevaVenta, onDestino
            )
        } else {
            VentasConBarra(
                ventas, ventasPorDia, ingresos, ganancias,
                periodo, onPeriodo, onVenta, onNuevaVenta, onDestino
            )
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun VentasConBarra(
    ventas: List<Venta>,
    ventasPorDia: List<Pair<String, Double>>,
    ingresos: Double,
    ganancias: Double,
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    onVenta: (String) -> Unit,
    onNuevaVenta: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Marco(
        barra = { BarraSuperior("Ventas", ventas.size.toString() + " en el periodo") },
        pie = { BarraInferior(DestinoBarra.VENTAS, onDestino) }
    ) {
        item { GraficaBarras(tituloDeVentas(periodo), ventasPorDia, formatearValor = { dinero(it) }) }
        item { SelectorPeriodoVentas(periodo, onPeriodo) }
        item { CifrasVentas(ingresos, ganancias) }
        item { TituloHistorial() }
        listaDeVentas(ventas, onVenta)
        item { BotonPrincipal("Nueva venta", onClick = onNuevaVenta) }
    }
}

// ---------- TABLETA  (Figma 52:1575) ----------

@Composable
private fun VentasConPanel(
    ventas: List<Venta>,
    ventasPorDia: List<Pair<String, Double>>,
    ingresos: Double,
    ganancias: Double,
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    onVenta: (String) -> Unit,
    onNuevaVenta: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Row(Modifier.fillMaxSize().systemBarsPadding()) {
            PanelLateral(DestinoBarra.VENTAS, onDestino)

                // ============================================================
                //  TODO DENTRO DE LA LISTA, INCLUIDO EL ENCABEZADO
                //
                //  Antes esto era una Column con el encabezado, la grafica y
                //  las cifras FIJOS, y abajo una lista que se llevaba el alto
                //  sobrante. En una ventana alta funciona; en una baja —una
                //  tableta acostada, o una ventana de escritorio a media
                //  altura— el contenido fijo se come el alto entero y a la
                //  lista le queda CERO: no se ve y, sobre todo, nada se
                //  desplaza, asi que lo que sobra abajo es inalcanzable.
                //
                //  Metiendolo todo en la LazyColumn, la pantalla se desplaza
                //  entera, como ya hace la version de telefono. Y de paso se
                //  quita un desplazamiento anidado, que en tableta es una
                //  fuente clasica de gestos que no responden.
                // ============================================================
            LazyColumn(
                Modifier.weight(1f).fillMaxSize().padding(Margenes.xl),
                verticalArrangement = Arrangement.spacedBy(Margenes.lg)
            ) {
                item { Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    Text(
                        "Ventas",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f).semantics { heading() }
                    )
                    // ============================================================
                    //  UN BOTON EN UN RENGLON TIENE QUE LLEVAR SU ANCHO
                    //
                    //  `BotonBase` hace `fillMaxWidth()` por dentro, que es lo
                    //  correcto para un boton al pie de un formulario. Dentro
                    //  de un Row, un hijo SIN peso se mide primero y con todo
                    //  el ancho disponible: el boton se quedaba con el renglon
                    //  entero y al titulo le tocaban cero, asi que "Ventas"
                    //  salia en vertical, una letra por linea.
                    //
                    //  Con un ancho maximo, el `fillMaxWidth` de dentro llena
                    //  esos 220 y no mas. Es el mismo patron que ya usan
                    //  Inventario y Catalogo con su barra de busqueda.
                    // ============================================================
                    BotonPrincipal(
                        "Nueva venta",
                        modifier = Modifier.widthIn(max = 220.dp),
                        onClick = onNuevaVenta
                    )
                } }

                item {
                    GraficaBarras(
                        tituloDeVentas(periodo), ventasPorDia,
                        formatearValor = { dinero(it) }
                    )
                }
                item { SelectorPeriodoVentas(periodo, onPeriodo) }
                item { CifrasVentas(ingresos, ganancias) }
                item { TituloHistorial() }

                listaDeVentas(ventas, onVenta)
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

@Composable
private fun SelectorPeriodoVentas(periodo: Periodo, onPeriodo: (Periodo) -> Unit) {
    FilaChips {
        ChipFiltro("Hoy", periodo == Periodo.DIARIO) { onPeriodo(Periodo.DIARIO) }
        ChipFiltro("Semana", periodo == Periodo.SEMANAL) { onPeriodo(Periodo.SEMANAL) }
        ChipFiltro("Mes", periodo == Periodo.MENSUAL) { onPeriodo(Periodo.MENSUAL) }
    }
}

@Composable
private fun CifrasVentas(ingresos: Double, ganancias: Double) {
    // Una ganancia negativa cambia la rayita y la cifra a rojo. La rayita sola
    // no bastaria —mide 4 de alto y es decorativa para el lector de pantalla—,
    // y la cifra sola obliga a cazar un signo menos de dos pixeles. Las dos
    // juntas se ven antes de leer el numero.
    val enPerdida = ganancias < 0.0
    // "Ingresos" puede traer nota y "Ganancia" no: FilaPareja las deja iguales.
    FilaPareja {
        TarjetaMetrica(
            "Ingresos", dinero(ingresos), null,
            colorAcento = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            compacta = true, maxLineasCifra = 2
        )
        TarjetaMetrica(
            if (enPerdida) "Pérdida" else "Ganancia", dinero(ganancias), null,
            colorAcento = if (enPerdida) MaterialTheme.colorScheme.error
            else MaterialTheme.coloresExtra.correct.color,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            compacta = true, maxLineasCifra = 2,
            colorValor = if (enPerdida) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TituloHistorial() {
    Text(
        "Historial",
        style = MaterialTheme.typography.tituloMedio,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.semantics { heading() }
    )
}

private fun LazyListScope.listaDeVentas(ventas: List<Venta>, onVenta: (String) -> Unit) {
    if (ventas.isEmpty()) {
        item { EstadoVacio("Sin ventas", "Las ventas del periodo aparecerán aquí") }
        return
    }
    items(ventas.size) { i ->
        val venta = ventas[i]
        FilaVentaHistorial(venta) { onVenta(venta.id) }
    }
}

/**
 * Un renglon del historial (Figma 51:1520).
 *
 * Una venta CANCELADA no se borra del historial: se queda, atenuada y dicho
 * con todas sus letras. Es la unica forma de que cuadre con la contabilidad,
 * y de paso explica por que los ingresos del periodo no son la suma de lo que
 * se ve.
 */
@Composable
private fun FilaVentaHistorial(venta: Venta, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val apagado = venta.cancelada

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(cs.surfaceContainerHigh)
            .clickable { onClick() }
            .padding(Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                folioDe(venta.id) + " · " + fechaYHora(venta.fecha, venta.hora),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (apagado) cs.onSurfaceVariant else cs.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                if (apagado) "Cancelada"
                else venta.items.size.toString() +
                    (if (venta.items.size == 1) " producto" else " productos"),
                style = MaterialTheme.typography.bodyLarge,
                color = if (apagado) cs.error else cs.onSurfaceVariant
            )
        }
        Text(
            dinero(venta.total),
            style = MaterialTheme.typography.tituloMedio,
            color = if (apagado) cs.onSurfaceVariant else cs.onSurface
        )
        Icon(
            Iconos.Siguiente,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = cs.onSurfaceVariant
        )
    }
}
