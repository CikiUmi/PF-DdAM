package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.data.dao.BitacoraDao
import com.ddam_a1.gestordeinventario.data.dao.LoteDao
import com.ddam_a1.gestordeinventario.data.dao.MaterialDao
import com.ddam_a1.gestordeinventario.data.dao.ProductoDao
import com.ddam_a1.gestordeinventario.data.dao.RecetaDao
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.IngredienteReceta
import com.ddam_a1.gestordeinventario.modelClasses.LoteMaterial
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.TipoAviso
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// ============================================================
//  EL INVENTARIO, CONTRA ROOM
//
//  Reemplaza a InventarioRepositorioMemoria. Ni el ViewModel ni las 25
//  pantallas se enteran: la interfaz es la misma.
//
//  Lo que desaparecio: todos los `refrescarX()` que habia que llamar a mano
//  despues de cada escritura. Room emite sola cuando la tabla cambia.
//
//  Lo que aparecio: armar los objetos. `Material.fechasCaducidad` y
//  `Producto.receta` no son columnas, viven en sus propias tablas, asi que hay
//  que juntar las piezas al leer.
// ============================================================

@Singleton
class InventarioRepositorioLocal @Inject constructor(
    private val materialDao: MaterialDao,
    private val loteDao: LoteDao,
    private val productoDao: ProductoDao,
    private val recetaDao: RecetaDao,
    private val bitacoraDao: BitacoraDao
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
                    fechasCaducidad = lotes
                        .filter { it.materialId == material.id }
                        .map { it.caducidad }
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
        nombre: String, unidad: String, costo: Double, cantidad: Double
    ): Material {
        val material = Material(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            unidadMedida = unidad,
            costoUnitario = costo,
            cantidadDisponible = cantidad
        )
        materialDao.guardar(material)
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

    override suspend fun agregarFechaCaducidad(materialId: String, fecha: String): Boolean {
        if (fecha.isBlank()) return false
        loteDao.agregar(LoteMaterial(materialId = materialId, caducidad = fecha.trim()))
        return true
    }

    override suspend fun definirStockMinimo(materialId: String, minimo: Double): Boolean =
        materialDao.definirStockMinimo(materialId, minimo) > 0

    override suspend fun definirDiasAvisoCaducidad(materialId: String, dias: Int): Boolean =
        materialDao.definirDiasAviso(materialId, dias) > 0

    override suspend fun leerMaterial(id: String): Material? =
        materialDao.leer(id)?.apply {
            fechasCaducidad = loteDao.lotesDe(id).map { it.caducidad }.toMutableList()
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
                Aviso(it.id, TipoAviso.STOCK_BAJO_MATERIAL,
                    "El material '" + it.nombre + "' esta bajo en inventario")
            }

    override suspend fun revisarStockBajoProductos(): List<Aviso> =
        productoDao.buscar("")
            .filter { !it.esBajoPedido && it.stockMinimo > 0 && it.stockDisponible <= it.stockMinimo }
            .map {
                Aviso(it.id, TipoAviso.STOCK_BAJO_PRODUCTO,
                    "Quedan " + it.stockDisponible + " piezas de '" + it.nombre + "'")
            }

    override suspend fun revisarCaducidadesProximas(fechaHoy: String): List<Aviso> {
        val avisos = mutableListOf<Aviso>()
        for (material in materialDao.buscar("")) {
            if (material.diasAvisoCaducidad <= 0) continue
            for (lote in loteDao.lotesDe(material.id)) {
                val dias = diasEntre(fechaHoy, lote.caducidad) ?: continue
                if (dias in 0..material.diasAvisoCaducidad) {
                    avisos.add(
                        Aviso(material.id, TipoAviso.CADUCIDAD,
                            "El material '" + material.nombre + "' caduca el " + lote.caducidad)
                    )
                }
            }
        }
        return avisos
    }

    private fun diasEntre(desde: String, hasta: String): Int? {
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
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

    override suspend fun exportarACSV(
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>,
        contrasena: String
    ): String = AlmacenamientoLocal.exportarACSV(nombreArchivo, encabezados, filas, contrasena)

    // ============================================================
    //  VENTAS  ←  LA PARTE QUE FALTA, LA HACEMOS JUNTAS
    //
    //  Todo lo de abajo esta sin implementar a proposito. Lo que hay que hacer:
    //
    //   1. `Venta` e `ItemVendido` como @Entity. Un ItemVendido apunta a su
    //      venta con `venta_id` (ForeignKey + CASCADE + indice), igual que los
    //      lotes con su material. Los precios van CONGELADOS en el item: eso ya
    //      lo tenias resuelto (RF30) y con Room se vuelve obvio por que.
    //   2. `VentaDao` con @Transaction y @Relation para leer una venta con sus
    //      items de un jalon. Es lo unico de este proyecto que necesita
    //      @Relation de verdad.
    //   3. Agregar las dos entidades a GestorDatabase y SUBIR version a 2.
    //   4. `registrarVenta`: dos fases como en produccion, pero acumulando los
    //      requerimientos de TODO el ticket antes de tocar nada — dos renglones
    //      del mismo producto tienen que sumar.
    // ============================================================

    override fun ventasStream(): Flow<List<Venta>> = flowOf(emptyList())

    override suspend fun registrarVenta(
        fecha: String, items: List<Pair<String, Int>>
    ): ResultadoVenta = ResultadoVenta.Fallo(ErrorVenta.VENTAS_NO_DISPONIBLES)

    override suspend fun cancelarVenta(ventaId: String, fecha: String): Boolean = false

    override suspend fun leerVenta(id: String): Venta? = null
}
