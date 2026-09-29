package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.puede
import com.ddam_a1.gestordeinventario.data.negocio.Accion
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.ListaPegada
import com.ddam_a1.gestordeinventario.ui.components.Pastilla
import com.ddam_a1.gestordeinventario.ui.components.TarjetaMetrica
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
        val medida = anchoPantallaDe(maxWidth)
        if (medida == AnchoPantalla.EXPANDIDA) {
            ProductoDosColumnas(producto, costo, receta, onEditar, onProducir, onReceta, onAtras)
        } else {
            ProductoUnaColumna(
                producto, costo, receta, onEditar, onProducir, onReceta, onAtras,
                // Solo en telefono se apilan: de 600 en adelante las tres
                // caben de lado sin cortar ni el importe mas largo.
                apiladas = medida == AnchoPantalla.COMPACTA
            )
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
    onAtras: () -> Unit,
    apiladas: Boolean
) {
    // `puede()` es @Composable y el bloque de una lista perezosa no lo es:
    // el permiso se lee aqui, en el cuerpo de la pantalla, y la lista usa el
    // booleano.
    val puedeEditar = puede(Accion.EDITAR_INVENTARIO)

    Marco(barra = {
        BarraSuperior("Detalle de producto", onAtras = onAtras) {
            if (puedeEditar) {
                BotonIcono(Iconos.Editar, "Editar producto", onEditar)
            }
        }
    }) {
        item { EncabezadoProducto(producto) }
        item { CifrasProducto(producto, costo, receta.size, apiladas) }
        item { CabeceraReceta(onReceta) }
        item { ListaIngredientes(receta) }
        item { ResumenCosto(costo) }
        if (puedeEditar) {
            item { BotonPrincipal("Registrar producción", onClick = onProducir) }
        }
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
    // `puede()` es @Composable y el bloque de una lista perezosa no lo es:
    // el permiso se lee aqui, en el cuerpo de la pantalla, y la lista usa el
    // booleano.
    val puedeEditar = puede(Accion.EDITAR_INVENTARIO)

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            BarraSuperior("Detalle de producto", onAtras = onAtras) {
                if (puedeEditar) {
                    BotonIcono(Iconos.Editar, "Editar producto", onEditar)
                }
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
                    item { CifrasProducto(producto, costo, receta.size, apiladas = false) }
                    if (puedeEditar) {
                        item { BotonPrincipal("Registrar producción", onClick = onProducir) }
                    }
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
private fun CifrasProducto(
    producto: Producto,
    costo: Double,
    materiales: Int,
    apiladas: Boolean
) {
    val ganancia = producto.precioVenta - costo
    val margen = if (producto.precioVenta > 0.0) (ganancia / producto.precioVenta) * 100.0 else 0.0
    val color =
        if (ganancia >= 0.0) MaterialTheme.coloresExtra.correct.color
        else MaterialTheme.colorScheme.error

    val cs = MaterialTheme.colorScheme

    // El porcentaje baja a la nota en vez de ir pegado al importe: asi la
    // cifra grande es SOLO dinero y se puede comparar de un vistazo con las
    // otras dos, que tambien son dinero.
    val notaMargen = cant(margen) + "% margen"

    // De donde sale el costo. Es el unico dato de esta pantalla que explica
    // esa cifra; para "Precio" no hay ninguno que aporte, y una nota puesta
    // por rellenar seria ruido.
    val notaCosto = when (materiales) {
        0 -> "Sin receta"
        1 -> "1 material"
        else -> materiales.toString() + " materiales"
    }

    // ============================================================
    //  UN IMPORTE NO SE PUEDE CORTAR
    //
    //  Tres tarjetas de lado en un telefono dan unos 116 de ancho cada una.
    //  Con precios de negocio de verdad ("$333,333.33") eso se convierte en
    //  "$333,33...", y un dinero a medias no es un dato: es un error de
    //  lectura esperando a pasar.
    //
    //  Asi que en telefono las tres se apilan como renglones, etiqueta a la
    //  izquierda e importe a la derecha, con todo el ancho para el numero. De
    //  600 en adelante vuelven a las tres columnas del Figma, que ahi si
    //  caben, con dos renglones permitidos por si el importe es largo.
    // ============================================================
    if (apiladas) {
        Column(verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {
            CifraEnRenglon(
                "Precio", dinero(producto.precioVenta),
                acento = cs.tertiary, colorValor = cs.onSurface
            )
            CifraEnRenglon(
                "Costo", dinero(costo),
                acento = cs.outlineVariant, colorValor = cs.onSurface,
                nota = notaCosto, colorNota = cs.onSurfaceVariant
            )
            CifraEnRenglon(
                "Ganancia", dinero(ganancia),
                acento = color, colorValor = color,
                nota = notaMargen, colorNota = color
            )
        }
        return
    }

    // TarjetaMetrica y no TarjetaCifra: es la que trae la rayita de color
    // arriba y el renglon de nota abajo. Va en su version `compacta`, con la
    // cifra en 20 en vez de 28, porque tres de 28 no caben de lado.
    //
    // FilaPareja: si una nota ocupa un renglon y otra ninguno, tres tarjetas
    // de alturas distintas leen como un grupo roto.
    FilaPareja(separacion = Margenes.sm) {
        val tarjeta = Modifier.weight(1f).fillMaxHeight()
        TarjetaMetrica(
            "Precio", dinero(producto.precioVenta), null,
            colorAcento = cs.tertiary,
            modifier = tarjeta,
            compacta = true, maxLineasCifra = 2
        )
        TarjetaMetrica(
            "Costo", dinero(costo), notaCosto,
            colorAcento = cs.outlineVariant,
            modifier = tarjeta,
            colorNota = cs.onSurfaceVariant,
            compacta = true, maxLineasCifra = 2
        )
        TarjetaMetrica(
            "Ganancia", dinero(ganancia), notaMargen,
            colorAcento = color,
            modifier = tarjeta,
            colorNota = color,
            compacta = true, maxLineasCifra = 2
        )
    }
}

/**
 * La misma tarjeta, tumbada: rayita y etiqueta a la izquierda, importe a la
 * derecha.
 *
 * El importe NO lleva maxLines: si hace falta, que baje de renglon. Cortarlo
 * seria justo lo que se esta evitando al apilarlas.
 */
@Composable
private fun CifraEnRenglon(
    etiqueta: String,
    valor: String,
    acento: Color,
    colorValor: Color,
    nota: String? = null,
    colorNota: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(Radios.campo))
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = Margenes.lg, vertical = Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            Box(
                Modifier
                    .clearAndSetSemantics { }   // decorativo
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(acento)
            )
            Text(
                etiqueta,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (nota != null) {
                Text(
                    nota,
                    style = MaterialTheme.typography.bodyMedium
                        .copy(fontWeight = FontWeight.SemiBold),
                    color = colorNota
                )
            }
        }
        Text(
            valor,
            style = MaterialTheme.typography.tituloMedio,
            color = colorValor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
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

    ListaPegada(receta.size) { i ->
        val renglon = receta[i]
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
