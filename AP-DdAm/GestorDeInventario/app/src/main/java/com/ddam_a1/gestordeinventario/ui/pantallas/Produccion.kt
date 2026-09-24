package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.componentes.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.componentes.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.componentes.CampoTexto
import com.ddam_a1.gestordeinventario.ui.componentes.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.componentes.FilaLista
import com.ddam_a1.gestordeinventario.ui.componentes.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.hoy
import com.ddam_a1.gestordeinventario.ui.componentes.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/** Pantalla 13 · Registrar producción (RF10, RF11). */
@Composable
fun PantallaProduccion(productoId: String, onAtras: () -> Unit) {
    EstadoApp.version
    val producto = CatalogoProductos.obtenerProductoPorId(productoId)
    if (producto == null) {
        onAtras()
        return
    }
    var cantidad by remember { mutableStateOf("1") }
    var preguntar by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val n = cantidad.toIntOrNull() ?: 0

    Marco(barra = {
        BarraSuperior("Registrar producción", producto.nombre, onAtras = { onAtras() })
    }) {
        item {
            CampoTexto(cantidad, "Cantidad a producir",
                { nuevo -> cantidad = nuevo.filter { c -> c.isDigit() } }, sufijo = "piezas")
        }
        item { EncabezadoSeccion("Materiales necesarios") }
        items(producto.receta.size) { i ->
            val ingrediente = producto.receta[i]
            val material = InventarioMateriales.obtenerMaterialPorId(ingrediente.materialId)
            val disponible = if (material != null) material.cantidadDisponible else 0.0
            val unidad = if (material != null) material.unidadMedida else ""
            val necesita = ingrediente.cantidadUsada * n
            val alcanza = disponible >= necesita
            FilaLista(
                titulo = if (material != null) material.nombre else "Material",
                subtitulo = "Necesitas " + cant(necesita) + " " + unidad,
                valor = cant(disponible) + " " + unidad,
                notaValor = if (alcanza) "disponible" else "insuficiente",
                colorPunto = if (alcanza) MaterialTheme.coloresExtra.correct.color else MaterialTheme.colorScheme.error
            )
        }
        item {
            TarjetaSuave {
                Text("La caducidad del lote se toma de la fecha más cercana de sus materiales.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (error.isNotBlank()) {
            item {
                Text(error, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error)
            }
        }
        item {
            BotonPrincipal("Registrar producción", habilitado = n > 0) {
                error = ""
                preguntar = true
            }
        }
    }

    if (preguntar) {
        DialogoSiNo(
            titulo = "¿Descontar los materiales del inventario?",
            mensaje = "Vas a registrar " + n + " piezas de " + producto.nombre +
                ". Puedes descontar ahora los materiales que usaste, o dejarlos como están si los repusiste aparte.",
            textoSi = "Descontar",
            textoNo = "No descontar",
            onSi = {
                preguntar = false
                if (CatalogoProductos.registrarExistencias(productoId, n, true)) {
                    AlmacenamientoLocal.registrarLog(hoy(), "manual",
                        "Producción de " + n + " " + producto.nombre + " (materiales descontados)")
                    EstadoApp.datosCambiaron()
                    onAtras()
                } else {
                    error = "No alcanzan los materiales para producir " + n + " piezas."
                }
            },
            onNo = {
                preguntar = false
                CatalogoProductos.registrarExistencias(productoId, n, false)
                AlmacenamientoLocal.registrarLog(hoy(), "manual",
                    "Producción de " + n + " " + producto.nombre + " (sin descontar)")
                EstadoApp.datosCambiaron()
                onAtras()
            },
            onCerrar = { preguntar = false }
        )
    }
}
