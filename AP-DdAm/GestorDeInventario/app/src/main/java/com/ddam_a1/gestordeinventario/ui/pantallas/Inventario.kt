package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.almacenamiento.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.inventario.InventarioMateriales
import com.ddam_a1.gestordeinventario.productos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*

private enum class FiltroInv { TODOS, BAJOS, CADUCAN }

/** Pantalla 6 · Inventario de materiales (RF20). */
@Composable
fun PantallaInventario(nav: Navegador) {
    EstadoApp.version
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroInv.TODOS) }

    val todos = if (texto.isBlank()) InventarioMateriales.obtenerTodos()
    else InventarioMateriales.buscarMaterial(texto)
    val lista = when (filtro) {
        FiltroInv.TODOS -> todos
        FiltroInv.BAJOS -> todos.filter { InventarioMateriales.esStockBajo(it) }
        FiltroInv.CADUCAN -> todos.filter { it.fechasCaducidad.isNotEmpty() }
    }

    Marco(
        barra = { BarraSuperior("Inventario", "${todos.size} materiales") },
        pie = { BarraInferior(nav.actual) { nav.irARaiz(it) } }
    ) {
        item { BarraBusqueda(texto, "Buscar material") { texto = it } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Todos", filtro == FiltroInv.TODOS) { filtro = FiltroInv.TODOS }
                ChipFiltro("Stock bajo", filtro == FiltroInv.BAJOS) { filtro = FiltroInv.BAJOS }
                ChipFiltro("Con caducidad", filtro == FiltroInv.CADUCAN) { filtro = FiltroInv.CADUCAN }
            }
        }
        if (lista.isEmpty()) {
            item { EstadoVacio("Sin materiales", "Toca + para agregar el primero") }
        } else {
            items(lista.size) { i ->
                val m = lista[i]
                val bajo = InventarioMateriales.esStockBajo(m)
                FilaLista(
                    m.nombre,
                    "${dinero(m.costoUnitario)} / ${m.unidadMedida}" +
                        if (m.fechasCaducidad.isNotEmpty()) " · caduca ${m.fechasCaducidad.min()}" else "",
                    "${cant(m.cantidadDisponible)} ${m.unidadMedida}",
                    if (bajo) "stock bajo" else null,
                    if (bajo) Peligro else Exito
                ) { nav.ir(Ruta.DetalleMaterial(m.id)) }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nuevo material") { nav.ir(Ruta.FormularioMaterial(null)) }
        }
    }
}

