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
import com.ddam_a1.gestordeinventario.almacenamiento.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.inventario.InventarioMateriales
import com.ddam_a1.gestordeinventario.productos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.BarraInferior
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.Navegador
import com.ddam_a1.gestordeinventario.ui.Ruta
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.hoy
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
import com.ddam_a1.gestordeinventario.ui.theme.Acento
import com.ddam_a1.gestordeinventario.ui.theme.AcentoSuave
import com.ddam_a1.gestordeinventario.ui.theme.Exito
import com.ddam_a1.gestordeinventario.ui.theme.ExitoSuave
import com.ddam_a1.gestordeinventario.ui.theme.Peligro
import com.ddam_a1.gestordeinventario.ui.theme.PeligroSuave

/** Pantalla 9 · Catálogo de productos (RF8). */
@Composable
fun PantallaCatalogo(nav: Navegador) {
    EstadoApp.version
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(0) } // 0 todos · 1 con stock · 2 bajo pedido

    val base = if (texto.isBlank()) CatalogoProductos.obtenerTodos()
    else CatalogoProductos.buscarProducto(texto)
    val lista = when (filtro) {
        1 -> base.filter { producto -> !producto.esBajoPedido }
        2 -> base.filter { producto -> producto.esBajoPedido }
        else -> base
    }

    Marco(
        barra = { BarraSuperior("Catálogo", base.size.toString() + " productos") },
        pie = { BarraInferior(nav.actual, { destino -> nav.irARaiz(destino) }) }
    ) {
        item { BarraBusqueda(texto, "Buscar y filtrar producto", { texto = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Todos", filtro == 0, { filtro = 0 })
                ChipFiltro("Con stock", filtro == 1, { filtro = 1 })
                ChipFiltro("Bajo pedido", filtro == 2, { filtro = 2 })
            }
        }
        if (lista.isEmpty()) {
            item { EstadoVacio("Sin productos", "Agrega el primero para poder vender") }
        } else {
            items(lista.size) { i ->
                val producto = lista[i]
                val costo = CatalogoProductos.calcularCostoProduccion(producto.id)
                TarjetaSuave(onClick = { nav.ir(Ruta.DetalleProducto(producto.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Costo " + dinero(costo) + " · Precio " + dinero(producto.precioVenta),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (producto.esBajoPedido) {
                            Insignia("Bajo pedido", Acento, AcentoSuave)
                        } else {
                            Insignia(producto.stockDisponible.toString() + " pza", Exito, ExitoSuave)
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nuevo producto") { nav.ir(Ruta.FormularioProducto(null)) }
        }
    }
}

/** Pantalla 10 · Detalle de producto (RF5, RF6, RF7, RF10). */
@Composable
fun PantallaDetalleProducto(nav: Navegador, id: String) {
    EstadoApp.version
    val producto = CatalogoProductos.obtenerProductoPorId(id)
    if (producto == null) {
        nav.volver()
        return
    }
    val costo = CatalogoProductos.calcularCostoProduccion(id)
    val ganancia = producto.precioVenta - costo
    val caducidad = producto.caducidadMasCercana

    Marco(barra = {
        BarraSuperior(producto.nombre, onAtras = { nav.volver() }) {
            BotonIcono(Iconos.Editar, "Editar", { nav.ir(Ruta.FormularioProducto(id)) })
        }
    }) {
        item {
            TarjetaSuave {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("Precio", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(producto.precioVenta), style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Costo", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(costo), style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Ganancia", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(ganancia), style = MaterialTheme.typography.titleLarge,
                            color = Exito)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (producto.esBajoPedido) {
                        Insignia("Bajo pedido", Acento, AcentoSuave)
                    } else {
                        Insignia(producto.stockDisponible.toString() + " piezas en stock", Exito, ExitoSuave)
                    }
                    if (caducidad != null) {
                        Insignia("Caduca " + caducidad, Peligro, PeligroSuave)
                    }
                }
            }
        }

        item { EncabezadoSeccion("Receta", "Editar", { nav.ir(Ruta.Receta(id)) }) }
        if (producto.receta.isEmpty()) {
            item {
                Text("Sin materiales ligados. Este producto no calcula costo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(producto.receta.size) { i ->
                val ingrediente = producto.receta[i]
                val material = InventarioMateriales.obtenerMaterialPorId(ingrediente.materialId)
                val costoUnit = if (material != null) material.costoUnitario else 0.0
                val unidad = if (material != null) material.unidadMedida else ""
                FilaLista(
                    titulo = if (material != null) material.nombre else "Material",
                    subtitulo = cant(ingrediente.cantidadUsada) + " " + unidad + " × " + dinero(costoUnit),
                    valor = dinero(costoUnit * ingrediente.cantidadUsada)
                )
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            if (!producto.esBajoPedido) {
                BotonPrincipal("Registrar producción") { nav.ir(Ruta.Produccion(id)) }
            } else {
                Text("Los productos bajo pedido descuentan sus materiales al momento de la venta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

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

/** Pantalla 12 · Seleccionar materiales de la receta (RF6). */
@Composable
fun PantallaReceta(nav: Navegador, productoId: String) {
    EstadoApp.version
    val producto = CatalogoProductos.obtenerProductoPorId(productoId)
    if (producto == null) {
        nav.volver()
        return
    }
    val materiales = InventarioMateriales.obtenerTodos()
    val cantidades = remember { mutableStateMapOf<String, String>() }

    remember(productoId) {
        producto.receta.forEach { ingrediente ->
            cantidades[ingrediente.materialId] = cant(ingrediente.cantidadUsada)
        }
        productoId
    }

    var costo = 0.0
    for (entrada in cantidades) {
        val material = InventarioMateriales.obtenerMaterialPorId(entrada.key)
        val unitario = if (material != null) material.costoUnitario else 0.0
        costo += (entrada.value.toDoubleOrNull() ?: 0.0) * unitario
    }

    Marco(barra = { BarraSuperior("Materiales", producto.nombre, onAtras = { nav.volver() }) }) {
        if (materiales.isEmpty()) {
            item { EstadoVacio("No hay materiales", "Agrega materiales al inventario primero") }
        }
        items(materiales.size) { i ->
            val material = materiales[i]
            val marcado = cantidades.containsKey(material.id)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = marcado,
                    onCheckedChange = { activado ->
                        if (activado) {
                            cantidades[material.id] = ""
                        } else {
                            cantidades.remove(material.id)
                        }
                    }
                )
                Column(Modifier.weight(1f)) {
                    Text(material.nombre, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(dinero(material.costoUnitario) + " / " + material.unidadMedida,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (marcado) {
                    Box(Modifier.width(110.dp)) {
                        CampoTexto(
                            valor = cantidades[material.id] ?: "",
                            etiqueta = material.unidadMedida,
                            onCambio = { nuevo -> cantidades[material.id] = nuevo },
                            soloNumeros = true
                        )
                    }
                }
            }
        }
        item {
            TarjetaSuave {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cantidades.size.toString() + " materiales · costo calculado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f))
                    Text(dinero(costo), style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            BotonPrincipal("Guardar receta") {
                producto.receta.clear()
                for (entrada in cantidades) {
                    val c = entrada.value.toDoubleOrNull() ?: 0.0
                    if (c > 0.0) {
                        CatalogoProductos.agregarIngredienteReceta(productoId, entrada.key, c)
                    }
                }
                CatalogoProductos.calcularCostoProduccion(productoId)
                EstadoApp.datosCambiaron()
                nav.volver()
            }
        }
    }
}

/** Pantalla 13 · Registrar producción (RF10, RF11). */
@Composable
fun PantallaProduccion(nav: Navegador, productoId: String) {
    EstadoApp.version
    val producto = CatalogoProductos.obtenerProductoPorId(productoId)
    if (producto == null) {
        nav.volver()
        return
    }
    var cantidad by remember { mutableStateOf("1") }
    var preguntar by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val n = cantidad.toIntOrNull() ?: 0

    Marco(barra = {
        BarraSuperior("Registrar producción", producto.nombre, onAtras = { nav.volver() })
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
                colorPunto = if (alcanza) Exito else Peligro
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
                    nav.volver()
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
                nav.volver()
            },
            onCerrar = { preguntar = false }
        )
    }
}
