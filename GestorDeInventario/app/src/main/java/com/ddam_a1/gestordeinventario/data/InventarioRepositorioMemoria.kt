package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// ============================================================
//  LA IMPLEMENTACION DE HOY: TODO EN MEMORIA
//
//  Envuelve los objetos que ya tenias (InventarioMateriales, CatalogoProductos,
//  Ventas, Notificaciones, AlmacenamientoLocal) y les pone dos cosas encima:
//
//    1. FLUJOS. Cada vez que algo cambia, se vuelve a publicar la lista en un
//       MutableStateFlow. Eso es lo que hace que las pantallas se redibujen
//       solas, sin el contador `EstadoApp.version`.
//
//    2. `suspend`. Hoy no hace falta, porque leer una lista en memoria es
//       instantaneo. Se pone desde ahora para que las firmas ya sean las
//       definitivas: cuando Room entre, consultar la base SI tarda, y si las
//       firmas no fueran suspend habria que cambiar el ViewModel entero.
//
//  Cuando escribas los DAO, este archivo se borra y llega
//  InventarioRepositorioLocal. Nada mas arriba cambia.
// ============================================================

@Singleton
class InventarioRepositorioMemoria @Inject constructor() : InventarioRepositorio {

    private val _materiales = MutableStateFlow(InventarioMateriales.obtenerTodos())
    private val _productos = MutableStateFlow(CatalogoProductos.obtenerTodos())
    private val _ventas = MutableStateFlow(Ventas.obtenerHistorialVentas())
    private val _bitacora = MutableStateFlow(AlmacenamientoLocal.consultarHistorial())

    // Volver a leer y publicar. Con Room esto desaparece: la base avisa sola.
    private fun refrescarMateriales() { _materiales.value = InventarioMateriales.obtenerTodos() }
    private fun refrescarProductos() { _productos.value = CatalogoProductos.obtenerTodos() }
    private fun refrescarVentas() { _ventas.value = Ventas.obtenerHistorialVentas() }
    private fun refrescarBitacora() { _bitacora.value = AlmacenamientoLocal.consultarHistorial() }

    override fun materialesStream(): Flow<List<Material>> = _materiales.asStateFlow()
    override fun productosStream(): Flow<List<Producto>> = _productos.asStateFlow()
    override fun ventasStream(): Flow<List<Venta>> = _ventas.asStateFlow()
    override fun bitacoraStream(): Flow<List<RegistroLog>> = _bitacora.asStateFlow()

    // ---------- MATERIALES ----------

    override suspend fun agregarMaterial(nombre: String, unidad: String, costo: Double, cantidad: Double): Material {
        val material = InventarioMateriales.agregarMaterial(nombre, unidad, costo, cantidad)
        refrescarMateriales()
        return material
    }

    override suspend fun editarMaterial(id: String, nombre: String?, costo: Double?): Boolean {
        val ok = InventarioMateriales.editarMaterial(id, nombre, costo)
        refrescarMateriales()
        return ok
    }

    override suspend fun eliminarMaterial(id: String): Boolean {
        val ok = InventarioMateriales.eliminarMaterial(id)
        refrescarMateriales()
        return ok
    }

    override suspend fun agregarFechaCaducidad(materialId: String, fecha: String): Boolean {
        val ok = InventarioMateriales.agregarFechaCaducidad(materialId, fecha)
        refrescarMateriales()
        return ok
    }

    override suspend fun definirStockMinimo(materialId: String, minimo: Double): Boolean {
        val ok = InventarioMateriales.definirStockMinimo(materialId, minimo)
        refrescarMateriales()
        return ok
    }

    override suspend fun definirDiasAvisoCaducidad(materialId: String, dias: Int): Boolean {
        val ok = InventarioMateriales.definirDiasAvisoCaducidad(materialId, dias)
        refrescarMateriales()
        return ok
    }

    override suspend fun leerMaterial(id: String): Material? =
        InventarioMateriales.obtenerMaterialPorId(id)

