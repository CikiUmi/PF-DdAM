package com.ddam_a1.gestordeinventario.inventario

import com.ddam_a1.gestordeinventario.modelo.Material
import java.util.UUID

/**
 * Módulo: Inventario de Materiales
 * RF1, RF2, RF3, RF9, RF18, RF19, RF20, RF22
 */
object InventarioMateriales {

    private val materiales = mutableListOf<Material>()

    // RF1: Añadir material al inventario
    fun agregarMaterial(nombre: String, unidad: String, costo: Double, cantidad: Double): Material {
        val material = Material(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            unidadMedida = unidad,
            costoUnitario = costo,
            cantidadDisponible = cantidad
        )
        materiales.add(material)
        return material
    }

    // RF1: Editar material existente
    fun editarMaterial(id: String, nombre: String? = null, costo: Double? = null): Boolean {
        val material = materiales.find { it.id == id } ?: return false
        nombre?.let { material.nombre = it }
        costo?.let { material.costoUnitario = it } // RF2: costo unitario
        return true
    }

    // RF1: Eliminar material
    fun eliminarMaterial(id: String): Boolean = materiales.removeIf { it.id == id }

    // RF20: Buscar / filtrar materiales por nombre
    fun buscarMaterial(texto: String): List<Material> =
        materiales.filter { it.nombre.contains(texto, ignoreCase = true) }

    // RF3, RF9: Agregar fecha de caducidad opcional (un material puede tener varias)
    fun agregarFechaCaducidad(materialId: String, fecha: String): Boolean {
        val material = materiales.find { it.id == materialId } ?: return false
        material.fechasCaducidad.add(fecha)
        return true
    }

    // RF19: Definir la cantidad que se considera "stock bajo"
    fun definirStockMinimo(materialId: String, minimo: Double): Boolean {
        val material = materiales.find { it.id == materialId } ?: return false
        material.stockMinimo = minimo
        return true
    }

    // RF18: Definir con cuánta antelación (días) se notifica la caducidad
    fun definirDiasAvisoCaducidad(materialId: String, dias: Int): Boolean {
        val material = materiales.find { it.id == materialId } ?: return false
        material.diasAvisoCaducidad = dias
        return true
    }

    // RF19: Verificar si un material está en stock bajo
    fun esStockBajo(material: Material): Boolean = material.cantidadDisponible <= material.stockMinimo

    // Descontar cantidad de un material (usado por Ventas y Productos al elaborar/vender)
    fun descontarCantidad(materialId: String, cantidad: Double): Boolean {
        val material = materiales.find { it.id == materialId } ?: return false
        if (material.cantidadDisponible < cantidad) return false
        material.cantidadDisponible -= cantidad
        return true
    }

    // Devuelve cantidad al inventario (usado al revertir una venta en proceso)
    fun devolverCantidad(materialId: String, cantidad: Double): Boolean {
        val material = materiales.find { it.id == materialId } ?: return false
        material.cantidadDisponible += cantidad
        return true
    }

    // Verifica si hay suficiente cantidad disponible sin descontar todavía
    fun hayCantidadSuficiente(materialId: String, cantidadRequerida: Double): Boolean {
        val material = materiales.find { it.id == materialId } ?: return false
        return material.cantidadDisponible >= cantidadRequerida
    }

    fun obtenerMaterialPorId(id: String): Material? = materiales.find { it.id == id }

    fun obtenerTodos(): List<Material> = materiales.toList()
}
