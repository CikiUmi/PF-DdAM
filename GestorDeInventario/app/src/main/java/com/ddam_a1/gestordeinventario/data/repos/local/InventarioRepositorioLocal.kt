package com.ddam_a1.gestordeinventario.data.repos.local

import androidx.room.withTransaction
import com.ddam_a1.gestordeinventario.data.database.GestorDatabase
import android.content.Context
import com.ddam_a1.gestordeinventario.data.services.ExportadorCSV
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import com.ddam_a1.gestordeinventario.data.repos.ErrorVenta
import com.ddam_a1.gestordeinventario.data.repos.ResultadoVenta
import com.ddam_a1.gestordeinventario.data.dao.AvisoDescartadoDao
import com.ddam_a1.gestordeinventario.data.dao.BitacoraDao
import com.ddam_a1.gestordeinventario.data.dao.LoteDao
import com.ddam_a1.gestordeinventario.data.dao.MaterialDao
import com.ddam_a1.gestordeinventario.data.dao.ProductoDao
import com.ddam_a1.gestordeinventario.data.dao.RecetaDao
import com.ddam_a1.gestordeinventario.data.dao.VentaDao
import com.ddam_a1.gestordeinventario.data.repos.DIAS_AVISO_CADUCIDAD_PRODUCTO
import com.ddam_a1.gestordeinventario.data.repos.InventarioRepositorio
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.AvisoDescartado
import com.ddam_a1.gestordeinventario.modelClasses.IngredienteReceta
import com.ddam_a1.gestordeinventario.modelClasses.ItemVendido
import com.ddam_a1.gestordeinventario.modelClasses.LoteMaterial
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.TipoAviso
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventarioRepositorioLocal @Inject constructor(
    private val materialDao: MaterialDao,
    private val loteDao: LoteDao,
    private val productoDao: ProductoDao,
    private val recetaDao: RecetaDao,
    private val bitacoraDao: BitacoraDao,
    private val avisoDescartadoDao: AvisoDescartadoDao,
    private val ventaDao: VentaDao,
    // La base entera, ademas de los DAO: es lo que permite abrir UNA transaccion
    // que abarque varios DAO. El @Provides ya existia (proveerBaseDatos).
    private val db: GestorDatabase,
    // Solo para saber DONDE escribir el .csv. Con @ApplicationContext Hilt da
    // el contexto de la aplicacion, que vive lo mismo que el proceso: guardar
    // el de una Activity en un @Singleton la dejaria sin poder morir.
    @ApplicationContext private val contexto: Context
) : InventarioRepositorio {

    // ---------- LOS FLUJOS ----------
    //
    // `combine` es la pieza clave: escucha DOS consultas a la vez y vuelve a
    // emitir cuando cualquiera de las dos cambia. Agregas un lote y la lista de
    // materiales se rearma sola con su fecha nueva.

    override fun materialesStream(): Flow<List<Material>> =
        combine(materialDao.todos(), loteDao.todos()) { materiales, lotes ->
            materiales.map { material ->
                material.apply {
                    this.lotes = lotes
                        .filter { it.materialId == material.id }
                        .toMutableList()
                }
            }
        }

    override fun productosStream(): Flow<List<Producto>> =
        combine(productoDao.todos(), recetaDao.todas()) { productos, receta ->
            productos.map { producto ->
                producto.apply {
                    this.receta = receta.filter { it.productoId == producto.id }.toMutableList()
                }
            }
        }

    override fun bitacoraStream(): Flow<List<RegistroLog>> = bitacoraDao.todos()

    // ---------- MATERIALES ----------

    override suspend fun agregarMaterial(
        nombre: String, unidad: String, costo: Double, cantidad: Double,
        caducidadInicial: String
    ): Material {
        val material = Material(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            unidadMedida = unidad,
            costoUnitario = costo,
            cantidadDisponible = cantidad
        )
        materialDao.guardar(material)

        // La cantidad inicial TAMBIEN es un lote. Antes solo subia la
        // existencia, y el material nacia con 20 kg que no venian de ninguna
        // entrada: la lista de lotes salia vacia y sus cantidades no sumaban
        // lo disponible.
        if (cantidad > 0.0) {
            loteDao.agregar(
                LoteMaterial(
                    materialId = material.id,
                    caducidad = caducidadInicial.trim(),
                    cantidad = cantidad
                )
            )
        }
        return material
    }

    /**
     * Leer, cambiar, guardar.
     *
     * OJO CON ESTO, que es la trampa numero uno al pasar de listas en memoria a
     * Room: antes bastaba `material.nombre = "otro"` porque el objeto ERA el
     * dato. Ahora el objeto es una COPIA de un renglon; cambiarlo en memoria no
     * escribe nada. Hay que pedirle a la base que actualice.
     */
    override suspend fun editarMaterial(id: String, nombre: String?, costo: Double?): Boolean {
        val material = materialDao.leer(id) ?: return false
        if (nombre != null) material.nombre = nombre
        if (costo != null) material.costoUnitario = costo
        materialDao.actualizar(material)
        return true
    }

    override suspend fun eliminarMaterial(id: String): Boolean {
        val material = materialDao.leer(id) ?: return false
        materialDao.borrar(material)   // los lotes se van solos, por CASCADE
        return true
    }

    override suspend fun agregarExistenciasMaterial(materialId: String, cantidad: Double): Boolean {
        if (cantidad <= 0.0) return false
        return materialDao.sumar(materialId, cantidad) > 0
    }

    override suspend fun agregarLote(materialId: String, cantidad: Double, fecha: String): Boolean {
        // Ya NO se exige fecha. Un material que no caduca tambien recibe
        // mercancia, y ese movimiento merece quedar registrado: sin esto, sus
        // entradas desaparecian y la seccion de lotes se veia vacia para
        // siempre. La cantidad si es obligatoria: un lote de cero no entro.
        if (cantidad <= 0.0) return false
        loteDao.agregar(
            LoteMaterial(materialId = materialId, caducidad = fecha.trim(), cantidad = cantidad)
        )
        return true
    }

    override suspend fun eliminarLote(loteId: String): Boolean {
        val lote = loteDao.leer(loteId) ?: return false
        return db.withTransaction {
            // `descontar` lleva un `AND cantidad_disponible >= :cantidad` que
            // protege de dejar la existencia en negativo. Aqui eso se volveria
            // en contra: si el material ya se consumio en produccion, lo
            // disponible puede ser MENOR que el lote, la resta no aplicaria y
            // el lote se iria sin descontar nada. Por eso se descuenta lo que
            // de verdad queda, que como mucho es el lote entero.
            val disponible = materialDao.leer(lote.materialId)?.cantidadDisponible ?: 0.0
            val aDescontar = minOf(lote.cantidad, disponible)
            if (aDescontar > 0.0) materialDao.descontar(lote.materialId, aDescontar)
            loteDao.eliminar(loteId) > 0
        }
    }

    override suspend fun definirStockMinimo(materialId: String, minimo: Double): Boolean =
        materialDao.definirStockMinimo(materialId, minimo) > 0

    override suspend fun definirDiasAvisoCaducidad(materialId: String, dias: Int): Boolean =
        materialDao.definirDiasAviso(materialId, dias) > 0

    override suspend fun leerMaterial(id: String): Material? =
        materialDao.leer(id)?.apply {
            lotes = loteDao.lotesDe(id).toMutableList()
        }

    override suspend fun buscarMaterial(texto: String): List<Material> = materialDao.buscar(texto)

    override fun esStockBajo(material: Material): Boolean =
        material.cantidadDisponible <= material.stockMinimo

    // ---------- PRODUCTOS ----------

    override suspend fun crearProducto(
        nombre: String, precioVenta: Double, esBajoPedido: Boolean
    ): Producto {
        val producto = Producto(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            precioVenta = precioVenta,
            esBajoPedido = esBajoPedido
        )
        productoDao.guardar(producto)
        return producto
    }

    override suspend fun editarProducto(id: String, nombre: String?, precioVenta: Double?): Boolean {
        val producto = productoDao.leer(id) ?: return false
        if (nombre != null) producto.nombre = nombre
        if (precioVenta != null) producto.precioVenta = precioVenta
        productoDao.actualizar(producto)
        return true
    }

    override suspend fun eliminarProducto(id: String): Boolean {
        val producto = productoDao.leer(id) ?: return false
        productoDao.borrar(producto)   // su receta se va por CASCADE
        return true
    }

    override suspend fun agregarIngredienteReceta(
        productoId: String, materialId: String, cantidadUsada: Double
    ): Boolean {
        if (cantidadUsada <= 0.0) return false
        recetaDao.agregar(IngredienteReceta(productoId, materialId, cantidadUsada))
        productoDao.definirCostoProduccion(productoId, calcularCostoProduccion(productoId))
        return true
    }

    override suspend fun reemplazarReceta(
        productoId: String, ingredientes: Map<String, Double>
    ): Boolean {
        val lista = ingredientes
            .filter { it.value > 0.0 }
            .map { IngredienteReceta(productoId, it.key, it.value) }
        recetaDao.reemplazar(productoId, lista)   // borrar + insertar, en una transaccion
        productoDao.definirCostoProduccion(productoId, calcularCostoProduccion(productoId))
        return true
    }

    override suspend fun asignarPrecioVenta(productoId: String, precio: Double): Boolean =
        productoDao.definirPrecioVenta(productoId, precio) > 0

    override suspend fun definirStockMinimoProducto(productoId: String, minimo: Int): Boolean =
        productoDao.definirStockMinimo(productoId, minimo) > 0

    /**
     * Registrar produccion (RF10, RF11).
     *
     * Dos fases, igual que antes: primero se revisa que alcance TODO, y solo
     * entonces se descuenta. Si validaras y descontaras a la vez, un ingrediente
     * que no alcanza a la mitad te dejaria el lote a medio producir con
     * materiales ya gastados.
     */
    override suspend fun registrarExistencias(
        productoId: String, cantidad: Int, descontarMaterialesAhora: Boolean
    ): Boolean {
        if (cantidad <= 0) return false
        productoDao.leer(productoId) ?: return false
        val receta = recetaDao.recetaDe(productoId)

        if (descontarMaterialesAhora) {
            // fase 1: revisar
            for (ingrediente in receta) {
                val material = materialDao.leer(ingrediente.materialId) ?: return false
                if (material.cantidadDisponible < ingrediente.cantidadUsada * cantidad) return false
            }
            // fase 2: aplicar
            for (ingrediente in receta) {
                materialDao.descontar(ingrediente.materialId, ingrediente.cantidadUsada * cantidad)
            }
        }

        productoDao.sumarStock(productoId, cantidad)

        // La caducidad del lote es la mas proxima de sus materiales, y se
        // conserva la que ya hubiera si era anterior.
        val caducidades = receta.flatMap { loteDao.lotesDe(it.materialId).map { l -> l.caducidad } }
        val masProxima = caducidades.minOrNull()
        if (masProxima != null) {
            val actual = productoDao.leer(productoId)?.caducidadMasCercana
            productoDao.definirCaducidad(productoId, listOfNotNull(actual, masProxima).min())
        }
        return true
    }

    override suspend fun leerProducto(id: String): Producto? =
        productoDao.leer(id)?.apply { receta = recetaDao.recetaDe(id).toMutableList() }

    override suspend fun buscarProducto(texto: String): List<Producto> = productoDao.buscar(texto)

    override suspend fun calcularCostoProduccion(productoId: String): Double {
        var total = 0.0
        for (ingrediente in recetaDao.recetaDe(productoId)) {
            val material = materialDao.leer(ingrediente.materialId)
            total += (material?.costoUnitario ?: 0.0) * ingrediente.cantidadUsada
        }
        return total
    }

    // ---------- AVISOS ----------
    //
    // Se calculan aqui y ya no en el objeto `Notificaciones`, porque aquel lee
    // las listas en memoria, que desde hoy estan vacias.

    override suspend fun revisarStockBajo(): List<Aviso> =
        materialDao.buscar("")
            .filter { esStockBajo(it) }
            .map {
                Aviso(
                    it.id, TipoAviso.STOCK_BAJO_MATERIAL,
                    it.nombre,
                    "Quedan " + sinCeroSobrante(it.cantidadDisponible) + " " + it.unidadMedida +
                        " · mínimo " + sinCeroSobrante(it.stockMinimo) + " " + it.unidadMedida
                )
            }

    override suspend fun revisarStockBajoProductos(): List<Aviso> =
        productoDao.buscar("")
            .filter { !it.esBajoPedido && it.stockMinimo > 0 && it.stockDisponible <= it.stockMinimo }
            .map {
                Aviso(
                    it.id, TipoAviso.STOCK_BAJO_PRODUCTO,
                    it.nombre,
                    "Quedan " + it.stockDisponible + " piezas · mínimo " + it.stockMinimo
                )
            }

    override suspend fun revisarCaducidades(fechaHoy: String): List<Aviso> {
        val avisos = mutableListOf<Aviso>()
        for (material in materialDao.buscar("")) {
            // Un material con cero dias de aviso es un material que no caduca:
            // sus lotes ni siquiera piden fecha al darlos de alta.
            if (material.diasAvisoCaducidad <= 0) continue
            for (lote in loteDao.lotesDe(material.id)) {
                val dias = diasEntre(fechaHoy, lote.caducidad) ?: continue
                val cuanto = sinCeroSobrante(lote.cantidad) + " " + material.unidadMedida

                // Los dias negativos son fechas que ya pasaron. Antes caian
                // fuera del rango y el lote desaparecia de los avisos justo el
                // dia en que mas importaba: el aviso se apagaba solo al caducar.
                if (dias < 0) {
                    avisos.add(
                        Aviso(
                            material.id, TipoAviso.CADUCADO,
                            material.nombre,
                            "Caducó el " + lote.caducidad + " · " + cuanto
                        )
                    )
                } else if (dias <= material.diasAvisoCaducidad) {
                    avisos.add(
                        Aviso(
                            material.id, TipoAviso.CADUCIDAD,
                            material.nombre,
                            "Caduca el " + lote.caducidad + " · " + cuanto
                        )
                    )
                }
            }
        }

        // ---- Productos ----
        //
        // Un producto no guarda lotes: guarda UNA fecha, la del material mas
        // proximo a caducar de los que se usaron al producirlo. Por eso aqui
        // hay una fecha por producto y no un bucle mas.
        //
        // Sin existencias no hay nada que caduque: un producto en cero, o uno
        // bajo pedido (que se fabrica cuando lo encargan), no avisan aunque
        // arrastren una fecha vieja de la ultima vez que se produjo.
        for (producto in productoDao.buscar("")) {
            if (producto.stockDisponible <= 0) continue
            val fecha = producto.caducidadMasCercana
            if (fecha.isNullOrBlank()) continue
            val dias = diasEntre(fechaHoy, fecha) ?: continue
            val cuanto = producto.stockDisponible.toString() + " piezas"

            if (dias < 0) {
                avisos.add(
                    Aviso(
                        producto.id, TipoAviso.CADUCADO_PRODUCTO,
                        producto.nombre,
                        "Caducó el " + fecha + " · " + cuanto
                    )
                )
            } else if (dias <= DIAS_AVISO_CADUCIDAD_PRODUCTO) {
                avisos.add(
                    Aviso(
                        producto.id, TipoAviso.CADUCIDAD_PRODUCTO,
                        producto.nombre,
                        "Caduca el " + fecha + " · " + cuanto
                    )
                )
            }
        }
        return avisos
    }

    // ---------- AVISOS: LEIDOS Y DESCARTES ----------

    override fun avisosDescartadosStream(): Flow<List<String>> =
        avisoDescartadoDao.clavesStream()

    override suspend fun avisos(fechaHoy: String): List<Aviso> {
        val vigentes = revisarStockBajo() +
                revisarStockBajoProductos() +
                revisarCaducidades(fechaHoy)

        // Primero la limpieza: las marcas de avisos que ya no existen se van.
        // Si la lista viene vacia hay que borrar todo a mano, porque el SQL
        // `NOT IN ()` con lista vacia no es valido en SQLite.
        val claves = vigentes.map { it.clave }
        if (claves.isEmpty()) avisoDescartadoDao.borrarTodos()
        else avisoDescartadoDao.borrarObsoletos(claves)

        val leidas = avisoDescartadoDao.claves().toSet()
        return vigentes.map { it.copy(leido = it.clave in leidas) }
    }

    override suspend fun marcarAvisoLeido(clave: String, fecha: String) =
        avisoDescartadoDao.descartar(AvisoDescartado(clave, fecha))

    override suspend fun marcarAvisosLeidos(claves: List<String>, fecha: String) =
        avisoDescartadoDao.descartarVarios(claves.map { AvisoDescartado(it, fecha) })

    override suspend fun restaurarAvisos() = avisoDescartadoDao.borrarTodos()

    private fun diasEntre(desde: String, hasta: String): Int? {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val a = runCatching { formato.parse(desde) }.getOrNull() ?: return null
        val b = runCatching { formato.parse(hasta) }.getOrNull() ?: return null
        return ((b.time - a.time) / (1000 * 60 * 60 * 24)).toInt()
    }

    // ---------- BITACORA ----------

    override suspend fun registrarLog(fecha: String, tipo: String, descripcion: String): RegistroLog {
        val registro = RegistroLog(UUID.randomUUID().toString(), fecha, tipo, descripcion)
        bitacoraDao.agregar(registro)
        return registro
    }

    override suspend fun consultarHistorial(textoBusqueda: String?): List<RegistroLog> =
        bitacoraDao.buscar(textoBusqueda ?: "")

    override suspend fun vistaPreviaCSV(
        encabezados: List<String>,
        filas: List<List<String>>
    ): String = ExportadorCSV.armar(encabezados, filas)

    override suspend fun exportarACSV(
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>
    ): String {
        // getExternalFilesDir es la carpeta privada de la app en el almacenamiento
        // compartido: NO pide permisos, se borra al desinstalar, y el gestor de
        // archivos del telefono si la ve. Si el telefono no tiene esa particion
        // montada devuelve null, y ahi se cae a la interna.
        val carpeta = File(contexto.getExternalFilesDir(null) ?: contexto.filesDir, "exportaciones")
        // Dispatchers.IO: escribir un archivo BLOQUEA. Room mueve sus consultas
        // a un hilo propio, pero esto es un File a pelo y nadie lo mueve por ti;
        // en el hilo principal congelaria la pantalla.
        return withContext(Dispatchers.IO) {
            ExportadorCSV.escribir(carpeta, nombreArchivo, encabezados, filas)
        }
    }

    // ============================================================
    //  VENTAS
    //
    //  Dos protecciones distintas, y las dos hacen falta:
    //
    //   - El `WHERE ... AND cantidad >= :cantidad` de `descontar` protege
    //     contra dos ventas SIMULTANEAS del mismo material.
    //   - `withTransaction` protege contra quedarse A MEDIAS: si el proceso
    //     muere entre descontar y guardarVenta, el inventario bajo y la venta
    //     no existe. Producto desaparecido.
    //
    //  OJO: `withTransaction` NO es inline, asi que dentro del bloque no se
    //  puede hacer `return`. Por eso las validaciones (las que devuelven
    //  Fallo) van AFUERA, y el bloque termina con el valor, sin `return`.
    // ============================================================

    override fun ventasStream(): Flow<List<Venta>> =
        ventaDao.todas().map { lista ->
            lista.map { fila -> fila.venta.apply { items = fila.items.toMutableList() } }
        }

    override suspend fun leerVenta(id: String): Venta? =
        ventaDao.leer(id)?.let { fila -> fila.venta.apply { items = fila.items.toMutableList() } }

    override suspend fun cancelarVenta(ventaId: String, fecha: String): Boolean {
        val fila = ventaDao.leer(ventaId) ?: return false
        if (fila.venta.cancelada) return false

        return db.withTransaction {
            // Se cancela ANTES de devolver nada. `cancelar` lleva AND cancelada = 0,
            // asi que si dos toques llegan a la vez, el segundo recibe 0 y se va.
            // Al reves —devolver y luego cancelar— dos toques devolverian el stock dos veces.
            if (ventaDao.cancelar(ventaId) == 0) {
                false
            } else {
                for (item in fila.items) {
                    val producto = productoDao.leer(item.productoId) ?: continue
                    if (producto.esBajoPedido) {
                        for (ingrediente in recetaDao.recetaDe(item.productoId)) {
                            materialDao.sumar(
                                ingrediente.materialId,
                                ingrediente.cantidadUsada * item.cantidad
                            )
                        }
                    } else {
                        productoDao.sumarStock(item.productoId, item.cantidad)
                    }
                }
                registrarLog(fecha, "manual", "Venta " + ventaId + " cancelada y devuelta al inventario")
                true
            }
        }
    }

    override suspend fun registrarVenta(
        fecha: String, hora: String, items: List<Pair<String, Int>>
    ): ResultadoVenta {
        if (items.isEmpty()) return ResultadoVenta.Fallo(ErrorVenta.TICKET_VACIO)

        // ---- Fase 0: acumular el ticket completo ----
        val materialesRequeridos = mutableMapOf<String, Double>()
        val productosRequeridos = mutableMapOf<String, Int>()
        val productos = mutableMapOf<String, Producto>()   // cache: un renglon repetido no relee la base

        for ((productoId, cantidad) in items) {
            if (cantidad <= 0) return ResultadoVenta.Fallo(ErrorVenta.CANTIDAD_INVALIDA)

            val producto = productos[productoId]
                ?: productoDao.leer(productoId)
                ?: return ResultadoVenta.Fallo(ErrorVenta.PRODUCTO_NO_EXISTE)
            productos[productoId] = producto

            if (producto.esBajoPedido) {
                // Bajo pedido: no hay pieza hecha, se gasta materia prima al vender.
                for (ingrediente in recetaDao.recetaDe(productoId)) {
                    materialesRequeridos[ingrediente.materialId] =
                        (materialesRequeridos[ingrediente.materialId] ?: 0.0) +
                                ingrediente.cantidadUsada * cantidad
                }
            } else {
                productosRequeridos[productoId] =
                    (productosRequeridos[productoId] ?: 0) + cantidad
            }
        }

        // ---- Fase 1: revisar TOTALES, no renglon por renglon ----
        // Si dos productos del ticket usan la misma harina, aqui ya vienen sumados.
        for ((materialId, requerido) in materialesRequeridos) {
            val material = materialDao.leer(materialId)
                ?: return ResultadoVenta.Fallo(ErrorVenta.MATERIALES_INSUFICIENTES)
            if (material.cantidadDisponible < requerido)
                return ResultadoVenta.Fallo(ErrorVenta.MATERIALES_INSUFICIENTES)
        }
        for ((productoId, requerido) in productosRequeridos) {
            if ((productos[productoId]?.stockDisponible ?: 0) < requerido)
                return ResultadoVenta.Fallo(ErrorVenta.STOCK_INSUFICIENTE)
        }

        // El costo congelado (RF30) se calcula ANTES de abrir la transaccion:
        // `calcularCostoProduccion` hace sus propias consultas y no hay razon
        // para tenerlas dentro del bloque que bloquea la escritura.
        val costos = productos.keys.associateWith { calcularCostoProduccion(it) }

        // ---- Fase 2 y 3: una sola operacion, o ninguna ----
        return db.withTransaction {
            materialesRequeridos.forEach { (materialId, requerido) ->
                materialDao.descontar(materialId, requerido)
            }
            productosRequeridos.forEach { (productoId, requerido) ->
                productoDao.descontarStock(productoId, requerido)
            }

            val ventaId = UUID.randomUUID().toString()
            val itemsVendidos = mutableListOf<ItemVendido>()
            var total = 0.0
            for ((productoId, cantidad) in items) {
                val producto = productos[productoId] ?: continue
                itemsVendidos.add(
                    ItemVendido(
                        productoId = productoId,
                        cantidad = cantidad,
                        precioUnitario = producto.precioVenta,          // RF30: congelado
                        costoUnitarioProduccion = costos[productoId] ?: 0.0,
                        ventaId = ventaId                               // la llave foranea
                    )
                )
                total += producto.precioVenta * cantidad
            }

            val venta = Venta(id = ventaId, fecha = fecha, hora = hora, total = total)
            venta.items = itemsVendidos

            // El orden importa: la venta PRIMERO. La llave foranea exige que exista
            // antes de que lleguen sus items, o SQLite rechaza el insert.
            ventaDao.guardarVenta(venta)
            ventaDao.guardarItems(itemsVendidos)

            registrarLog(
                fecha, "venta",
                "Venta " + folioCorto(ventaId) + " registrada por un total de " + total
            )
            ResultadoVenta.Exito(venta)   // sin `return`: es el valor del bloque
        }
    }
}

