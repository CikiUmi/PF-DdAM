package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.aTexto
import com.ddam_a1.gestordeinventario.ui.components.BarraPasos
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.filtrarNumero
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 12 - RECETA   (Figma 48:1596 / 48:1969 / 48:2324)
//
//  Paso 2 de 2 del alta: que materiales lleva, cuanto de cada uno y a cuanto
//  se vende.
//
//  El PRECIO vive aqui y no en el paso 1 a proposito: arriba se ve el costo
//  estimado mientras se marcan los materiales, asi que el precio se decide
//  sabiendo cuanto cuesta producirlo. Poner uno sin el otro es adivinar.
//
//  El estado son DOS mapas y no uno:
//    `marcados`   que materiales entran (la casilla)
//    `cantidades` cuanto de cada uno, como TEXTO
//  Separados porque desmarcar y volver a marcar no debe borrar lo que ya
//  habias escrito, y porque un campo a medio escribir ("1.") no es un numero
//  todavia pero si es algo que el usuario ve.
// ============================================================

@Composable
fun PantallaReceta(
    nombreProducto: String,
    materiales: List<Material>,
    recetaActual: Map<String, Double>,
    precioActual: Double,
    esProductoNuevo: Boolean,
    onGuardar: (ingredientes: Map<String, Double>, precio: Double) -> Unit,
    onDescartar: () -> Unit,
    onAtras: () -> Unit
) {
    // Si vienes de crear el producto, salirse de aqui es CANCELAR el alta: el
    // producto ya existe y quedaria a medias, sin receta y sin costo. Por eso se
    // pregunta en vez de dejarte ir en silencio.
    var preguntarDescarte by remember { mutableStateOf(false) }
    val salir = { if (esProductoNuevo) preguntarDescarte = true else onAtras() }

    val marcados = remember(recetaActual) {
        mutableStateMapOf<String, Boolean>().apply {
            recetaActual.keys.forEach { this[it] = true }
        }
    }
    val cantidades = remember(recetaActual) {
        mutableStateMapOf<String, String>().apply {
            recetaActual.forEach { (id, cantidad) -> this[id] = cantidad.aTexto() }
        }
    }
    var precio by remember(precioActual) { mutableStateOf(precioActual.aTexto()) }

    // Solo cuenta lo marcado CON cantidad valida: una casilla marcada y el
    // campo vacio no es un ingrediente, es un renglon a medias.
    val elegidos = materiales.filter { marcados[it.id] == true }
    val ingredientes = elegidos.mapNotNull { m ->
        val n = cantidades[m.id]?.toDoubleOrNull() ?: 0.0
        if (n > 0.0) m.id to n else null
    }.toMap()

    val costoEstimado = elegidos.sumOf { m ->
        (cantidades[m.id]?.toDoubleOrNull() ?: 0.0) * m.costoUnitario
    }

    BoxWithConstraints {
        val enDosColumnas = anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA

        Marco(barra = {
            BarraSuperior("Receta", nombreProducto.ifBlank { null }, onAtras = salir)
        }) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        Modifier.widthIn(max = Anchos.tarjetaAncha),
                        verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                    ) {
                        if (esProductoNuevo) BarraPasos(paso = 2, total = 2, centrado = true)

                        Etiqueta("Seleccionar materiales requeridos")

                        if (materiales.isEmpty()) {
                            EstadoVacio(
                                "Sin materiales",
                                "Registre materiales en el inventario para poder armar una receta"
                            )
                        } else if (enDosColumnas) {
                            // En 700 de ancho caben dos casillas por renglon
                            // (Figma 48:2342) y la lista deja de ser una tira.
                            materiales.chunked(2).forEach { pareja ->
                                // Un material marcado trae campo de cantidad y
                                // otro sin marcar no: sin FilaPareja las dos
                                // casillas del renglon quedan desiguales.
                                FilaPareja {
                                    pareja.forEach { m ->
                                        Box(Modifier.weight(1f).fillMaxHeight()) {
                                            CasillaMaterial(m, marcados, cantidades)
                                        }
                                    }
                                    if (pareja.size == 1) Box(Modifier.weight(1f)) {}
                                }
                            }
                        } else {
                            materiales.forEach { m -> CasillaMaterial(m, marcados, cantidades) }
                        }

                        ResumenReceta(ingredientes.size, costoEstimado)

                        CampoTexto(
                            precio, "Precio de venta", { precio = it },
                            soloNumeros = true, sufijo = "MXN"
                        )

                        BotonPrincipal(
                            "Guardar receta",
                            habilitado = ingredientes.isNotEmpty()
                        ) {
                            // No se llama a onAtras: a donde ir despues de
                            // guardar depende de si esto fue un alta o una
                            // edicion, y eso lo sabe el NavHost, no la pantalla.
                            onGuardar(ingredientes, precio.toDoubleOrNull() ?: 0.0)
                        }
                    }
                }
            }
        }
    }

    if (preguntarDescarte) {
        DialogoSiNo(
            titulo = "¿Descartar el producto?",
            mensaje = "El producto ya se creó para poder asignarle esta receta. " +
                "Si sale ahora, se eliminará.",
            textoSi = "Descartar",
            textoNo = "Seguir aquí",
            onSi = {
                preguntarDescarte = false
                onDescartar()
            },
            onNo = { preguntarDescarte = false },
            onCerrar = { preguntarDescarte = false },
            destructivo = true
        )
    }
}

