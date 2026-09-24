package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Un producto que lleva este material en su receta, ya masticado.
 *
 * La pantalla recibe esto y no la lista de productos con sus recetas: asi no
 * tiene que buscar dentro de `p.receta` cual ingrediente le toca. Quien hace
 * ese trabajo es el NavHost, que si tiene los datos.
 */
data class UsoEnProducto(
    val productoId: String,
    val nombre: String,
    val cantidadUsada: Double
)
