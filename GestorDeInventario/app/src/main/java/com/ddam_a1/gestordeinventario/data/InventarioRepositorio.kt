package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import kotlinx.coroutines.flow.Flow

// ============================================================
//  EL REPOSITORIO DEL NEGOCIO
//
//  Fijate en lo que NO hay en este archivo: ni un import de Room, ni de los
//  objetos de data/. Esa ausencia ES la capa. El ViewModel habla con esta
//  interfaz y no sabe si atras hay una lista en memoria o una base de datos.
//
//  Hoy la implementacion es InventarioRepositorioMemoria. Cuando escribas los
//  DAO, agregas InventarioRepositorioLocal y cambias UNA linea en el modulo de
//  Hilt. El ViewModel y las pantallas no se enteran.
//
//  Materiales, productos y ventas viven en la MISMA interfaz y no en tres
//  aparte porque no se pueden separar: un producto tiene una receta de
//  materiales, producir descuenta materiales, y vender descuenta productos.
//  Partirlo obligaria al ViewModel a pedirle a tres objetos lo que es una sola
//  operacion.
// ============================================================

interface InventarioRepositorio {

    // ---------- LOS FLUJOS ----------
    //
    // Un Flow es un canal: emite una lista nueva cada vez que los datos
    // cambian. Es lo que reemplaza al contador `EstadoApp.version`: ya nadie
    // avisa a nadie, la pantalla se suscribe y se redibuja sola.

    fun materialesStream(): Flow<List<Material>>
    fun productosStream(): Flow<List<Producto>>
    fun ventasStream(): Flow<List<Venta>>
    fun bitacoraStream(): Flow<List<RegistroLog>>

    // ---------- MATERIALES ----------

    suspend fun agregarMaterial(nombre: String, unidad: String, costo: Double, cantidad: Double): Material
    suspend fun editarMaterial(id: String, nombre: String? = null, costo: Double? = null): Boolean
    suspend fun eliminarMaterial(id: String): Boolean
    suspend fun agregarFechaCaducidad(materialId: String, fecha: String): Boolean
    suspend fun definirStockMinimo(materialId: String, minimo: Double): Boolean
    suspend fun definirDiasAvisoCaducidad(materialId: String, dias: Int): Boolean

    /**
     * Suma existencias a un material: una compra, una reposicion.
     *
     * Faltaba por completo. `editarMaterial` solo cambia nombre y costo, asi
     * que la unica forma de tener cantidad era al darlo de alta; despues de eso
     * el inventario solo podia bajar.
     */
    suspend fun agregarExistenciasMaterial(materialId: String, cantidad: Double): Boolean

    /** Lee uno por id. Es `suspend` porque con Room va a consultar la base. */
    suspend fun leerMaterial(id: String): Material?

    /** La busqueda vive aqui, no en la pantalla: manana esto es un WHERE en SQL. */
    suspend fun buscarMaterial(texto: String): List<Material>

    /** Calculo puro sobre un material que ya tienes. Por eso no es suspend. */
    fun esStockBajo(material: Material): Boolean

    // ---------- PRODUCTOS ----------

    suspend fun crearProducto(nombre: String, precioVenta: Double, esBajoPedido: Boolean): Producto
    suspend fun editarProducto(id: String, nombre: String? = null, precioVenta: Double? = null): Boolean
    suspend fun eliminarProducto(id: String): Boolean
    suspend fun agregarIngredienteReceta(productoId: String, materialId: String, cantidadUsada: Double): Boolean
    /**
     * Deja la receta EXACTAMENTE con estos ingredientes: borra lo que hubiera y
     * pone lo nuevo.
     *
     * Antes la pantalla hacia `producto.receta.clear()` y luego agregaba uno por
     * uno. Eso es escribir en la base desde la vista. Aqui es una sola
     * operacion, y con Room sera un DELETE + INSERT en una transaccion.
     */
    suspend fun reemplazarReceta(productoId: String, ingredientes: Map<String, Double>): Boolean

    suspend fun asignarPrecioVenta(productoId: String, precio: Double): Boolean
    suspend fun registrarExistencias(productoId: String, cantidad: Int, descontarMaterialesAhora: Boolean): Boolean
    /** Umbral de aviso del producto, en piezas (RF19). */
    suspend fun definirStockMinimoProducto(productoId: String, minimo: Int): Boolean

    suspend fun leerProducto(id: String): Producto?
    suspend fun buscarProducto(texto: String): List<Producto>
    suspend fun calcularCostoProduccion(productoId: String): Double

    // ---------- VENTAS ----------

    suspend fun registrarVenta(fecha: String, items: List<Pair<String, Int>>): ResultadoVenta
    suspend fun cancelarVenta(ventaId: String, fecha: String): Boolean
    suspend fun leerVenta(id: String): Venta?

    // ---------- AVISOS ----------
    //
    // Estos SI son acceso a datos aunque parezcan calculo: leen el inventario
    // completo para decidir que avisar.

    suspend fun revisarStockBajo(): List<Aviso>
    suspend fun revisarStockBajoProductos(): List<Aviso>
    suspend fun revisarCaducidadesProximas(fechaHoy: String): List<Aviso>

    // ---------- BITACORA Y EXPORTACION ----------

    suspend fun registrarLog(fecha: String, tipo: String, descripcion: String): RegistroLog
    suspend fun consultarHistorial(textoBusqueda: String? = null): List<RegistroLog>
    suspend fun exportarACSV(
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>,
        contrasena: String
    ): String
}
