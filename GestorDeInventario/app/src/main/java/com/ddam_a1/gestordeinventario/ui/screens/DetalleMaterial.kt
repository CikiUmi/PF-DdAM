package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.LoteMaterial
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.diasHasta
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.components.HojaInferior
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Pastilla
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions

// ============================================================
//  PANTALLA 7 - DETALLE DE MATERIAL   (Figma 43:786 / 43:1071 / 43:1331)
//  RF3, RF9, RF18, RF19
//
//  `material` puede ser null mientras el NavHost todavia lo esta buscando, o
//  si lo borraron desde otra pantalla. La decision de que hacer en ese caso es
//  de quien navega, no de la pantalla: aqui solo se dibuja un hueco.
//
//  La entrada de inventario ya NO vive dentro de la pantalla: sale en una hoja
//  (pantalla 07b). Asi el detalle se lee de un vistazo y el formulario aparece
//  solo cuando se va a usar.
// ============================================================

@Composable
fun PantallaDetalleMaterial(
    material: Material?,
    bajo: Boolean,
    usadoEn: List<UsoEnProducto>,
    onEntrada: (cantidad: Double, caducidad: String?) -> Unit,
    onProducto: (String) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onEliminarLote: (LoteMaterial) -> Unit,
    onAtras: () -> Unit
) {
    if (material == null) {
        Marco(barra = { BarraSuperior("Material", onAtras = onAtras) }) {
            item {
                Text(
                    "Este material ya no existe.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var hoja by remember { mutableStateOf(false) }
    var confirmarBorrado by remember { mutableStateOf(false) }
    // El lote que el usuario deslizo y todavia no confirma. Null es "ninguno".
    var loteABorrar by remember { mutableStateOf<LoteMaterial?>(null) }
    var entrada by remember { mutableStateOf("0") }
    var nuevaFecha by remember { mutableStateOf("") }
    // El campo arranca en "0", que es invalido. Sin esto la hoja se abriria
    // con el campo ya en rojo, regañando por algo que el usuario no ha hecho.
    var cantidadTocada by remember { mutableStateOf(false) }

    // "Caduca" no es una pregunta suelta: es lo que se dijo al darlo de alta.
    val caduca = material.diasAvisoCaducidad > 0

    // Los campos se limpian al ABRIR y no al cerrar: si se cierra tocando
    // fuera, al volver a entrar no aparece lo que se escribio la vez pasada.
    val abrirHoja = {
        entrada = "0"
        nuevaFecha = ""
        cantidadTocada = false
        hoja = true
    }

    Box(Modifier.fillMaxSize()) {
        BoxWithConstraints {
            if (anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA) {
                DetalleDosColumnas(
                    material, bajo, usadoEn, onProducto, onEditar,
                    { confirmarBorrado = true }, onAtras, abrirHoja,
                    { lote -> loteABorrar = lote }
                )
            } else {
                DetalleUnaColumna(
                    material, bajo, usadoEn, onProducto, onEditar,
                    { confirmarBorrado = true }, onAtras, abrirHoja,
                    { lote -> loteABorrar = lote }
                )
            }
        }

        // ---------- PANTALLA 07b: la hoja de entrada ----------
        if (hoja) {
            // Estrictamente MAYOR que cero: "0", "0.0" y vacio no son una
            // entrada. "0.5" si: media unidad es media unidad.
            val cantidadValida = (entrada.toDoubleOrNull() ?: 0.0) > 0.0
            // Si el material caduca, el lote NO entra sin su fecha.
            val listo = cantidadValida && (!caduca || nuevaFecha.isNotBlank())

            HojaInferior("Entrada de inventario", { hoja = false }) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Margenes.md)
                ) {
                    Box(Modifier.weight(1f)) {
                        CampoTexto(
                            entrada, "Cantidad",
                            { entrada = it; cantidadTocada = true },
                            soloNumeros = true, sufijo = material.unidadMedida,
                            // El boton apagado dice que no se puede; esto dice
                            // por que. Solo despues de escribir, no al abrir.
                            error = if (cantidadTocada && !cantidadValida)
                                "Debe ser mayor que 0" else null
                        )
                    }
                    if (caduca) {
                        Box(Modifier.weight(1f)) {
                            // La fecha se teclea en formato ISO a proposito: asi
                            // ordenar alfabeticamente ya es ordenar por fecha, y
                            // por eso `lotes.caducidad` es texto y no necesita
                            // convertidor. El marcador ensena el formato.
                            CampoTexto(
                                nuevaFecha, "Caduca el", { nuevaFecha = it },
                                marcador = "aaaa-mm-dd"
                            )
                        }
                    }
                }
                BotonPrincipal("Registrar entrada", habilitado = listo) {
                    onEntrada(entrada.toDoubleOrNull() ?: 0.0, if (caduca) nuevaFecha else null)
                    hoja = false
                }
            }
        }

        // Deslizar no borra por si solo: abre esta pregunta. Asi un deslizado
        // sin querer no se lleva mercancia, y la fila vuelve a su sitio sola.
        val lote = loteABorrar
        if (lote != null) {
            DialogoSiNo(
                titulo = "Eliminar lote",
                mensaje = "Se dará de baja este lote" +
                    (if (lote.cantidad > 0.0)
                        " y se descontarán " + cant(lote.cantidad) + " " + material.unidadMedida +
                            " de la existencia."
                    else ".") +
                    " Esto no se puede deshacer.",
                textoSi = "Eliminar",
                textoNo = "Cancelar",
                onSi = {
                    loteABorrar = null
                    onEliminarLote(lote)
                },
                onNo = { loteABorrar = null },
                onCerrar = { loteABorrar = null },
                destructivo = true
            )
        }

        if (confirmarBorrado) {
            DialogoSiNo(
                titulo = "Eliminar material",
                mensaje = "Se eliminará \"" + material.nombre + "\" y sus lotes. " +
                    "Las recetas que lo usaban quedarán incompletas.",
                textoSi = "Eliminar",
                textoNo = "Cancelar",
                onSi = {
                    confirmarBorrado = false
                    onEliminar()
                },
                onNo = { confirmarBorrado = false },
                onCerrar = { confirmarBorrado = false },
                destructivo = true
            )
        }
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun DetalleUnaColumna(
    material: Material,
    bajo: Boolean,
    usadoEn: List<UsoEnProducto>,
    onProducto: (String) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onAtras: () -> Unit,
    onRegistrarEntrada: () -> Unit,
    onPedirBajaDeLote: (LoteMaterial) -> Unit
) {
    Marco(barra = {
        BarraSuperior(material.nombre, onAtras = onAtras) {
            AccionesBarra(onEditar, onEliminar)
        }
    }) {
        item { TarjetaMaterial(material, bajo) }
        item { BotonPrincipal("Registrar entrada", onClick = onRegistrarEntrada) }

        if (material.lotes.isNotEmpty()) {
            item { TituloSeccion("Lotes") }
            items(material.lotes.size) { i ->
                FilaLote(
                    material.lotes[i], material.unidadMedida,
                    material.diasAvisoCaducidad, onPedirBajaDeLote
                )
            }
        }

        item { TituloSeccion("Se usa en") }
        seccionUsos(usadoEn, material.unidadMedida, onProducto)
    }
}

// ---------- TABLETA  (Figma 43:1331) ----------

@Composable
private fun DetalleDosColumnas(
    material: Material,
    bajo: Boolean,
    usadoEn: List<UsoEnProducto>,
    onProducto: (String) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onAtras: () -> Unit,
    onRegistrarEntrada: () -> Unit,
    onPedirBajaDeLote: (LoteMaterial) -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            BarraSuperior(material.nombre, onAtras = onAtras) {
                AccionesBarra(onEditar, onEliminar)
            }
            Row(
                Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = Margenes.xl),
                horizontalArrangement = Arrangement.spacedBy(Margenes.xxl)
            ) {
                // Izquierda: el estado del material y lo que se puede hacer con
                // el. Derecha: a donde va. Son dos lecturas distintas y en 1280
                // caben una junto a otra sin apretarse.
                LazyColumn(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    item { TarjetaMaterial(material, bajo) }
                    item { BotonPrincipal("Registrar entrada", onClick = onRegistrarEntrada) }
                    if (material.lotes.isNotEmpty()) {
                        item { TituloSeccion("Lotes disponibles") }
                        items(material.lotes.size) { i ->
                            FilaLote(
                                material.lotes[i], material.unidadMedida,
                                material.diasAvisoCaducidad, onPedirBajaDeLote
                            )
                        }
                    }
                }
                LazyColumn(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Margenes.md)
                ) {
                    item { TituloSeccion("Se usa en productos") }
                    seccionUsos(usadoEn, material.unidadMedida, onProducto)
                }
            }
        }
    }
}

