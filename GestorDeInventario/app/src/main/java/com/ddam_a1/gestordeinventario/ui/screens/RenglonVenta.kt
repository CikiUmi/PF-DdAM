package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Un item de una venta con el nombre del producto ya resuelto.
 *
 * La pantalla recibe esto y no `ItemVendido`, que solo trae el id: si
 * recibiera el id tendria que ir a buscar el producto, y eso ya seria salir a
 * pedir datos. Ademas el producto pudo cambiar de precio o desaparecer desde
 * entonces, y la venta tiene que seguir diciendo lo que se cobro ESE dia: por
 * eso el precio viaja aqui y no se lee del catalogo.
 */
data class RenglonVenta(
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
) {
    val importe: Double get() = precioUnitario * cantidad
}