/** Pantalla 7 · Detalle de material (RF3, RF9, RF18, RF19). */
@Composable
fun PantallaDetalleMaterial(nav: Navegador, id: String) {
    EstadoApp.version
    val m = InventarioMateriales.obtenerMaterialPorId(id)
    var nuevaFecha by remember { mutableStateOf("") }

    if (m == null) { nav.volver(); return }
    val bajo = InventarioMateriales.esStockBajo(m)
    val usadoEn = CatalogoProductos.obtenerTodos().filter { p -> p.receta.any { it.materialId == id } }

    Marco(barra = {
        BarraSuperior(m.nombre, onAtras = { nav.volver() }) {
            BotonIcono(Iconos.Editar, "Editar", { nav.ir(Ruta.FormularioMaterial(id)) })
        }
    }) {
        item {
            TarjetaSuave {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("En inventario", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${cant(m.cantidadDisponible)} ${m.unidadMedida}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (bajo) Peligro else MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Costo unitario", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(m.costoUnitario), style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Insignia("Aviso en ${cant(m.stockMinimo)} ${m.unidadMedida}", Primario, PrimarioSuave)
                    if (m.diasAvisoCaducidad > 0)
                        Insignia("${m.diasAvisoCaducidad} días antes", Acento, AcentoSuave)
                }
            }
        }
        item { EncabezadoSeccion("Fechas de caducidad (RF9)") }
        if (m.fechasCaducidad.isEmpty()) {
            item { Text("Este material no registra caducidad.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(m.fechasCaducidad.size) { i ->
                FilaLista("Lote ${i + 1}", "Caduca el ${m.fechasCaducidad[i]}")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) { CampoTexto(nuevaFecha, "Nueva fecha (aaaa-mm-dd)", { nuevaFecha = it }) }
                Box(Modifier.width(120.dp)) {
                    BotonSecundario("Agregar") {
                        if (nuevaFecha.isNotBlank()) {
                            InventarioMateriales.agregarFechaCaducidad(id, nuevaFecha.trim())
                            AlmacenamientoLocal.registrarLog(hoy(), "manual", "Caducidad ${nuevaFecha.trim()} en ${m.nombre}")
                            nuevaFecha = ""; EstadoApp.datosCambiaron()
                        }
                    }
                }
            }
        }
        item { EncabezadoSeccion("Se usa en") }
        if (usadoEn.isEmpty()) {
            item { Text("Todavía no forma parte de ninguna receta.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(usadoEn.size) { i ->
                val p = usadoEn[i]
                val ing = p.receta.first { it.materialId == id }
                FilaLista(p.nombre, "${cant(ing.cantidadUsada)} ${m.unidadMedida} por pieza") {
                    nav.ir(Ruta.DetalleProducto(p.id))
                }
            }
        }
    }
}

/** Pantalla 8 · Nuevo / editar material (RF1, RF2, RF18, RF19, RF22). */
@Composable
fun PantallaFormularioMaterial(nav: Navegador, id: String?) {
    val m = id?.let { InventarioMateriales.obtenerMaterialPorId(it) }
    var nombre by remember { mutableStateOf(m?.nombre ?: "") }
    var unidad by remember { mutableStateOf(m?.unidadMedida ?: "kg") }
    var cantidad by remember { mutableStateOf(m?.cantidadDisponible?.toString() ?: "") }
    var costo by remember { mutableStateOf(m?.costoUnitario?.toString() ?: "") }
    var minimo by remember { mutableStateOf(m?.stockMinimo?.toString() ?: "") }
    var caduca by remember { mutableStateOf((m?.diasAvisoCaducidad ?: 0) > 0) }
    var dias by remember { mutableStateOf(m?.diasAvisoCaducidad?.toString() ?: "7") }
    val valido = nombre.isNotBlank() && unidad.isNotBlank() && costo.toDoubleOrNull() != null

    Marco(barra = { BarraSuperior(if (m == null) "Nuevo material" else "Editar material", onAtras = { nav.volver() }) }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { CampoTexto(unidad, "Unidad (RF22)", { unidad = it }) }
                Box(Modifier.weight(1f)) { CampoTexto(cantidad, "Cantidad", { cantidad = it }, soloNumeros = true) }
            }
        }
        item { CampoTexto(costo, "Costo unitario", { costo = it }, soloNumeros = true, sufijo = "MXN / $unidad") }
        item { CampoTexto(minimo, "Avisarme cuando baje de", { minimo = it }, soloNumeros = true, sufijo = unidad) }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Este material caduca", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Podrás registrar varias fechas por lote",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(caduca, { caduca = it })
            }
        }
        if (caduca) {
            item { CampoTexto(dias, "Avisar antes de caducar", { dias = it }, soloNumeros = true, sufijo = "días") }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Guardar", habilitado = valido) {
                val c = costo.toDoubleOrNull() ?: 0.0
                val destino = if (m == null) {
                    InventarioMateriales.agregarMaterial(nombre.trim(), unidad.trim(), c, cantidad.toDoubleOrNull() ?: 0.0)
                } else {
                    InventarioMateriales.editarMaterial(m.id, nombre.trim(), c); m
                }
                InventarioMateriales.definirStockMinimo(destino.id, minimo.toDoubleOrNull() ?: 0.0)
                InventarioMateriales.definirDiasAvisoCaducidad(destino.id, if (caduca) dias.toIntOrNull() ?: 0 else 0)
                AlmacenamientoLocal.registrarLog(hoy(), "manual",
                    (if (m == null) "Alta" else "Edición") + " de material ${destino.nombre}")
                EstadoApp.datosCambiaron()
                nav.volver()
            }
        }
    }
}
