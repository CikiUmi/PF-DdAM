package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.navegacion.BarraInferior
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.navegacion.Navegador
import com.ddam_a1.gestordeinventario.ui.navegacion.Ruta
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.componentes.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.componentes.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.componentes.BotonIcono
import com.ddam_a1.gestordeinventario.ui.componentes.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.componentes.CampoTexto
import com.ddam_a1.gestordeinventario.ui.componentes.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.componentes.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.componentes.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.componentes.FilaLista
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos
import com.ddam_a1.gestordeinventario.ui.componentes.Insignia
import com.ddam_a1.gestordeinventario.ui.componentes.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.hoy
import com.ddam_a1.gestordeinventario.ui.componentes.OpcionSimple

/** Pantalla 11 · Nuevo / editar producto (RF4, RF7). */
@Composable
fun PantallaFormularioProducto(nav: Navegador, id: String?) {
    val existente = if (id != null) CatalogoProductos.obtenerProductoPorId(id) else null
    var nombre by remember { mutableStateOf(if (existente != null) existente.nombre else "") }
    var precio by remember { mutableStateOf(if (existente != null) existente.precioVenta.toString() else "") }
    var bajoPedido by remember { mutableStateOf(existente != null && existente.esBajoPedido) }
    val valido = nombre.isNotBlank() && precio.toDoubleOrNull() != null

    Marco(barra = {
        BarraSuperior(if (existente == null) "Nuevo producto" else "Editar producto",
            onAtras = { nav.volver() })
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
                    nav.ir(Ruta.Receta(creado.id))
                } else {
                    CatalogoProductos.editarProducto(existente.id, nombre.trim(), pr)
                    existente.esBajoPedido = bajoPedido
                    AlmacenamientoLocal.registrarLog(hoy(), "manual", "Edición de producto " + existente.nombre)
                    EstadoApp.datosCambiaron()
                    nav.volver()
                }
            }
        }
    }
}