// ---------- PIEZAS ----------

/**
 * Un material de la lista, con su casilla y su cantidad.
 *
 * La fila entera es el area de toque de la casilla (`toggleable` con
 * `Role.Checkbox`), no solo el cuadrito de 24: en un telefono, acertarle a 24
 * puntos es pedir demasiado. El campo de cantidad queda fuera de esa area
 * porque tiene su propio foco.
 */
@Composable
private fun CasillaMaterial(
    material: Material,
    marcados: MutableMap<String, Boolean>,
    cantidades: MutableMap<String, String>
) {
    val activo = marcados[material.id] == true
    val cs = MaterialTheme.colorScheme

    Row(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(Radios.campo))
            .background(if (activo) cs.primaryContainer else cs.surfaceContainer)
            .padding(Margenes.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Row(
            Modifier
                .weight(1f)
                .toggleable(
                    value = activo,
                    role = Role.Checkbox,
                    onValueChange = { marcado ->
                        marcados[material.id] = marcado
                        // Al marcar por primera vez se siembra en 0: el campo
                        // aparece con algo dentro y se ve que ahi va un numero.
                        if (marcado && cantidades[material.id] == null) {
                            cantidades[material.id] = "0"
                        }
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            Cuadrito(activo)
            Text(
                material.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (activo) cs.onSurface else cs.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (activo) {
            CampoCantidad(
                valor = cantidades[material.id].orEmpty(),
                unidad = material.unidadMedida,
                etiqueta = "Cantidad de " + material.nombre,
                onCambio = { cantidades[material.id] = it }
            )
        }
    }
}

/** La casilla dibujada: llena con palomita, o solo el contorno. */
@Composable
private fun Cuadrito(activo: Boolean) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(6.dp)
    Box(
        Modifier
            .size(24.dp)
            .clip(forma)
            .then(
                if (activo) Modifier.background(cs.tertiary)
                else Modifier.border(Medidas.bordeGrueso, cs.outlineVariant, forma)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (activo) {
            Icon(
                Iconos.Check,
                contentDescription = null,
                tint = cs.onTertiary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * El campito de la derecha: blanco, bajito y con la unidad pegada.
 *
 * No es CampoTexto porque ese trae etiqueta arriba y 48 de alto; aqui cabe
 * dentro del renglon. Pero filtra con la MISMA funcion, que vive en
 * ui/Formato.kt: antes tenia su propia copia sin el cero guia, y por eso
 * escribir sobre el 0 sembrado dejaba un "3230" con el cero de sobra.
 */
@Composable
private fun CampoCantidad(
    valor: String,
    unidad: String,
    etiqueta: String,
    onCambio: (String) -> Unit
) {
    BasicTextField(
        value = valor,
        onValueChange = { nuevo -> onCambio(filtrarNumero(valor, nuevo)) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        // Misma regla que CampoTexto: el 0 sembrado se va al entrar al campo y
        // vuelve si se deja vacio. El comentario largo esta alla.
        modifier = Modifier
            .width(96.dp)
            .onFocusChanged { foco ->
                if (foco.isFocused && valor == "0") onCambio("")
                else if (!foco.isFocused && valor.isBlank()) onCambio("0")
            },
        decorationBox = { campo ->
            Row(
                Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(horizontal = Margenes.md)
                    .semanticaCampo(etiqueta),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Margenes.xs)
            ) {
                Box(Modifier.weight(1f)) { campo() }
                Text(
                    unidad,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

/** El campo no tiene etiqueta visible, asi que se la ponemos al lector de pantalla. */
private fun Modifier.semanticaCampo(etiqueta: String): Modifier =
    this.semantics { contentDescription = etiqueta }

@Composable
private fun ResumenReceta(cuantos: Int, costo: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Margenes.lg)
            .semanticaFila(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            cuantos.toString() + (if (cuantos == 1) " material" else " materiales") +
                " · costo estimado:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            dinero(costo),
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

private fun Modifier.semanticaFila(): Modifier =
    this.semantics(mergeDescendants = true) { }