    override suspend fun buscarMaterial(texto: String): List<Material> =
        InventarioMateriales.buscarMaterial(texto)

    override fun esStockBajo(material: Material): Boolean =
        InventarioMateriales.esStockBajo(material)

    // ---------- PRODUCTOS ----------

    override suspend fun crearProducto(nombre: String, precioVenta: Double, esBajoPedido: Boolean): Producto {
        val producto = CatalogoProductos.crearProducto(nombre, precioVenta, esBajoPedido)
        refrescarProductos()
        return producto
    }

    override suspend fun editarProducto(id: String, nombre: String?, precioVenta: Double?): Boolean {
        val ok = CatalogoProductos.editarProducto(id, nombre, precioVenta)
        refrescarProductos()
        return ok
    }

    override suspend fun eliminarProducto(id: String): Boolean {
        val ok = CatalogoProductos.eliminarProducto(id)
        refrescarProductos()
        return ok
    }

    override suspend fun agregarIngredienteReceta(productoId: String, materialId: String, cantidadUsada: Double): Boolean {
        val ok = CatalogoProductos.agregarIngredienteReceta(productoId, materialId, cantidadUsada)
        refrescarProductos()
        return ok
    }

    override suspend fun asignarPrecioVenta(productoId: String, precio: Double): Boolean {
        val ok = CatalogoProductos.asignarPrecioVenta(productoId, precio)
        refrescarProductos()
        return ok
    }

    // Produccion toca las DOS listas: descuenta materiales y sube existencias.
    override suspend fun registrarExistencias(productoId: String, cantidad: Int, descontarMaterialesAhora: Boolean): Boolean {
        val ok = CatalogoProductos.registrarExistencias(productoId, cantidad, descontarMaterialesAhora)
        refrescarProductos()
        refrescarMateriales()
        return ok
    }

    override suspend fun leerProducto(id: String): Producto? =
        CatalogoProductos.obtenerProductoPorId(id)

    override suspend fun buscarProducto(texto: String): List<Producto> =
        CatalogoProductos.buscarProducto(texto)

    override suspend fun calcularCostoProduccion(productoId: String): Double =
        CatalogoProductos.calcularCostoProduccion(productoId)

    // ---------- VENTAS ----------

    // Vender mueve las tres listas: la venta, el stock del producto y, si es
    // bajo pedido, los materiales.
    override suspend fun registrarVenta(fecha: String, items: List<Pair<String, Int>>): ResultadoVenta {
        val resultado = Ventas.registrarVenta(fecha, items)
        refrescarVentas()
        refrescarProductos()
        refrescarMateriales()
        refrescarBitacora()
        return resultado
    }

    override suspend fun cancelarVenta(ventaId: String, fecha: String): Boolean {
        val ok = Ventas.cancelarVenta(ventaId, fecha)
        refrescarVentas()
        refrescarProductos()
        refrescarMateriales()
        refrescarBitacora()
        return ok
    }

    override suspend fun leerVenta(id: String): Venta? = Ventas.obtenerVentaPorId(id)

    // ---------- AVISOS ----------

    override suspend fun revisarStockBajo(): List<Aviso> = Notificaciones.revisarStockBajo()

    override suspend fun revisarCaducidadesProximas(fechaHoy: String): List<Aviso> =
        Notificaciones.revisarCaducidadesProximas(fechaHoy)

    // ---------- BITACORA ----------

    override suspend fun registrarLog(fecha: String, tipo: String, descripcion: String): RegistroLog {
        val registro = AlmacenamientoLocal.registrarLog(fecha, tipo, descripcion)
        refrescarBitacora()
        return registro
    }

    override suspend fun consultarHistorial(textoBusqueda: String?): List<RegistroLog> =
        AlmacenamientoLocal.consultarHistorial(textoBusqueda)

    override suspend fun exportarACSV(
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>,
        contrasena: String
    ): String = AlmacenamientoLocal.exportarACSV(nombreArchivo, encabezados, filas, contrasena)
}
