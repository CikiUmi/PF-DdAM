package com.ddam_a1.gestordeinventario.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddam_a1.gestordeinventario.data.InventarioRepositorio
import com.ddam_a1.gestordeinventario.data.RendimientoNegocio
import com.ddam_a1.gestordeinventario.data.ResultadoVenta
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.modelClasses.Periodo
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============================================================
//  EL VIEWMODEL DEL NEGOCIO
//
//  Materiales, productos, ventas, avisos y estadisticas viven juntos porque las
//  pantallas los piden juntos. Se midio: DetalleProducto, Receta y Produccion
//  usan materiales Y productos; HistorialVentas usa productos Y ventas; Inicio
//  usa los cinco. Un ViewModel por tabla obligaria a Inicio a pedir cuatro.
//
//  El ViewModel DECIDE (valida, ordena, calcula) y el repositorio EJECUTA.
//  Nunca toca Room ni los objetos de data/ directamente: solo la interfaz.
// ============================================================

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val repo: InventarioRepositorio
) : ViewModel() {

    // ---------- LAS LISTAS ----------
    //
    // `stateIn` convierte el Flow del repositorio en un StateFlow, que es un
    // Flow que SIEMPRE tiene un valor actual. La pantalla se suscribe con
    // collectAsState() y se redibuja sola cuando llega una emision nueva.
    //
    //   viewModelScope ......... vive mientras viva el ViewModel
    //   WhileSubscribed(5000) .. suelta el flow 5 segundos despues de que la
    //                            ultima pantalla se va. Los 5 segundos son para
    //                            que una rotacion de pantalla no lo reinicie.
    //   emptyList() ............ que mostrar mientras llega el primer dato

    val materiales: StateFlow<List<Material>> = repo.materialesStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productos: StateFlow<List<Producto>> = repo.productosStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ventas: StateFlow<List<Venta>> = repo.ventasStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bitacora: StateFlow<List<RegistroLog>> = repo.bitacoraStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** RF21: que metricas se muestran en el menu principal. */
    private val _metricasElegidas = MutableStateFlow(
        listOf("Ingresos", "Ganancias", "Productos mas vendidos", "Perdidas")
    )
    val metricasElegidas: StateFlow<List<String>> = _metricasElegidas.asStateFlow()

    fun elegirMetricas(metricas: List<String>) { _metricasElegidas.value = metricas }

    /**
     * Los avisos, en vivo.
     *
     * NO lleva `stateIn` a proposito: depende de la fecha que le pase la
     * pantalla, y un StateFlow por fecha habria que guardarlo en un mapa y
     * limpiarlo a mano. La pantalla lo recoge con collectAsState(emptyList()).
     *
     * Cuelga de `materialesStream` para que se recalcule solo cuando cambie el
     * inventario: si baja el stock de algo, el aviso aparece sin que nadie
     * llame a nada.
     */
    fun avisos(fechaHoy: String): Flow<List<Aviso>> =
        combine(repo.materialesStream(), repo.productosStream()) { _, _ ->
            repo.revisarStockBajo() +
                repo.revisarStockBajoProductos() +
                repo.revisarCaducidadesProximas(fechaHoy)
        }

    // ---------- MATERIALES ----------
    //
    // Las que no devuelven nada usan `launch`: mandan la orden y se acaban. Las
    // que la pantalla necesita de vuelta (para navegar, o para saber si fallo)
    // son `suspend` y la pantalla las llama desde su propio scope.

    fun agregarMaterial(nombre: String, unidad: String, costo: Double, cantidad: Double) {
        if (nombre.isBlank() || unidad.isBlank()) return
        viewModelScope.launch { repo.agregarMaterial(nombre.trim(), unidad.trim(), costo, cantidad) }
    }

    fun editarMaterial(id: String, nombre: String? = null, costo: Double? = null) {
        viewModelScope.launch { repo.editarMaterial(id, nombre?.trim(), costo) }
    }

    fun eliminarMaterial(id: String) { viewModelScope.launch { repo.eliminarMaterial(id) } }

    /**
     * Registra un LOTE que entra al inventario: cuanto y, si el material
     * caduca, cuando.
     *
     * Van juntos porque son una sola cosa. Antes eran dos botones distintos
     * (sumar cantidad por un lado, agregar fecha por el otro) y nada obligaba a
     * que coincidieran: podias sumar 10 kg sin decir cuando caducan, o registrar
     * una caducidad de un lote que nunca entro.
     *
     * `caducidad` en null o vacio significa que este material no caduca.
     */
    fun registrarEntradaMaterial(
        materialId: String,
        nombreMaterial: String,
        cantidad: Double,
        unidad: String,
        caducidad: String?,
        fecha: String
    ) {
        if (cantidad <= 0.0) return
        viewModelScope.launch {
            repo.agregarExistenciasMaterial(materialId, cantidad)

            val tieneCaducidad = !caducidad.isNullOrBlank()
            if (tieneCaducidad) {
                repo.agregarFechaCaducidad(materialId, caducidad!!.trim())
            }

            repo.registrarLog(
                fecha, "manual",
                "Entrada de " + cantidad + " " + unidad + " de " + nombreMaterial +
                    (if (tieneCaducidad) " (caduca el " + caducidad!!.trim() + ")" else "")
            )
        }
    }

    fun agregarFechaCaducidad(materialId: String, fecha: String) {
        if (fecha.isBlank()) return
        viewModelScope.launch { repo.agregarFechaCaducidad(materialId, fecha) }
    }

    fun definirStockMinimo(materialId: String, minimo: Double) {
        viewModelScope.launch { repo.definirStockMinimo(materialId, minimo) }
    }

    fun definirDiasAvisoCaducidad(materialId: String, dias: Int) {
        viewModelScope.launch { repo.definirDiasAvisoCaducidad(materialId, dias) }
    }

    /**
     * Da de alta o edita un material, COMPLETO: nombre, costo, stock minimo,
     * dias de aviso y la entrada en la bitacora.
     *
     * Antes esto vivia dentro del boton "Guardar" de la pantalla: cinco
     * llamadas seguidas a los objetos de data/, y si fallaba la tercera el
     * material quedaba a medias. Aqui es UNA operacion y la pantalla solo
     * entrega los datos del formulario.
     *
     * `fecha` la manda quien llama porque `hoy()` vive en ui/Formato.kt y el
     * ViewModel no debe importar nada de ui.
     */
    fun guardarMaterial(
        id: String?,
        nombre: String,
        unidad: String,
        cantidad: Double,
        costo: Double,
        stockMinimo: Double,
        diasAvisoCaducidad: Int,
        fecha: String
    ) {
        if (nombre.isBlank() || unidad.isBlank()) return
        viewModelScope.launch {
            val destino = if (id == null) {
                repo.agregarMaterial(nombre.trim(), unidad.trim(), costo, cantidad)
            } else {
                repo.editarMaterial(id, nombre.trim(), costo)
                repo.leerMaterial(id) ?: return@launch
            }
            repo.definirStockMinimo(destino.id, stockMinimo)
            repo.definirDiasAvisoCaducidad(destino.id, diasAvisoCaducidad)
            repo.registrarLog(
                fecha, "manual",
                (if (id == null) "Alta" else "Edicion") + " de material " + destino.nombre
            )
        }
    }

    suspend fun leerMaterial(id: String): Material? = repo.leerMaterial(id)
    suspend fun buscarMaterial(texto: String): List<Material> = repo.buscarMaterial(texto)
    fun esStockBajo(material: Material): Boolean = repo.esStockBajo(material)

    // ---------- PRODUCTOS ----------

    /** Devuelve el producto creado porque la pantalla navega a su receta. */
    suspend fun crearProducto(nombre: String, precioVenta: Double, esBajoPedido: Boolean): Producto? {
        if (nombre.isBlank()) return null
        return repo.crearProducto(nombre.trim(), precioVenta, esBajoPedido)
    }

    fun editarProducto(id: String, nombre: String? = null, precioVenta: Double? = null) {
        viewModelScope.launch { repo.editarProducto(id, nombre?.trim(), precioVenta) }
    }

    fun eliminarProducto(id: String) { viewModelScope.launch { repo.eliminarProducto(id) } }

    fun agregarIngredienteReceta(productoId: String, materialId: String, cantidadUsada: Double) {
        if (cantidadUsada <= 0.0) return
        viewModelScope.launch { repo.agregarIngredienteReceta(productoId, materialId, cantidadUsada) }
    }

    fun asignarPrecioVenta(productoId: String, precio: Double) {
        viewModelScope.launch { repo.asignarPrecioVenta(productoId, precio) }
    }

    /** Devuelve si alcanzo, porque la pantalla tiene que avisar si no. */
    suspend fun registrarExistencias(productoId: String, cantidad: Int, descontarMaterialesAhora: Boolean): Boolean =
        repo.registrarExistencias(productoId, cantidad, descontarMaterialesAhora)

    /**
     * El costo de produccion de cada producto, calculado sobre las dos listas
     * que la pantalla YA tiene.
     *
     * Es una funcion pura, sin `suspend`, a proposito: al depender solo de
     * `productos` y `materiales`, se recalcula sola cuando cualquiera de las dos
     * cambia. Si subes el precio de la harina, el costo de todos los panes se
     * actualiza sin que nadie llame a nada.
     */
    fun costosDeProduccion(
        productos: List<Producto>,
        materiales: List<Material>
    ): Map<String, Double> = productos.associate { producto ->
        producto.id to producto.receta.sumOf { ingrediente ->
            val material = materiales.find { it.id == ingrediente.materialId }
            (material?.costoUnitario ?: 0.0) * ingrediente.cantidadUsada
        }
    }

    /**
     * Da de alta o edita un producto y lo anota en la bitacora.
     * Devuelve el id porque al crear uno nuevo la pantalla se va a su receta.
     */
    suspend fun guardarProducto(
        id: String?,
        nombre: String,
        precioVenta: Double,
        esBajoPedido: Boolean,
        stockMinimo: Int,
        fecha: String
    ): String? {
        if (nombre.isBlank()) return null
        // Un producto bajo pedido no tiene stock, asi que tampoco umbral.
        val umbral = if (esBajoPedido) 0 else stockMinimo
        return if (id == null) {
            val creado = repo.crearProducto(nombre.trim(), precioVenta, esBajoPedido)
            repo.definirStockMinimoProducto(creado.id, umbral)
            repo.registrarLog(fecha, "manual", "Alta de producto " + creado.nombre)
            creado.id
        } else {
            repo.editarProducto(id, nombre.trim(), precioVenta)
            val existente = repo.leerProducto(id)
            if (existente != null) {
                existente.esBajoPedido = esBajoPedido
                repo.definirStockMinimoProducto(id, umbral)
                repo.registrarLog(fecha, "manual", "Edicion de producto " + existente.nombre)
            }
            id
        }
    }

    fun guardarReceta(productoId: String, ingredientes: Map<String, Double>) {
        viewModelScope.launch { repo.reemplazarReceta(productoId, ingredientes) }
    }

    /**
     * Registra un lote producido. Devuelve false si no alcanzaron los
     * materiales, para que la pantalla lo diga en vez de fallar callada.
     */
    suspend fun registrarProduccion(
        productoId: String,
        cantidad: Int,
        descontarMateriales: Boolean,
        fecha: String
    ): Boolean {
        if (cantidad <= 0) return false
        val ok = repo.registrarExistencias(productoId, cantidad, descontarMateriales)
        if (ok) {
            val producto = repo.leerProducto(productoId)
            val nota = if (descontarMateriales) " (materiales descontados)" else " (sin descontar)"
            repo.registrarLog(
                fecha, "manual",
                "Produccion de " + cantidad + " " + (producto?.nombre ?: "") + nota
            )
        }
        return ok
    }

    suspend fun leerProducto(id: String): Producto? = repo.leerProducto(id)
    suspend fun buscarProducto(texto: String): List<Producto> = repo.buscarProducto(texto)
    suspend fun calcularCostoProduccion(productoId: String): Double = repo.calcularCostoProduccion(productoId)

    // ---------- VENTAS ----------

    /** Devuelve el resultado completo: la pantalla decide que mensaje mostrar. */
    suspend fun registrarVenta(fecha: String, items: List<Pair<String, Int>>): ResultadoVenta =
        repo.registrarVenta(fecha, items)

    fun cancelarVenta(ventaId: String, fecha: String) {
        viewModelScope.launch { repo.cancelarVenta(ventaId, fecha) }
    }

    // ---------- METRICAS ----------
    //
    // RendimientoNegocio son calculos puros sobre una lista que ya tienes: no
    // leen ni escriben nada. Por eso no pasan por el repositorio ni son
    // suspend. Se exponen aqui nada mas para que las pantallas no tengan que
    // importar data/.

    fun calcularIngresos(ventas: List<Venta>): Double = RendimientoNegocio.calcularIngresos(ventas)
    fun calcularGanancias(ventas: List<Venta>): Double = RendimientoNegocio.calcularGanancias(ventas)
    fun calcularPerdidas(ventas: List<Venta>): Double = RendimientoNegocio.calcularPerdidas(ventas)

    fun productosMasVendidos(ventas: List<Venta>, top: Int = 5): List<Pair<String, Int>> =
        RendimientoNegocio.productosMasVendidos(ventas, top)

    fun filtrarVentasPorPeriodo(ventas: List<Venta>, periodo: Periodo, fechaReferencia: String): List<Venta> =
        RendimientoNegocio.filtrarVentasPorPeriodo(ventas, periodo, fechaReferencia)

    fun metricasDisponibles(): List<String> = RendimientoNegocio.metricasDisponibles()

    // ---------- BITACORA Y EXPORTACION ----------

    fun registrarLog(fecha: String, tipo: String, descripcion: String) {
        viewModelScope.launch { repo.registrarLog(fecha, tipo, descripcion) }
    }

    suspend fun consultarHistorial(textoBusqueda: String? = null): List<RegistroLog> =
        repo.consultarHistorial(textoBusqueda)

    suspend fun exportarACSV(
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>,
        contrasena: String
    ): String = repo.exportarACSV(nombreArchivo, encabezados, filas, contrasena)
}
