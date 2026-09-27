package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.enums.Periodo
import com.ddam_a1.gestordeinventario.ui.components.BarraProgreso
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.GraficaBarras
import com.ddam_a1.gestordeinventario.ui.components.GraficaDona
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.PorcionDona
import com.ddam_a1.gestordeinventario.ui.components.SelectorPestanas
import com.ddam_a1.gestordeinventario.ui.components.TarjetaCifra
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 5 - RENDIMIENTO
//  Figma: Compact 41:802 · Medium 41:1083 · Tablet 84:4741
//
//  Aqui viven TODOS los numeros. Tapa ademas un hueco de la lista:
//  `calcularPerdidas` (RF23) existia en el repositorio y no aparecia en
//  ninguna pantalla.
//
//  Las tres medidas no son tres pantallas: son el mismo contenido repartido
//  distinto segun el ancho que haya.
//
//    COMPACTA   todo en una columna
//    MEDIA      la dona comparte renglon con las tres cifras, que se apilan.
//               Sola, la dona dejaba medio ancho vacio.
//    EXPANDIDA  dos columnas: a la izquierda el analisis, a la derecha el
//               ranking, que asi se ve entero sin desplazar
// ============================================================

/** Un renglon de "productos mas vendidos". */
data class VentaPorProducto(
    val nombre: String,
    val piezas: Int,
    val precioUnitario: Double
)

@Composable
fun PantallaRendimiento(
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    ingresos: Double,
    ganancia: Double,
    costo: Double,
    perdidas: Double,
    /** Ventas por dia de la semana, en orden. */
    ventasPorDia: List<Pair<String, Double>>,
    masVendidos: List<VentaPorProducto>,
    onAtras: () -> Unit
) {
    BoxWithConstraints {
        when (anchoPantallaDe(maxWidth)) {
            AnchoPantalla.EXPANDIDA -> RendimientoDosColumnas(
                periodo, onPeriodo, ingresos, ganancia, costo, perdidas,
                ventasPorDia, masVendidos, onAtras
            )
            AnchoPantalla.MEDIA -> RendimientoUnaColumna(
                periodo, onPeriodo, ingresos, ganancia, costo, perdidas,
                ventasPorDia, masVendidos, onAtras, anchaDeSobra = true
            )
            AnchoPantalla.COMPACTA -> RendimientoUnaColumna(
                periodo, onPeriodo, ingresos, ganancia, costo, perdidas,
                ventasPorDia, masVendidos, onAtras, anchaDeSobra = false
            )
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun RendimientoUnaColumna(
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    ingresos: Double,
    ganancia: Double,
    costo: Double,
    perdidas: Double,
    ventasPorDia: List<Pair<String, Double>>,
    masVendidos: List<VentaPorProducto>,
    onAtras: () -> Unit,
    /** true en MEDIA: la dona y las cifras caben en el mismo renglon. */
    anchaDeSobra: Boolean
) {
    Marco(barra = { BarraSuperior("Rendimiento", onAtras = onAtras) }) {
        item { SelectorPeriodo(periodo, onPeriodo) }

        // Respiro entre bloques. `Marco` ya reparte 12; con esto son 24, que
        // es lo que separa una seccion de la siguiente en el diseno.
        item { Spacer(Modifier.height(Margenes.md)) }

        item { GraficaBarras("Ventas por día", ventasPorDia) }


        if (anchaDeSobra) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    Dona(ingresos, ganancia, costo, Modifier.weight(1.6f))
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Margenes.sm)
                    ) { CifrasApiladas(ganancia, costo, perdidas) }
                }
            }
        } else {
            item { Dona(ingresos, ganancia, costo) }
            item {
                FilaPareja(separacion = Margenes.sm) {
                    CifrasEnFila(ganancia, costo, perdidas, etiquetasLargas = false)
                }
            }
        }

        item { Spacer(Modifier.height(Margenes.md)) }

        item { EncabezadoRanking() }

        if (masVendidos.isEmpty()) {
            item {
                EstadoVacio(
                    "Sin ventas en este período",
                    "Cambie el período o registre una venta"
                )
            }
        } else {
            // La barra mas larga es la del producto mas vendido, no un
            // porcentaje del total: compararlos entre si es el punto.
            val tope = masVendidos.maxOf { it.piezas }.coerceAtLeast(1)
            items(masVendidos.size) { i ->
                val renglon = masVendidos[i]
                BarraProgreso(
                    etiqueta = renglon.nombre,
                    valor = renglon.piezas.toString() + " un",
                    proporcion = renglon.piezas.toFloat() / tope
                )
            }
        }
    }
}

// ---------- TABLETA  (Figma 84:4741) ----------

