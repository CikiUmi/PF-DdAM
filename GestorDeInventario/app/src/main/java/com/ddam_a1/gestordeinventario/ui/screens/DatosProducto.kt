package com.ddam_a1.gestordeinventario.ui.screens

/** Lo que el formulario de producto entrega al tocar Guardar. */
data class DatosProducto(
    val nombre: String,
    val precioVenta: Double,
    val esBajoPedido: Boolean,
    /** Avisar cuando queden estas piezas o menos. 0 = no avisar. */
    val stockMinimo: Int,
    /**
     * Piezas que ya existen al dar de alta el producto. Solo se usa al crear.
     *
     * No es una columna de `Producto`: es una ENTRADA de existencias, y el
     * ViewModel la registra como tal para que quede en la bitacora igual que
     * cualquier otra. Meterla directo en la tabla la haria aparecer de la nada.
     */
    val stockInicial: Int = 0
)