// ---------- PIEZAS COMPARTIDAS ----------

/**
 * Lapiz y menu de la barra. El menu guarda lo irreversible: borrar no puede
 * estar a un toque de distancia del lapiz.
 */
@Composable
private fun AccionesBarra(onEditar: () -> Unit, onEliminar: () -> Unit) {
    var abierto by remember { mutableStateOf(false) }

    BotonIcono(Iconos.Editar, "Editar material", onEditar)
    Box {
        BotonIcono(Iconos.Mas, "Más opciones", { abierto = true })
        DropdownMenu(abierto, { abierto = false }) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Eliminar material",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    abierto = false
                    onEliminar()
                }
            )
        }
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.tituloMedio,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.semantics { heading() }
    )
}

/** La tarjeta de arriba: cuanto hay, a que costo y con que avisos. */
@Composable
private fun TarjetaMaterial(material: Material, bajo: Boolean) {
    val forma = RoundedCornerShape(Radios.dialogo)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(Medidas.borde, MaterialTheme.colorScheme.outlineVariant, forma)
            .padding(Margenes.xl),
        verticalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            Text(
                "Cantidad disponible:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                cant(material.cantidadDisponible) + " " + material.unidadMedida,
                style = MaterialTheme.typography.headlineLarge,
                // El rojo es la segunda senal, no la unica: abajo esta el
                // umbral escrito, que dice por que esta en rojo.
                color = if (bajo) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            Text(
                "Costo unitario:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                dinero(material.costoUnitario) + " / " + material.unidadMedida,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Margenes.sm)) {
            Pastilla("Umbral: " + cant(material.stockMinimo) + " " + material.unidadMedida)
            if (material.diasAvisoCaducidad > 0) {
                Pastilla("Aviso: " + material.diasAvisoCaducidad + " días")
            }
        }
    }
}


