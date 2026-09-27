package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.ui.aTexto
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.OpcionSimple

/**
 * Pantalla 11 - Nuevo / editar producto (RF4, RF7).
 *
 * No guarda ni navega: entrega los datos. Quien decide si despues de crear hay
 * que ir a la receta es el NavHost, que es el unico que sabe navegar.
 */
@Composable
fun PantallaFormularioProducto(
    producto: Producto?,
    onGuardar: (DatosProducto) -> Unit,
    onAtras: () -> Unit
) {
    var nombre by remember(producto) { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember(producto) { mutableStateOf(producto?.precioVenta?.aTexto() ?: "0") }
    var bajoPedido by remember(producto) { mutableStateOf(producto?.esBajoPedido ?: false) }
    var minimo by remember(producto) { mutableStateOf(producto?.stockMinimo?.toString() ?: "0") }

    // Stock inicial: solo al CREAR. Al editar no aparece, porque cambiar el
    // stock desde aqui se saltaria la bitacora; para eso esta "registrar
    // existencias", que si deja rastro de cuanto entro y cuando.
    var stockInicial by remember(producto) { mutableStateOf("0") }

    val precioNum = precio.toDoubleOrNull() ?: 0.0
    val valido = nombre.isNotBlank() && precioNum > 0.0

    Marco(barra = {
        BarraSuperior(if (producto == null) "Nuevo producto" else "Editar producto", onAtras = onAtras)
    }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item {
            CampoTexto(
                precio, "Precio de venta", { precio = it },
                soloNumeros = true, sufijo = "MXN",
                error = if (precioNum <= 0.0) "El precio debe ser mayor que 0" else null
            )
        }
        item { EncabezadoSeccion("Tipo de producto") }
        item {
            OpcionSimple("Con stock",
                "Se produce por lotes y se almacena. La venta descuenta del stock.",
                !bajoPedido, { bajoPedido = false })
        }
        item {
            OpcionSimple("Bajo pedido",
                "Se elabora al momento. La venta descuenta los materiales.",
                bajoPedido, { bajoPedido = true })
        }
        // Solo los productos CON stock pueden quedarse bajos. Uno bajo pedido se
        // elabora al momento, asi que preguntarle un umbral no tiene sentido.
        if (!bajoPedido) {
            // Un producto bajo pedido se elabora al momento: ni tiene stock
            // que registrar ni umbral que vigilar.
            if (producto == null) {
                item {
                    CampoTexto(
                        stockInicial, "Stock inicial", { stockInicial = it },
                        soloEnteros = true, sufijo = "piezas",
                        marcador = "0"
                    )
                }
            }
            item {
                CampoTexto(
                    minimo, "Avisar cuando el inventario baje de", { minimo = it },
                    soloEnteros = true, sufijo = "piezas"
                )
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Guardar", habilitado = valido) {
                onGuardar(
                    DatosProducto(
                        nombre = nombre.trim(),
                        precioVenta = precioNum,
                        esBajoPedido = bajoPedido,
                        stockMinimo = if (bajoPedido) 0 else (minimo.toIntOrNull() ?: 0),
                        stockInicial = if (bajoPedido || producto != null) 0
                        else (stockInicial.toIntOrNull() ?: 0)
                    )
                )
            }
        }
    }
}
