package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Lo que el formulario entrega cuando el usuario toca Guardar.
 *
 * Es una data class y no seis parametros sueltos en el callback porque siempre
 * viajan juntos, y asi no se pueden confundir dos Double seguidos al llamar.
 */
data class DatosMaterial(
    val nombre: String,
    val unidad: String,
    val cantidad: Double,
    val costo: Double,
    val stockMinimo: Double,
    val diasAvisoCaducidad: Int
)
