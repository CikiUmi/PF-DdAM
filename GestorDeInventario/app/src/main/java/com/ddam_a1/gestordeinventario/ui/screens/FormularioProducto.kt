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
    var precio by remember(producto) { mutableStateOf(producto?.precioVenta?.toString() ?: "") }
    var bajoPedido by remember(producto) { mutableStateOf(producto?.esBajoPedido ?: false) }

    val valido = nombre.isNotBlank() && precio.toDoubleOrNull() != null

    Marco(barra = {
        BarraSuperior(if (producto == null) "Nuevo producto" else "Editar producto", onAtras = onAtras)
    }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item { CampoTexto(precio, "Precio de venta", { precio = it }, soloNumeros = true, sufijo = "MXN") }
        item { EncabezadoSeccion("Como se maneja?") }
        item {
            OpcionSimple("Con stock",
                "Se produce por lotes y se guarda. La venta descuenta del stock.",
                !bajoPedido, { bajoPedido = false })
        }
        item {
            OpcionSimple("Bajo pedido",
                "Se elabora al momento. La venta descuenta los materiales.",
                bajoPedido, { bajoPedido = true })
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Guardar", habilitado = valido) {
                onGuardar(
                    DatosProducto(
                        nombre = nombre.trim(),
                        precioVenta = precio.toDoubleOrNull() ?: 0.0,
                        esBajoPedido = bajoPedido
                    )
                )
            }
        }
    }
}