// ============================================================
//  UN LOTE REGISTRADO  (Figma 43:820)
//
//  Cuanto entro y, si el material caduca, cuando. Se pinta de rojo con el
//  MISMO criterio que usan los avisos y las notificaciones: el lote entra en
//  rojo cuando le quedan `diasAviso` dias o menos. Si en la pantalla se viera
//  un lote tranquilo y el telefono estuviera mandando una notificacion por el,
//  una de las dos estaria mintiendo.
//
//  El rojo NO es la unica senal: el triangulo y el cambio de "Caduca" a
//  "Caducó" dicen lo mismo sin depender del color.
//
//  SE DA DE BAJA DESLIZANDO HACIA LA IZQUIERDA. Un boton de basura por lote
//  llenaria la lista de iconos rojos para algo que se hace de vez en cuando;
//  el deslizado guarda la accion hasta que se busca. Y deslizar NO borra: pide
//  confirmacion, porque al lote se le va detras la mercancia.
//
//  Solo de derecha a izquierda (`enableDismissFromStartToEnd = false`): con
//  las dos direcciones, cualquier arrastre horizontal de la lista acaba
//  disparando algo.
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilaLote(
    lote: LoteMaterial,
    unidad: String,
    diasAviso: Int,
    onPedirBaja: (LoteMaterial) -> Unit
) {
    val estado = rememberSwipeToDismissBoxState(
        // Se devuelve SIEMPRE false a proposito: el gesto no confirma el
        // borrado, solo lo propone. La fila regresa a su sitio y el dialogo
        // decide. Si devolviera true, cancelar en el dialogo dejaria la fila
        // desaparecida sin haber borrado nada.
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.EndToStart) onPedirBaja(lote)
            false
        }
    )

    SwipeToDismissBox(
        state = estado,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Row(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Radios.campo))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = Margenes.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Iconos.Basura,
                    contentDescription = null,   // el dialogo que sigue ya lo dice
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        ContenidoLote(lote, unidad, diasAviso, onPedirBaja)
    }
}

@Composable
private fun ContenidoLote(
    lote: LoteMaterial,
    unidad: String,
    diasAviso: Int,
    onPedirBaja: (LoteMaterial) -> Unit
) {
    val dias = diasHasta(lote.caducidad)
    val vencido = dias != null && dias < 0
    val porCaducar = dias != null && dias >= 0 && dias <= diasAviso
    val alarma = vencido || porCaducar

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Margenes.lg)
            // El deslizado no existe para quien navega con lector de pantalla
            // o con teclado: esta accion lo deja al alcance de los dos.
            .semantics(mergeDescendants = true) {
                customActions = listOf(
                    CustomAccessibilityAction("Eliminar lote") { onPedirBaja(lote); true }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            // Los lotes de antes de que `lotes` tuviera columna de cantidad
            // valen 0. Decir "0 kg" seria mentir: nadie registro cero, es que
            // en su momento no se preguntaba.
            if (lote.cantidad > 0.0) cant(lote.cantidad) + " " + unidad
            else "Sin cantidad",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (lote.cantidad > 0.0) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (lote.cantidad > 0.0) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            when {
                // Sin fecha: el material no caduca. Antes esto salia como
                // "Caduca: " seguido de nada.
                lote.caducidad.isBlank() -> "Sin caducidad"
                vencido -> "⚠ Caducó: " + lote.caducidad
                porCaducar -> "⚠ Caduca: " + lote.caducidad
                else -> "Caduca: " + lote.caducidad
            },
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (alarma) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (alarma) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Los productos que llevan este material. Se comparte entre las dos medidas. */
private fun LazyListScope.seccionUsos(
    usadoEn: List<UsoEnProducto>,
    unidad: String,
    onProducto: (String) -> Unit
) {
    if (usadoEn.isEmpty()) {
        item {
            Text(
                "Todavía no forma parte de ninguna receta.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        items(usadoEn.size) { i ->
            val uso = usadoEn[i]
            FilaUso(uso, unidad) { onProducto(uso.productoId) }
        }
    }
}

@Composable
private fun FilaUso(uso: UsoEnProducto, unidad: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick() }
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            Text(
                uso.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                cant(uso.cantidadUsada) + " " + unidad + " por pieza",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Iconos.Siguiente,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
