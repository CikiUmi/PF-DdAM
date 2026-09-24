package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Los numeros del menu principal, en un solo paquete.
 *
 * Es una data class y no ocho parametros sueltos por la misma razon que
 * `Margenes` en A4: siempre viajan juntos, y asi no se pueden cruzar dos Int
 * por accidente al llamar.
 */
data class ResumenInicio(
    val ingresosDelMes: Double,
    val gananciaDelMes: Double,
    val ventasDelMes: Int,
    val totalMateriales: Int,
    val materialesBajos: Int,
    val totalProductos: Int
)
