package com.ddam_a1.gestordeinventario.ui.screens

/** Lo que el formulario de producto entrega al tocar Guardar. */
data class DatosProducto(
    val nombre: String,
    val precioVenta: Double,
    val esBajoPedido: Boolean,
    /** Avisar cuando queden estas piezas o menos. 0 = no avisar. */
    val stockMinimo: Int
)
