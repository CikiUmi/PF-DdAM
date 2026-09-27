package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.components.BarraPasos
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.OpcionSimple
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

// ============================================================
//  PANTALLA 11 - NUEVO / EDITAR PRODUCTO   (Figma 48:1552 / 48:1924 / 48:2282)
//
//  Paso 1 de 2. Aqui van el nombre, como se surte y cuando avisar; el PRECIO
//  y la receta van en el paso 2 (pantalla 12), porque el precio solo tiene
//  sentido cuando ya se sabe cuanto cuesta producirlo.
//
//  Al EDITAR no hay dos pasos: se entra a esta pantalla sola desde el detalle,
//  asi que la barra de pasos y el "Siguiente" solo salen al crear.
//
//  El precio del producto viaja igual dentro de DatosProducto, con el valor
//  que ya tenia. Es a proposito: si se mandara 0, editar el nombre borraria
//  el precio sin que nadie lo pidiera.
// ============================================================

@Composable
fun PantallaFormularioProducto(
    producto: Producto?,
    onGuardar: (DatosProducto) -> Unit,
    onAtras: () -> Unit
) {
    val esNuevo = producto == null

    var nombre by remember(producto) { mutableStateOf(producto?.nombre ?: "") }
    var bajoPedido by remember(producto) { mutableStateOf(producto?.esBajoPedido ?: false) }
    var minimo by remember(producto) { mutableStateOf(producto?.stockMinimo?.toString() ?: "0") }

    // Stock inicial: solo al CREAR. Al editar no aparece, porque cambiar el
    // stock desde aqui se saltaria la bitacora; para eso esta "registrar
    // produccion", que si deja rastro de cuanto entro y cuando.
    var stockInicial by remember(producto) { mutableStateOf("0") }

    val valido = nombre.isNotBlank()

    Marco(barra = {
        BarraSuperior(if (esNuevo) "Nuevo producto" else "Editar producto", onAtras = onAtras)
    }) {
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    Modifier.widthIn(max = Anchos.tarjetaAncha),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    if (esNuevo) BarraPasos(paso = 1, total = 2)

                    CampoTexto(
                        nombre, "Nombre del producto", { nombre = it },
                        marcador = "Ej. Pan de chocolate"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(Margenes.md)) {
                        Etiqueta("Modo de disponibilidad")
                        OpcionSimple(
                            "Con stock en inventario",
                            "Se produce por adelantado y se guarda",
                            !bajoPedido
                        ) { bajoPedido = false }
                        OpcionSimple(
                            "Bajo pedido de clientes",
                            "Se fabrica cuando alguien lo encarga",
                            bajoPedido
                        ) { bajoPedido = true }
                    }

                    // Un producto bajo pedido no guarda existencias, asi que
                    // no hay nada que vigilar ni stock con el que empezar.
                    if (!bajoPedido) {
                        CampoTexto(
                            minimo, "Avisar cuando queden", { minimo = it },
                            soloEnteros = true, sufijo = "piezas"
                        )
                        if (esNuevo) {
                            CampoTexto(
                                stockInicial, "Piezas que ya tiene", { stockInicial = it },
                                soloEnteros = true, sufijo = "piezas"
                            )
                        }
                    }

                    BotonPrincipal(
                        if (esNuevo) "Siguiente" else "Guardar",
                        habilitado = valido
                    ) {
                        onGuardar(
                            DatosProducto(
                                nombre = nombre.trim(),
                                // El precio no se toca aqui: lo pone el paso 2.
                                precioVenta = producto?.precioVenta ?: 0.0,
                                esBajoPedido = bajoPedido,
                                stockMinimo = if (bajoPedido) 0 else (minimo.toIntOrNull() ?: 0),
                                stockInicial =
                                    if (esNuevo && !bajoPedido) (stockInicial.toIntOrNull() ?: 0)
                                    else 0
                            )
                        )
                    }
                }
            }
        }
    }
}

/** El titulo de un grupo de opciones, con la misma letra que las etiquetas de campo. */
@Composable
internal fun Etiqueta(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.semantics { heading() }
    )
}
