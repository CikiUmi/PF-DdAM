package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.modelClasses.IngredienteReceta
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import java.util.UUID

/**
 * Módulo: Catálogo de productos
 * RF4, RF5, RF6, RF7, RF8, RF10, RF11
 */
object CatalogoProductos {

    private val productos = mutableListOf<Producto>()

    // RF4: Crear producto y definir si es bajo pedido o si maneja stock
    fun crearProducto(nombre: String, precioVenta: Double, esBajoPedido: Boolean): Producto {
        val producto = Producto(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            precioVenta = precioVenta,
            esBajoPedido = esBajoPedido
        )
        productos.add(producto)
        return producto
    }

    // RF4: Editar producto
    fun editarProducto(id: String, nombre: String? = null, precioVenta: Double? = null): Boolean {
        val producto = productos.find { it.id == id } ?: return false
        nombre?.let { producto.nombre = it }
        // RF30: al cambiar el precio, las ventas ya registradas NO se ven afectadas
        // porque cada venta guarda su propio "precioUnitario" congelado.
        precioVenta?.let { producto.precioVenta = it }
        return true
    }

    fun eliminarProducto(id: String): Boolean = productos.removeIf { it.id == id }

    // RF8: Buscar / filtrar producto
    fun buscarProducto(texto: String): List<Producto> =
        productos.filter { it.nombre.contains(texto, ignoreCase = true) }

    // RF6: Seleccionar materiales usados y su cantidad (receta) para elaborar el producto
    fun agregarIngredienteReceta(productoId: String, materialId: String, cantidadUsada: Double): Boolean {
        val producto = productos.find { it.id == productoId } ?: return false
        producto.receta.add(IngredienteReceta(materialId, cantidadUsada))
        return true
    }

    // RF5: Calcular el costo de producción según los materiales y cantidades de la receta
    fun calcularCostoProduccion(productoId: String): Double {
        val producto = productos.find { it.id == productoId } ?: return 0.0
        var costoTotal = 0.0
        for (ingrediente in producto.receta) {
            val material = InventarioMateriales.obtenerMaterialPorId(ingrediente.materialId) ?: continue
            costoTotal += material.costoUnitario * ingrediente.cantidadUsada
        }
        producto.costoProduccion = costoTotal
        return costoTotal
    }

    // RF7: Asignar precio de venta al producto
    fun asignarPrecioVenta(productoId: String, precio: Double): Boolean {
        val producto = productos.find { it.id == productoId } ?: return false
        producto.precioVenta = precio
        return true
    }

    // RF10, RF11: Registrar existencias (stock) de un producto.
    // Conserva la caducidad más cercana de los materiales que lo conforman
    // y pregunta (parámetro descontarMaterialesAhora) si se descuentan del inventario o no.
    fun registrarExistencias(productoId: String, cantidad: Int, descontarMaterialesAhora: Boolean): Boolean {
        if (cantidad <= 0) return false
        val producto = productos.find { it.id == productoId } ?: return false

        // Si se van a descontar, primero se verifica que TODOS los materiales alcancen.
        if (descontarMaterialesAhora) {
            val requerido = mutableMapOf<String, Double>()
            for (ingrediente in producto.receta) {
                requerido[ingrediente.materialId] =
                    (requerido[ingrediente.materialId] ?: 0.0) + ingrediente.cantidadUsada * cantidad
            }
            val alcanzaTodo = requerido.all { (materialId, cantidadRequerida) ->
                InventarioMateriales.hayCantidadSuficiente(materialId, cantidadRequerida)
            }
            if (!alcanzaTodo) return false
            requerido.forEach { (materialId, cantidadRequerida) ->
                InventarioMateriales.descontarCantidad(materialId, cantidadRequerida)
            }
        }

        producto.stockDisponible += cantidad

        // RF10: la caducidad del producto es la más cercana entre la que ya tenía
        // y la de los materiales del lote nuevo (nunca se reemplaza por una posterior).
        val caducidadDelLote = producto.receta
            .mapNotNull { InventarioMateriales.obtenerMaterialPorId(it.materialId)?.fechasCaducidad?.minOrNull() }
            .minOrNull()
        producto.caducidadMasCercana =
            listOfNotNull(producto.caducidadMasCercana, caducidadDelLote).minOrNull()

        return true
    }

    fun obtenerProductoPorId(id: String): Producto? = productos.find { it.id == id }

    fun obtenerTodos(): List<Producto> = productos.toList()
}
