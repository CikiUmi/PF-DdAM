package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.SinResultados
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.components.BannerAviso
import com.ddam_a1.gestordeinventario.ui.components.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Pastilla
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 14 - NUEVA VENTA   (Figma 51:1384 / 51:1642 / 52:1474)
//  RF12, RF13, RF14
//
//  El ticket es estado de ESTA pantalla: mientras lo armas no existe para
//  nadie mas, y si te sales se tira. Solo al confirmar se entrega completo.
//
//  `error` llega de afuera porque es la respuesta a la operacion: la pantalla
//  no sabe si fallo por materiales o por stock, solo lo muestra.
//
//  En tableta se parte en dos (Figma 52:1487): a la izquierda el catalogo del
//  que eliges, a la derecha el ticket que llevas. Son las dos preguntas que
//  tiene quien esta cobrando, y en 1280 caben las dos a la vez.
// ============================================================

@Composable
fun PantallaNuevaVenta(
    productos: List<Producto>,
    error: String?,
    onDescartarError: () -> Unit,
    onConfirmar: (Map<String, Int>) -> Unit,
    onAtras: () -> Unit
) {
    val ticket = remember { mutableStateMapOf<String, Int>() }
    var buscar by remember { mutableStateOf("") }

    val encontrados =
        if (buscar.isBlank()) productos
        else productos.filter { it.nombre.contains(buscar, ignoreCase = true) }

    // Sumar el ticket es aritmetica para mostrar: depende de lo que el usuario
    // acaba de tocar, no de la base.
    var total = 0.0
    for (entrada in ticket) {
        val producto = productos.find { it.id == entrada.key }
        total += (producto?.precioVenta ?: 0.0) * entrada.value
    }
    val piezas = ticket.values.sum()

    // El tipo va ESCRITO y no inferido a proposito. `ticket.remove(id)`
    // devuelve el valor que quito (Int?), asi que el if/else acababa siendo
    // de tipo Any? y la lambda entera `(String) -> Any?`, que no encaja donde
    // se espera `(String) -> Unit`. Con el tipo puesto, el valor se descarta.
    val sumar: (String) -> Unit = { id -> ticket[id] = (ticket[id] ?: 0) + 1 }
    val restar: (String) -> Unit = { id ->
        val n = ticket[id] ?: 0
        // Llegar a cero SACA el producto del ticket en vez de dejarlo en 0:
        // el ticket son las cosas que se llevan, y cero de algo no es nada.
        if (n > 1) ticket[id] = n - 1 else ticket.remove(id)
    }

    BoxWithConstraints {
        if (anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA) {
            VentaDosColumnas(
                encontrados, productos, ticket, buscar, { buscar = it },
                error, onDescartarError, sumar, restar,
                total, piezas, onConfirmar, onAtras
            )
        } else {
            VentaUnaColumna(
                encontrados, ticket, buscar, { buscar = it },
                error, onDescartarError, sumar, restar,
                total, piezas, onConfirmar, onAtras
            )
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun VentaUnaColumna(
    encontrados: List<Producto>,
    ticket: MutableMap<String, Int>,
    buscar: String,
    onBuscar: (String) -> Unit,
    error: String?,
    onDescartarError: () -> Unit,
    sumar: (String) -> Unit,
    restar: (String) -> Unit,
    total: Double,
    piezas: Int,
    onConfirmar: (Map<String, Int>) -> Unit,
    onAtras: () -> Unit
) {
    Marco(barra = { BarraSuperior("Nueva venta", onAtras = onAtras) }) {
        if (error != null) {
            // El banner ya es de error por dentro: el titulo aparte sobraba, el
            // motivo solo se explica mejor.
            item { BannerAviso(error, onClick = onDescartarError) }
        }
        item { BarraBusqueda(buscar, "Buscar producto...", onBuscar) }
        listaDeProductos(encontrados, ticket, buscar, { onBuscar("") }, sumar, restar)

        item { ResumenTicket(ticket.size, piezas, total) }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Margenes.md)
            ) {
                Box(Modifier.weight(1f)) { BotonSecundario("Cancelar") { onAtras() } }
                Box(Modifier.weight(1f)) {
                    BotonPrincipal("Confirmar venta", habilitado = ticket.isNotEmpty()) {
                        onConfirmar(ticket.toMap())
                    }
                }
            }
        }
    }
}

// ---------- TABLETA  (Figma 52:1474) ----------

