package com.ddam_a1.gestordeinventario.modelClasses

data class Producto(
    val id: String,
    var nombre: String,
    var precioVenta: Double,       // RF7
    var esBajoPedido: Boolean,     // RF4
    val receta: MutableList<IngredienteReceta> = mutableListOf(), // RF6
    var stockDisponible: Int = 0,  // RF10
    var costoProduccion: Double = 0.0, // RF5
    var caducidadMasCercana: String? = null, // RF10
    /**
     * Avisar cuando queden estas piezas o menos (RF19).
     *
     * Solo tiene sentido en productos CON stock: uno bajo pedido se elabora al
     * momento, asi que nunca "se le acaba".
     */
    var stockMinimo: Int = 0
)
