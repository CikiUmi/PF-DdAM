package com.ddam_a1.gestordeinventario.data.repos

import com.ddam_a1.gestordeinventario.modelClasses.Venta

// ============================================================
//  EL RESULTADO DE VENDER
//
//  Vive aqui, junto al contrato, y no dentro de una implementacion.
//  `InventarioRepositorio` lo devuelve, el ViewModel lo recibe y el NavHost
//  lo traduce a un mensaje: los tres dependen de ESTO, no de quien lo produzca.
//
//  Estaba dentro de `repos/memory/Ventas.kt`. Mientras estuvo ahi, el contrato
//  dependia de la implementacion que ibamos a borrar: al reves de como debe ser.
// ============================================================

/** Motivo por el que una venta no se pudo registrar (para mostrarlo en pantalla). */
enum class ErrorVenta {
    TICKET_VACIO,
    PRODUCTO_NO_EXISTE,
    CANTIDAD_INVALIDA,
    MATERIALES_INSUFICIENTES,
    STOCK_INSUFICIENTE
}

/** Resultado de intentar registrar una venta. */
sealed class ResultadoVenta {
    data class Exito(val venta: Venta) : ResultadoVenta()
    data class Fallo(val motivo: ErrorVenta) : ResultadoVenta()
}