@Composable
private fun VentaDosColumnas(
    encontrados: List<Producto>,
    todos: List<Producto>,
    ticket: MutableMap<String, Int>,
    buscar: String,
    onBuscar: (String) -> Unit,
    error: String?,
    onDescartarError: () -> Unit,
    sumar: (String) -> Unit,
    restar: (String) -> Unit,
    total: Double,
    piezas: Int,
    onConfirmar: (Map<String, Int>) -> Unit,
    onAtras: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            BarraSuperior("Nueva venta", onAtras = onAtras)
            if (error != null) {
                Box(Modifier.padding(horizontal = 40.dp, vertical = Margenes.sm)) {
                    BannerAviso(error, onClick = onDescartarError)
                }
            }
            Row(
                Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = Margenes.xl),
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // Izquierda: de donde eliges.
                LazyColumn(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Margenes.md),
                    contentPadding = PaddingValues(bottom = Margenes.xl)
                ) {
                    item { BarraBusqueda(buscar, "Buscar producto...", onBuscar) }
                    listaDeProductos(
                        encontrados, ticket, buscar, { onBuscar("") }, sumar, restar
                    )
                }

                // Derecha: lo que llevas. Se queda quieto mientras eliges, que
                // es justo lo que se quiere comprobar antes de cobrar.
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    Text(
                        "Resumen del pedido",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { heading() }
                    )

                    if (ticket.isEmpty()) {
                        EstadoVacio(
                            "Ticket vacío",
                            "Agregue productos de la izquierda para armar la venta"
                        )
                    } else {
                        LazyColumn(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(Margenes.sm)
                        ) {
                            val enTicket = ticket.keys.toList()
                            items(enTicket.size) { i ->
                                val id = enTicket[i]
                                val producto = todos.find { it.id == id }
                                RenglonTicket(
                                    nombre = producto?.nombre ?: "Producto",
                                    cantidad = ticket[id] ?: 0,
                                    importe = (producto?.precioVenta ?: 0.0) * (ticket[id] ?: 0)
                                )
                            }
                        }
                    }

                    ResumenTicket(ticket.size, piezas, total)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
                    ) {
                        Box(Modifier.weight(1f)) { BotonSecundario("Cancelar") { onAtras() } }
                        Box(Modifier.weight(1f)) {
                            BotonPrincipal("Confirmar venta", habilitado = ticket.isNotEmpty()) {
                                onConfirmar(ticket.toMap())
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

/** El catalogo con su contador. Igual en las tres medidas. */
private fun LazyListScope.listaDeProductos(
    encontrados: List<Producto>,
    ticket: Map<String, Int>,
    busqueda: String,
    onLimpiarBusqueda: () -> Unit,
    sumar: (String) -> Unit,
    restar: (String) -> Unit
) {
    if (encontrados.isEmpty()) {
        item {
            // Buscar algo que no existe no es lo mismo que no tener catalogo:
            // el segundo mensaje manda a registrar productos que quiza ya
            // estan, solo que no se llaman asi.
            if (busqueda.isNotBlank()) SinResultados(busqueda, onLimpiarBusqueda)
            else EstadoVacio(
                "Sin productos", "Registre productos en el catálogo para poder vender"
            )
        }
        return
    }
    items(encontrados.size) { i ->
        val producto = encontrados[i]
        FilaProductoVenta(
            producto = producto,
            cantidad = ticket[producto.id] ?: 0,
            onMenos = { restar(producto.id) },
            onMas = { sumar(producto.id) }
        )
    }
}

/**
 * Un producto que se puede agregar al ticket (Figma 51:1402).
 *
 * La pastilla del stock usa el mismo verde que el catalogo, y se pone en rojo
 * cuando ya no queda: el numero sigue ahi, que es lo que hace falta saber
 * antes de seguir sumando piezas.
 */
@Composable
private fun FilaProductoVenta(
    producto: Producto,
    cantidad: Int,
    onMenos: () -> Unit,
    onMas: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct
    val sinStock = !producto.esBajoPedido && producto.stockDisponible <= 0

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(cs.surfaceContainerHigh)
            .padding(Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        // LA PASTILLA, EN SU PROPIO RENGLON Y ARRIBA DE TODO.
        //
        // Antes compartia renglon con el precio, y ese renglon compartia ancho
        // con los botones de mas y menos: a "Bajo pedido" le quedaban unos 90
        // y se partia en dos ("Bajo / pedido"), empujando la tarjeta hacia
        // abajo y dejando cada una de un alto distinto.
        //
        // Arriba y no abajo porque es lo que decide si este renglon se puede
        // tocar: antes de sumar piezas hay que saber si hay de donde. Puesta
        // debajo del precio se lee DESPUES de haber decidido.
        if (producto.esBajoPedido) {
            Pastilla("Bajo pedido", cs.tertiaryContainer, cs.onTertiaryContainer, Iconos.Reloj)
        } else if (sinStock) {
            Pastilla("Sin stock", cs.errorContainer, cs.onErrorContainer, Iconos.Alerta)
        } else {
            Pastilla(
                producto.stockDisponible.toString() + " en stock",
                correcto.colorContainer, correcto.onColorContainer, Iconos.Check
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.tituloMedio,
                    color = cs.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    dinero(producto.precioVenta),
                    style = MaterialTheme.typography.bodyLarge,
                    color = cs.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Contador(cantidad, onMenos = onMenos, onMas = onMas)
        }
    }
}

/** Un renglon del ticket, en la columna derecha de la tableta. */
@Composable
private fun RenglonTicket(nombre: String, cantidad: Int, importe: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Text(
            nombre + " × " + cantidad,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            dinero(importe),
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** La tarjeta azul del total (Figma 51:1455). */
@Composable
private fun ResumenTicket(productos: Int, piezas: Int, total: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Text(
            productos.toString() + (if (productos == 1) " producto · " else " productos · ") +
                piezas + (if (piezas == 1) " pieza" else " piezas"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        Text(
            "Total: " + dinero(total),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
