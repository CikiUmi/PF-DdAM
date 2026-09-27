package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Pastilla
import com.ddam_a1.gestordeinventario.ui.components.TarjetaCifra
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 10 - DETALLE DE PRODUCTO   (Figma 48:1497 / 48:1866 / 48:2221)
//
//  Tres cifras arriba (precio, costo, ganancia), la receta debajo y el boton
//  de producir al final.
//
//  La GANANCIA se calcula aqui y no viene de la base: es precio - costo, y el
//  costo ya se recalcula solo cuando cambia el precio de un material. Si se
//  guardara, el dia que suba la harina la ganancia guardada mentiria.
//
//  En tableta se parte en dos: a la izquierda lo que el producto ES y lo que
//  se puede hacer con el; a la derecha de que esta hecho.
// ============================================================

@Composable
fun PantallaDetalleProducto(
    producto: Producto?,
    costo: Double,
    receta: List<RenglonReceta>,
    onEditar: () -> Unit,
    onProducir: () -> Unit,
    onReceta: () -> Unit,
    onAtras: () -> Unit
) {
    if (producto == null) {
        Marco(barra = { BarraSuperior("Producto", onAtras = onAtras) }) {
            item {
                Text(
                    "Este producto ya no existe.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    BoxWithConstraints {
        if (anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA) {
            ProductoDosColumnas(producto, costo, receta, onEditar, onProducir, onReceta, onAtras)
        } else {
            ProductoUnaColumna(producto, costo, receta, onEditar, onProducir, onReceta, onAtras)
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun ProductoUnaColumna(
    producto: Producto,
    costo: Double,
    receta: List<RenglonReceta>,
    onEditar: () -> Unit,
    onProducir: () -> Unit,
    onReceta: () -> Unit,
    onAtras: () -> Unit
) {
    Marco(barra = {
        BarraSuperior("Detalle de producto", onAtras = onAtras) {
            BotonIcono(Iconos.Editar, "Editar producto", onEditar)
        }
    }) {
        item { EncabezadoProducto(producto) }
        item { CifrasProducto(producto, costo, enFila = true) }
        item { CabeceraReceta(onReceta) }
        item { ListaIngredientes(receta) }
        item { ResumenCosto(costo) }
        item { BotonPrincipal("Registrar producción", onClick = onProducir) }
    }
}

// ---------- TABLETA  (Figma 48:2221) ----------

@Composable
private fun ProductoDosColumnas(
    producto: Producto,
    costo: Double,
    receta: List<RenglonReceta>,
    onEditar: () -> Unit,
    onProducir: () -> Unit,
    onReceta: () -> Unit,
    onAtras: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior("Detalle de producto", onAtras = onAtras) {
                BotonIcono(Iconos.Editar, "Editar producto", onEditar)
            }
            Row(
                Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = Margenes.xl),
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                LazyColumn(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Margenes.xl)
                ) {
                    item { EncabezadoProducto(producto) }
                    item { CifrasProducto(producto, costo, enFila = true) }
                    item { BotonPrincipal("Registrar producción", onClick = onProducir) }
                }

                // La receta va dentro de una tarjeta y no suelta: en 604 de
                // ancho, sin fondo, los renglones se perderian en el blanco.
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(Radios.dialogo))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentPadding = PaddingValues(Margenes.xl),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    item { CabeceraReceta(onReceta) }
                    item { ListaIngredientes(receta) }
                    item { ResumenCosto(costo) }
                }
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

@Composable
private fun EncabezadoProducto(producto: Producto) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct
    val bajo = producto.stockMinimo > 0 && producto.stockDisponible <= producto.stockMinimo

    Column(verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {
        Text(
            producto.nombre,
            style = MaterialTheme.typography.headlineSmall,
            color = cs.onSurface,
            modifier = Modifier.semantics { heading() }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Margenes.sm)) {
            if (producto.esBajoPedido) {
                Pastilla("Bajo pedido", cs.tertiaryContainer, cs.onTertiaryContainer, Iconos.Reloj)
            } else if (bajo) {
                Pastilla(
                    producto.stockDisponible.toString() + " en stock",
                    cs.errorContainer, cs.onErrorContainer, Iconos.Alerta
                )
            } else {
                Pastilla(
                    producto.stockDisponible.toString() + " en stock",
                    correcto.colorContainer, correcto.onColorContainer, Iconos.Check
                )
            }
            val caduca = producto.caducidadMasCercana
            if (!caduca.isNullOrBlank()) {
                Pastilla("Caduca: " + caduca, icono = Iconos.Calendario)
            }
        }
    }
}

/**
 * Precio, costo y ganancia.
 *
 * El porcentaje se calcula sobre el PRECIO (margen), no sobre el costo: es lo
 * que dice el Figma con "$12.50 (50%)" para 25 de precio y 12.50 de costo.
 */
@Composable
private fun CifrasProducto(producto: Producto, costo: Double, enFila: Boolean) {
    val ganancia = producto.precioVenta - costo
    val margen = if (producto.precioVenta > 0.0) (ganancia / producto.precioVenta) * 100.0 else 0.0
    val color =
        if (ganancia >= 0.0) MaterialTheme.coloresExtra.correct.color
        else MaterialTheme.colorScheme.error

    val cs = MaterialTheme.colorScheme
    // Se reusa TarjetaCifra (la de Rendimiento) y no TarjetaMetrica: esta trae
    // el par de colores desde fuera, que es lo que hace falta para pintar la
    // ganancia en verde, y su cifra va en 20 como en el Figma.
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        val tarjeta = Modifier
            .weight(1f)
            .shadow(2.dp, RoundedCornerShape(Radios.campo))
        TarjetaCifra(
            "Precio", dinero(producto.precioVenta),
            cs.surfaceContainerLowest, cs.onSurfaceVariant, tarjeta,
            colorValor = cs.onSurface
        )
        TarjetaCifra(
            "Costo", dinero(costo),
            cs.surfaceContainerLowest, cs.onSurfaceVariant, tarjeta,
            colorValor = cs.onSurface
        )
        TarjetaCifra(
            "Ganancia", dinero(ganancia) + " (" + cant(margen) + "%)",
            cs.surfaceContainerLowest, cs.onSurfaceVariant, tarjeta,
            colorValor = color,
            maxLineas = 2
        )
    }
}

@Composable
private fun CabeceraReceta(onReceta: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Receta",
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            "Editar",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .clip(RoundedCornerShape(Radios.accion))
                .clickable { onReceta() }
                .padding(horizontal = Margenes.md, vertical = Margenes.sm)
        )
    }
}

/**
 * Los ingredientes, en una caja con los renglones pegados.
 *
 * El fondo de la caja asoma entre renglon y renglon (1 de separacion), que es
 * como el Figma dibuja la linea divisoria: sin trazar ninguna.
 */
@Composable
private fun ListaIngredientes(receta: List<RenglonReceta>) {
    if (receta.isEmpty()) {
        Text(
            "Este producto todavía no tiene receta.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        receta.forEachIndexed { i, renglon ->
            val primero = i == 0
            val ultimo = i == receta.size - 1
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = if (primero) 12.dp else 4.dp,
                            topEnd = if (primero) 12.dp else 4.dp,
                            bottomStart = if (ultimo) 12.dp else 4.dp,
                            bottomEnd = if (ultimo) 12.dp else 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(Margenes.md)
                    .semantics(mergeDescendants = true) { },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    renglon.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    cant(renglon.cantidadUsada) + " " + renglon.unidad +
                        " (" + dinero(renglon.cantidadUsada * renglon.costoUnitario) + ")",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ResumenCosto(costo: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Costo total de producción:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            dinero(costo),
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
