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
import com.ddam_a1.gestordeinventario.data.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.data.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.hoy
import com.ddam_a1.gestordeinventario.ui.components.OpcionSimple

/** Pantalla 11 · Nuevo / editar producto (RF4, RF7). */
@Composable
fun PantallaFormularioProducto(
    id: String?,
    onReceta: (String) -> Unit,
    onAtras: () -> Unit
) {
    val existente = if (id != null) CatalogoProductos.obtenerProductoPorId(id) else null
    var nombre by remember { mutableStateOf(if (existente != null) existente.nombre else "") }
    var precio by remember { mutableStateOf(if (existente != null) existente.precioVenta.toString() else "") }
    var bajoPedido by remember { mutableStateOf(existente != null && existente.esBajoPedido) }
    val valido = nombre.isNotBlank() && precio.toDoubleOrNull() != null

    Marco(barra = {
        BarraSuperior(if (existente == null) "Nuevo producto" else "Editar producto",
            onAtras = { onAtras() })
    }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item { CampoTexto(precio, "Precio de venta", { precio = it }, soloNumeros = true, sufijo = "MXN") }
        item { EncabezadoSeccion("¿Cómo se maneja?") }
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
                val pr = precio.toDoubleOrNull() ?: 0.0
                if (existente == null) {
                    val creado = CatalogoProductos.crearProducto(nombre.trim(), pr, bajoPedido)
                    AlmacenamientoLocal.registrarLog(hoy(), "manual", "Alta de producto " + creado.nombre)
                    EstadoApp.datosCambiaron()
                    onReceta(creado.id)
                } else {
                    CatalogoProductos.editarProducto(existente.id, nombre.trim(), pr)
                    existente.esBajoPedido = bajoPedido
                    AlmacenamientoLocal.registrarLog(hoy(), "manual", "Edición de producto " + existente.nombre)
                    EstadoApp.datosCambiaron()
                    onAtras()
                }
            }
        }
    }
}
