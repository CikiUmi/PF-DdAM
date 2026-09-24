package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Un producto del top de mas vendidos, con su nombre ya resuelto.
 *
 * `ItemVendido` solo guarda el `productoId`, asi que sin esto la pantalla
 * tendria que salir a buscar cada producto para poder escribir su nombre.
 */
data class VentaPorProducto(
    val nombre: String,
    val piezas: Int,
    val precioUnitario: Double
)
