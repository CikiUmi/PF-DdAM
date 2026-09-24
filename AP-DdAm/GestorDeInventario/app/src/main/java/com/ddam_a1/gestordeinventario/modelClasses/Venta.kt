package com.ddam_a1.gestordeinventario.modelClasses



data class Venta(
    val id: String,
    val fecha: String,
    val items: MutableList<ItemVendido>,
    val total: Double,
    var cancelada: Boolean = false // solo se puede cancelar durante el proceso de creación
)
