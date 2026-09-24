package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Un material de la receta con lo que hay en inventario.
 *
 * Trae `cantidadPorPieza` y no el total, porque el total depende de cuantas
 * piezas escriba el usuario, y eso es estado de la pantalla. La multiplicacion
 * si la hace ella: es aritmetica para mostrar, no una consulta.
 */
data class RenglonProduccion(
    val nombre: String,
    val unidad: String,
    val cantidadPorPieza: Double,
    val disponible: Double
)
