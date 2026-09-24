package com.ddam_a1.gestordeinventario.modelo



// ---------- Módulo: Ventas ----------

data class ItemVendido(
    val productoId: String,
    val cantidad: Int,
    val precioUnitario: Double,          // congelado -> RF30
    val costoUnitarioProduccion: Double  // congelado -> RF30 (el costo tampoco es retroactivo)
)