/**
 * "20" en vez de "20.0" dentro del texto de un aviso.
 *
 * Es lo mismo que `cant()` de ui/Formato.kt, escrito otra vez a proposito:
 * `data/` no importa de `ui/`. Si algun dia el formato de numeros crece
 * (separador de miles, idiomas), lo que toca es sacarlo a un sitio comun,
 * no invertir la dependencia.
 */
private fun sinCeroSobrante(v: Double): String =
    if (v % 1.0 == 0.0) v.toInt().toString() else String.format(java.util.Locale.getDefault(), "%.2f", v)

/**
 * Los ultimos seis caracteres del id, en mayusculas: "VTA-3F91C2".
 *
 * El id de una venta es un UUID de 36 caracteres. Escrito entero en la
 * bitacora ocupa el renglon completo y no dice nada; recortado por la
 * pantalla queda un "Venta 3732cc32-0994-4e91-a9a0-..." que tampoco.
 *
 * Es el gemelo de `folioDe()` de ui/Formato.kt, separado porque `data/` no
 * importa de `ui/`. Si algun dia hay un consecutivo de verdad (una columna
 * numero_de_venta), los dos se cambian por el.
 */
private fun folioCorto(id: String): String {
    val limpio = id.filter { it.isLetterOrDigit() }
    return "VTA-" + (if (limpio.length <= 6) limpio else limpio.takeLast(6)).uppercase()
}
