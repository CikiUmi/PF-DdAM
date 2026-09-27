package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.components.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BotonFlotante
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.PanelLateral
import com.ddam_a1.gestordeinventario.ui.components.TarjetaProductoRejilla
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe

private enum class FiltroCat(val etiqueta: String) {
    TODOS("Todos"), CON_STOCK("Con stock"), BAJO_PEDIDO("Bajo pedido")
}

// ============================================================
//  PANTALLA 9 - CATALOGO   (Figma 48:1385 / 48:1741 / 48:2089)
//
//  Rejilla de productos. Las columnas salen del Figma y no de una formula:
//    COMPACTA   2   (tarjetas de ~180)
//    MEDIA      3
//    EXPANDIDA  4   (tarjetas de 224 sobre 944, con el panel lateral)
//
//  Se arma con `chunked` sobre el LazyColumn que ya usa Marco, y no con
//  LazyVerticalGrid, para que la pantalla siga teniendo un solo modo de
//  desplazamiento y pueda usar el hueco del boton flotante.
// ============================================================

@Composable
fun PantallaCatalogo(
    productos: List<Producto>,
    costos: Map<String, Double>,
    onProducto: (String) -> Unit,
    onNuevoProducto: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroCat.TODOS) }

    val encontrados =
        if (texto.isBlank()) productos
        else productos.filter { it.nombre.contains(texto, ignoreCase = true) }

    val lista = when (filtro) {
        FiltroCat.TODOS -> encontrados
        FiltroCat.CON_STOCK -> encontrados.filter { !it.esBajoPedido }
        FiltroCat.BAJO_PEDIDO -> encontrados.filter { it.esBajoPedido }
    }

    BoxWithConstraints {
        val medida = anchoPantallaDe(maxWidth)
        if (medida == AnchoPantalla.EXPANDIDA) {
            CatalogoConPanel(lista, costos, texto, { texto = it }, filtro, { filtro = it },
                onProducto, onNuevoProducto, onDestino)
        } else {
            CatalogoConBarra(lista, costos, texto, { texto = it }, filtro, { filtro = it },
                onProducto, onNuevoProducto, onDestino,
                columnas = if (medida == AnchoPantalla.MEDIA) 3 else 2)
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun CatalogoConBarra(
    lista: List<Producto>,
    costos: Map<String, Double>,
    texto: String,
    onTexto: (String) -> Unit,
    filtro: FiltroCat,
    onFiltro: (FiltroCat) -> Unit,
    onProducto: (String) -> Unit,
    onNuevoProducto: () -> Unit,
    onDestino: (DestinoBarra) -> Unit,
    columnas: Int
) {
    Marco(
        barra = { TituloCatalogo() },
        pie = { BarraInferior(DestinoBarra.CATALOGO, onDestino) },
        flotante = { BotonFlotante(Iconos.Agregar, "Nuevo producto", onClick = onNuevoProducto) }
    ) {
        item { BarraBusqueda(texto, "Buscar producto...", onTexto) }
        item { FilaFiltrosCatalogo(filtro, onFiltro) }
        rejillaProductos(lista, costos, columnas, onProducto)
    }
}

// ---------- TABLETA  (Figma 48:2089) ----------

@Composable
private fun CatalogoConPanel(
    lista: List<Producto>,
    costos: Map<String, Double>,
    texto: String,
    onTexto: (String) -> Unit,
    filtro: FiltroCat,
    onFiltro: (FiltroCat) -> Unit,
    onProducto: (String) -> Unit,
    onNuevoProducto: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Row(Modifier.fillMaxSize().statusBarsPadding()) {
            PanelLateral(DestinoBarra.CATALOGO, onDestino)

            Box(Modifier.weight(1f).fillMaxSize()) {
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = Margenes.xl),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    // Titulo y busqueda en el mismo renglon: en 944 de ancho la
                    // barra sola se estiraria de lado a lado sin necesidad.
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Margenes.xl)
                    ) {
                        Text(
                            "Catálogo",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f).semantics { heading() }
                        )
                        Box(Modifier.width(380.dp)) {
                            BarraBusqueda(texto, "Buscar producto...", onTexto)
                        }
                    }

                    FilaFiltrosCatalogo(filtro, onFiltro)

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Margenes.lg),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        rejillaProductos(lista, costos, 4, onProducto)
                    }
                }
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 40.dp, bottom = Margenes.xl)
                ) { BotonFlotante(Iconos.Agregar, "Nuevo producto", onClick = onNuevoProducto) }
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

@Composable
private fun TituloCatalogo() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = Margenes.pantalla, vertical = Margenes.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Catálogo",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() }
        )
    }
}

@Composable
private fun FilaFiltrosCatalogo(filtro: FiltroCat, onFiltro: (FiltroCat) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Margenes.sm)) {
        FiltroCat.entries.forEach { f ->
            ChipFiltro(f.etiqueta, filtro == f) { onFiltro(f) }
        }
    }
}

/**
 * La rejilla, compartida entre las tres medidas.
 *
 * `chunked` en vez de una rejilla perezosa: son decenas de productos, no
 * miles, y asi la pantalla entera sigue siendo un solo LazyColumn.
 */
private fun LazyListScope.rejillaProductos(
    lista: List<Producto>,
    costos: Map<String, Double>,
    columnas: Int,
    onProducto: (String) -> Unit
) {
    if (lista.isEmpty()) {
        item { EstadoVacio("Sin productos", "Los productos que registres aparecerán aquí") }
        return
    }

    val renglones = lista.chunked(columnas)
    items(renglones.size) { i ->
        // Los nombres largos ocupan dos renglones y los cortos uno: sin esto,
        // las tarjetas de un mismo renglon acabarian a alturas distintas.
        FilaPareja {
            renglones[i].forEach { p ->
                Box(Modifier.weight(1f).fillMaxHeight()) {
                    TarjetaProductoRejilla(
                        modifier = Modifier.fillMaxHeight(),
                        nombre = p.nombre,
                        costo = dinero(costos[p.id] ?: p.costoProduccion),
                        venta = dinero(p.precioVenta),
                        esBajoPedido = p.esBajoPedido,
                        stock = p.stockDisponible,
                        stockBajo = p.stockMinimo > 0 && p.stockDisponible <= p.stockMinimo,
                        onClick = { onProducto(p.id) }
                    )
                }
            }
            // Relleno del ultimo renglon incompleto: sin esto, una sola
            // tarjeta se estiraria a lo ancho de todas las columnas.
            repeat(columnas - renglones[i].size) {
                Box(Modifier.weight(1f)) {}
            }
        }
    }
}
