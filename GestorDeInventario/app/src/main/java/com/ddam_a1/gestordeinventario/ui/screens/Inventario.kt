package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraBusqueda
import com.ddam_a1.gestordeinventario.data.negocio.Accion
import com.ddam_a1.gestordeinventario.ui.puede
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonFlotante
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.FilaChips
import com.ddam_a1.gestordeinventario.ui.diasHasta
import com.ddam_a1.gestordeinventario.ui.components.PastillaEstado
import com.ddam_a1.gestordeinventario.ui.components.EstadoInventario
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.PanelLateral
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

private enum class FiltroInv(val etiqueta: String) {
    TODOS("Todos"), BAJOS("Stock bajo"), CADUCAN("Caducan")
}

// ============================================================
//  PANTALLA 6 - INVENTARIO   (Figma 43:690 / 43:973 / 43:1233)
//
//  Recibe la lista ya hecha. `esStockBajo` llega como funcion para que la
//  regla viva en un solo sitio y la pantalla solo la consulte.
//
//  Las tres medidas cambian COMO se ve la lista, no que hay en ella:
//    COMPACTA   una tarjeta por renglon
//    MEDIA      dos columnas de tarjetas: en 700 una sola columna deja
//               la mitad del ancho en blanco
//    EXPANDIDA  tabla con encabezados y navegacion lateral. Con 944 de ancho
//               se pueden alinear las columnas y comparar de un vistazo
// ============================================================

@Composable
fun PantallaInventario(
    materiales: List<Material>,
    esStockBajo: (Material) -> Boolean,
    onMaterial: (String) -> Unit,
    onNuevoMaterial: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    // Busqueda y filtro SI son estado de esta pantalla: nadie mas los
    // necesita y no sobreviven a salir de aqui.
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroInv.TODOS) }

    // Filtrar en memoria es correcto con las decenas de materiales de un
    // negocio chico. Con miles, esto se volveria un WHERE en el DAO.
    val encontrados =
        if (texto.isBlank()) materiales
        else materiales.filter { it.nombre.contains(texto, ignoreCase = true) }

    val lista = when (filtro) {
        FiltroInv.TODOS -> encontrados
        FiltroInv.BAJOS -> encontrados.filter { esStockBajo(it) }
        FiltroInv.CADUCAN -> encontrados.filter { it.lotes.isNotEmpty() }
    }

    BoxWithConstraints {
        when (anchoPantallaDe(maxWidth)) {
            AnchoPantalla.EXPANDIDA -> InventarioTabla(
                lista, esStockBajo, texto, { texto = it }, filtro, { filtro = it },
                onMaterial, onNuevoMaterial, onDestino
            )
            else -> InventarioLista(
                lista, esStockBajo, texto, { texto = it }, filtro, { filtro = it },
                onMaterial, onNuevoMaterial, onDestino,
                enDosColumnas = anchoPantallaDe(maxWidth) == AnchoPantalla.MEDIA
            )
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun InventarioLista(
    lista: List<Material>,
    esStockBajo: (Material) -> Boolean,
    texto: String,
    onTexto: (String) -> Unit,
    filtro: FiltroInv,
    onFiltro: (FiltroInv) -> Unit,
    onMaterial: (String) -> Unit,
    onNuevoMaterial: () -> Unit,
    onDestino: (DestinoBarra) -> Unit,
    enDosColumnas: Boolean
) {
    Marco(
        barra = { BarraSuperior("Inventario") },
        pie = { BarraInferior(DestinoBarra.INVENTARIO, onDestino) },
        // Sin permiso de editar el inventario, el boton no se pinta.
        flotante = {
            if (puede(Accion.EDITAR_INVENTARIO)) {
                BotonFlotante(Iconos.Agregar, "Nuevo material", onClick = onNuevoMaterial)
            }
        }
    ) {
        item { BarraBusqueda(texto, "Buscar material...", onTexto) }
        item { FilaFiltros(filtro, onFiltro) }

        if (lista.isEmpty()) {
            item { EstadoVacio("Sin materiales", "Los materiales registrados aparecerán aquí") }
        } else if (enDosColumnas) {
            // De dos en dos. `chunked` en vez de una rejilla perezosa para no
            // traer otra dependencia por una pantalla.
            val parejas = lista.chunked(2)
            items(parejas.size) { i ->
                // Una tarjeta con aviso de stock bajo es mas alta que una sin
                // el: FilaPareja las deja todas al alto de la mayor.
                FilaPareja(separacion = Margenes.lg) {
                    parejas[i].forEach { m ->
                        Box(Modifier.weight(1f).fillMaxHeight()) {
                            MaterialTarjeta(m, esStockBajo, onMaterial)
                        }
                    }
                    // Relleno cuando el ultimo renglon trae uno solo: sin esto
                    // esa tarjeta se estiraria al doble de ancho.
                    if (parejas[i].size == 1) Box(Modifier.weight(1f)) {}
                }
            }
        } else {
            items(lista.size) { i -> MaterialTarjeta(lista[i], esStockBajo, onMaterial) }
        }
    }
}

// ---------- TABLETA  (Figma 43:1233) ----------

@Composable
private fun InventarioTabla(
    lista: List<Material>,
    esStockBajo: (Material) -> Boolean,
    texto: String,
    onTexto: (String) -> Unit,
    filtro: FiltroInv,
    onFiltro: (FiltroInv) -> Unit,
    onMaterial: (String) -> Unit,
    onNuevoMaterial: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Row(Modifier.fillMaxSize().systemBarsPadding()) {
            PanelLateral(DestinoBarra.INVENTARIO, onDestino)

            Box(Modifier.weight(1f).fillMaxSize()) {
                // Todo dentro de la lista, encabezado incluido: con el
                // encabezado fijo y la lista debajo, una ventana baja deja a
                // la lista sin alto y la pantalla entera se queda sin
                // desplazamiento. Asi se desplaza completa, como en telefono.
                LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = Margenes.xl),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    // Titulo y busqueda comparten renglon: en tableta la barra
                    // de busqueda sola desperdiciaria toda una franja.
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Margenes.xl)
                        ) {
                            Text(
                                "Inventario",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f).semantics { heading() }
                            )
                            Box(Modifier.width(380.dp)) {
                                BarraBusqueda(texto, "Buscar material...", onTexto)
                            }
                        }
                    }

                    item { FilaFiltros(filtro, onFiltro) }

                    if (lista.isEmpty()) {
                        item {
                            EstadoVacio(
                                "Sin materiales",
                                "Los materiales registrados aparecerán aquí"
                            )
                        }
                    } else {
                        item { EncabezadoTabla() }
                        // Los renglones quedan a 16 y no a 8 como antes: es
                        // la separacion de la lista, que ahora los incluye. Se
                        // ve un poco mas aireado y es un cambio a proposito.
                        items(lista.size) { i ->
                            RenglonTabla(lista[i], esStockBajo, onMaterial)
                        }
                    }
                }
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 40.dp, bottom = Margenes.xl)
                ) {
                    if (puede(Accion.EDITAR_INVENTARIO)) {
                        BotonFlotante(Iconos.Agregar, "Nuevo material", onClick = onNuevoMaterial)
                    }
                }
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