@Composable
private fun RendimientoDosColumnas(
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    ingresos: Double,
    ganancia: Double,
    costo: Double,
    perdidas: Double,
    ventasPorDia: List<Pair<String, Double>>,
    masVendidos: List<VentaPorProducto>,
    onAtras: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 40.dp, vertical = Margenes.xl),
            verticalArrangement = Arrangement.spacedBy(Margenes.xl)
        ) {
            // La cabecera cruza las dos columnas.
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
            ) {
                BotonIcono(Iconos.Atras, "Atrás", onAtras)
                Text(
                    "Rendimiento",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Row(
                Modifier.fillMaxWidth().fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(Margenes.xl)
            ) {
                // Izquierda: el analisis
                Column(
                    Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Margenes.xl)
                ) {
                    SelectorPeriodo(periodo, onPeriodo)
                    GraficaBarras("Ventas por día", ventasPorDia)
                    Dona(ingresos, ganancia, costo, conImportes = true)
                    FilaPareja(separacion = Margenes.lg) {
                        CifrasEnFila(ganancia, costo, perdidas, etiquetasLargas = true)
                    }
                }

                // Derecha: el ranking, que aqui se ve entero sin desplazar
                Column(
                    Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Margenes.md)
                ) {
                    EncabezadoRanking()
                    if (masVendidos.isEmpty()) {
                        EstadoVacio(
                            "Sin ventas en este período",
                            "Cambie el período o registre una venta"
                        )
                    } else {
                        val tope = masVendidos.maxOf { it.piezas }.coerceAtLeast(1)
                        masVendidos.forEach { renglon ->
                            BarraProgreso(
                                etiqueta = renglon.nombre,
                                valor = renglon.piezas.toString() + " un",
                                proporcion = renglon.piezas.toFloat() / tope
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

@Composable
private fun SelectorPeriodo(periodo: Periodo, onPeriodo: (Periodo) -> Unit) {
    val periodos = listOf(Periodo.DIARIO, Periodo.SEMANAL, Periodo.MENSUAL)
    SelectorPestanas(
        opciones = listOf("Día", "Semana", "Mes"),
        indiceActivo = periodos.indexOf(periodo).coerceAtLeast(0)
    ) { i -> onPeriodo(periodos[i]) }
}

@Composable
private fun Dona(
    ingresos: Double,
    ganancia: Double,
    costo: Double,
    modifier: Modifier = Modifier,
    conImportes: Boolean = false
) {
    GraficaDona(
        titulo = "Distribución",
        totalTexto = dinero(ingresos),
        porciones = listOf(
            PorcionDona("Ganancia", ganancia, MaterialTheme.coloresExtra.correct.color),
            PorcionDona("Costo", costo, MaterialTheme.colorScheme.tertiary)
        ),
        modifier = modifier,
        diametro = if (conImportes) 130.dp else 110.dp,
        // El importe solo donde cabe: en telefono el renglon se cortaria.
        formatearValor = if (conImportes) ({ v -> dinero(v) }) else null
    )
}

/** Las tres cifras repartidas a lo ancho. */
@Composable
private fun RowScope.CifrasEnFila(
    ganancia: Double,
    costo: Double,
    perdidas: Double,
    etiquetasLargas: Boolean
) = CifrasContenido(
    ganancia, costo, perdidas, etiquetasLargas,
    // fillMaxHeight dentro de FilaPareja: "Pérdidas registradas" ocupa dos
    // renglones y las otras una, y las tres deben acabar iguales.
    Modifier.weight(1f).fillMaxHeight()
)

/** Las tres cifras una debajo de otra, cada una a todo el ancho. */
@Composable
private fun ColumnScope.CifrasApiladas(
    ganancia: Double,
    costo: Double,
    perdidas: Double
) = CifrasContenido(ganancia, costo, perdidas, true, Modifier.fillMaxWidth())

@Composable
private fun CifrasContenido(
    ganancia: Double,
    costo: Double,
    perdidas: Double,
    etiquetasLargas: Boolean,
    modificadorTarjeta: Modifier
) {
    TarjetaCifra(
        if (etiquetasLargas) "Ganancia total" else "Ganancia", dinero(ganancia),
        fondo = MaterialTheme.colorScheme.primaryContainer,
        contenido = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modificadorTarjeta
    )
    TarjetaCifra(
        if (etiquetasLargas) "Costo operativo" else "Costo", dinero(costo),
        fondo = MaterialTheme.colorScheme.surfaceContainerHigh,
        contenido = MaterialTheme.colorScheme.onSurfaceVariant,
        colorValor = MaterialTheme.colorScheme.onSurface,
        modifier = modificadorTarjeta
    )
    TarjetaCifra(
        if (etiquetasLargas) "Pérdidas registradas" else "Pérdidas", dinero(perdidas),
        fondo = MaterialTheme.colorScheme.errorContainer,
        contenido = MaterialTheme.colorScheme.error,
        modifier = modificadorTarjeta
    )
}

@Composable
private fun EncabezadoRanking() {
    Text(
        "Productos más vendidos",
        style = MaterialTheme.typography.tituloMedio,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.semantics { heading() }
    )
}
