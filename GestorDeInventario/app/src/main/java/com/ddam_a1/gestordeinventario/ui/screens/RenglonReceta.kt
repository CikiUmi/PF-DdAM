package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Un ingrediente de la receta con su material ya resuelto.
 *
 * La pantalla recibe esto y no `IngredienteReceta`, que solo trae un id: si
 * recibiera el id tendria que ir a buscar el material, y eso ya seria salir a
 * pedir datos.
 */
data class RenglonReceta(
    val nombre: String,
    val unidad: String,
    val costoUnitario: Double,
    val cantidadUsada: Double
)