@Composable
private fun FilaFiltros(filtro: FiltroInv, onFiltro: (FiltroInv) -> Unit) {
    FilaChips {
        FiltroInv.entries.forEach { f ->
            ChipFiltro(f.etiqueta, filtro == f) { onFiltro(f) }
        }
    }
}

// ============================================================
//  QUE LE PASA A UN MATERIAL
//
//  El mismo calculo que usan los avisos y las notificaciones. Se escribe una
//  vez y lo comparten la tarjeta del telefono y el renglon de la tableta: si
//  cada uno decidiera por su cuenta, una pantalla podria ensenar en rosa lo
//  que la otra ensena en rojo.
//
//  Un material puede tener VARIAS cosas a la vez —estar bajo de stock y ademas
//  con un lote vencido— y entonces salen las dos pastillas. Esconder una para
//  que la fila se vea limpia seria esconderle medio problema al usuario.
// ============================================================

private fun estadosDe(m: Material, bajo: Boolean): List<EstadoInventario> {
    val estados = mutableListOf<EstadoInventario>()
    if (bajo) estados.add(EstadoInventario.STOCK_BAJO)
    if (m.diasAvisoCaducidad > 0) {
        val dias = m.lotes.mapNotNull { diasHasta(it.caducidad) }
        if (dias.any { it < 0 }) estados.add(EstadoInventario.CADUCADO)
        else if (dias.any { it <= m.diasAvisoCaducidad }) estados.add(EstadoInventario.POR_CADUCAR)
    }
    return estados
}

/** El subtitulo de una tarjeta: precio unitario y, si toca, las pastillas. */
@Composable
private fun MaterialTarjeta(
    m: Material,
    esStockBajo: (Material) -> Boolean,
    onMaterial: (String) -> Unit
) {
    val estados = estadosDe(m, esStockBajo(m))
    FilaLista(
        modifier = Modifier.fillMaxHeight(),
        titulo = m.nombre,
        subtitulo = dinero(m.costoUnitario) + " / " + m.unidadMedida,
        valor = cant(m.cantidadDisponible) + " " + m.unidadMedida,
        pastillas = if (estados.isEmpty()) null
        else ({ estados.forEach { PastillaEstado(it) } })
    ) { onMaterial(m.id) }
}

// Los pesos de las columnas salen del Figma (296/200/250/150 sobre 944) y se
// comparten entre el encabezado y los renglones: si cambian, cambian en los
// dos sitios a la vez y nunca se descuadran.
private const val COL_MATERIAL = 296f
private const val COL_COSTO = 200f
private const val COL_ESTADO = 250f
private const val COL_STOCK = 150f

@Composable
private fun EncabezadoTabla() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = Margenes.lg, vertical = Margenes.md),
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        listOf(
            "Material" to COL_MATERIAL,
            "Costo unitario" to COL_COSTO,
            "Alertas / Estado" to COL_ESTADO,
            "Total en stock" to COL_STOCK
        ).forEach { (titulo, peso) ->
            Text(
                titulo,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(peso)
            )
        }
    }
}

@Composable
private fun RenglonTabla(
    m: Material,
    esStockBajo: (Material) -> Boolean,
    onMaterial: (String) -> Unit
) {
    val bajo = esStockBajo(m)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onMaterial(m.id) }
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Text(
            m.nombre,
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(COL_MATERIAL)
        )
        Text(
            dinero(m.costoUnitario) + " / " + m.unidadMedida,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(COL_COSTO)
        )
        Row(
            Modifier.weight(COL_ESTADO),
            horizontalArrangement = Arrangement.spacedBy(Margenes.xs)
        ) {
            val estados = estadosDe(m, bajo)
            if (estados.isEmpty()) {
                // En la tabla la columna existe siempre, asi que cuando no hay
                // nada que decir se dice que no hay nada: una celda en blanco
                // se lee como un dato que falta.
                Text(
                    "En orden",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                estados.forEach { PastillaEstado(it) }
            }
        }
        Text(
            cant(m.cantidadDisponible) + " " + m.unidadMedida,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(COL_STOCK)
        )
    }
}
