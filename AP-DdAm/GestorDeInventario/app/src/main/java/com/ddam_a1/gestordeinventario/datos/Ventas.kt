package com.ddam_a1.gestordeinventario.datos

import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.modelo.ItemVendido
import com.ddam_a1.gestordeinventario.modelo.Venta
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import java.util.UUID

/** Motivo por el que una venta no se pudo registrar (para mostrarlo en pantalla). */
enum class ErrorVenta { TICKET_VACIO, PRODUCTO_NO_EXISTE, CANTIDAD_INVALIDA, MATERIALES_INSUFICIENTES, STOCK_INSUFICIENTE }

/** Resultado de intentar registrar una venta. */
sealed class ResultadoVenta {
    data class Exito(val venta: Venta) : ResultadoVenta()
    data class Fallo(val motivo: ErrorVenta) : ResultadoVenta()
}

/**
 * Módulo: Ventas
 * RF12, RF13, RF14
 * El historial es de solo creación/lectura: una venta guardada no se edita ni se elimina.
 * Mientras se arma el ticket no se toca el inventario; el descuento ocurre al confirmar.
 */
object Ventas {

    private val ventas = mutableListOf<Venta>()

    /**
     * RF12: Registrar una venta de uno o varios productos.
     * itemsSolicitados: lista de pares (idProducto, cantidad).
     *
     * Trabaja en dos fases para no dejar el inventario a medias:
     *   1. Valida TODO el ticket (sumando lo que cada material necesita en todos los productos).
     *   2. Solo si todo alcanza, descuenta y guarda la venta.
     */
    fun registrarVenta(fecha: String, itemsSolicitados: List<Pair<String, Int>>): ResultadoVenta {
        if (itemsSolicitados.isEmpty()) return ResultadoVenta.Fallo(ErrorVenta.TICKET_VACIO)

        // ---- Fase 1: reunir y validar ----
        val materialesRequeridos = mutableMapOf<String, Double>() // RF13: productos bajo pedido
        val productosRequeridos = mutableMapOf<String, Int>()     // RF14: productos con stock

        for ((productoId, cantidad) in itemsSolicitados) {
            if (cantidad <= 0) return ResultadoVenta.Fallo(ErrorVenta.CANTIDAD_INVALIDA)
            val producto = CatalogoProductos.obtenerProductoPorId(productoId)
                ?: return ResultadoVenta.Fallo(ErrorVenta.PRODUCTO_NO_EXISTE)

            if (producto.esBajoPedido) {
                for (ingrediente in producto.receta) {
                    materialesRequeridos[ingrediente.materialId] =
                        (materialesRequeridos[ingrediente.materialId] ?: 0.0) + ingrediente.cantidadUsada * cantidad
                }
            } else {
                productosRequeridos[productoId] = (productosRequeridos[productoId] ?: 0) + cantidad
            }
        }

        // Se valida el total acumulado, no producto por producto: si dos productos
        // del mismo ticket usan la misma harina, se suma antes de comparar.
        val faltanMateriales = materialesRequeridos.any { (materialId, requerido) ->
            !InventarioMateriales.hayCantidadSuficiente(materialId, requerido)
        }
        if (faltanMateriales) return ResultadoVenta.Fallo(ErrorVenta.MATERIALES_INSUFICIENTES)

        val faltaStock = productosRequeridos.any { (productoId, requerido) ->
            (CatalogoProductos.obtenerProductoPorId(productoId)?.stockDisponible ?: 0) < requerido
        }
        if (faltaStock) return ResultadoVenta.Fallo(ErrorVenta.STOCK_INSUFICIENTE)

        // ---- Fase 2: aplicar (aquí ya nada puede fallar) ----
        materialesRequeridos.forEach { (materialId, requerido) ->
            InventarioMateriales.descontarCantidad(materialId, requerido)
        }
        productosRequeridos.forEach { (productoId, requerido) ->
            CatalogoProductos.obtenerProductoPorId(productoId)?.let { it.stockDisponible -= requerido }
        }

        val items = mutableListOf<ItemVendido>()
        var total = 0.0
        for ((productoId, cantidad) in itemsSolicitados) {
            val producto = CatalogoProductos.obtenerProductoPorId(productoId) ?: continue
            // RF30: se congelan precio Y costo en el momento de la venta.
            val costoUnitario = CatalogoProductos.calcularCostoProduccion(productoId)
            items.add(ItemVendido(productoId, cantidad, producto.precioVenta, costoUnitario))
            total += producto.precioVenta * cantidad
        }

        val venta = Venta(
            id = UUID.randomUUID().toString(),
            fecha = fecha,
            items = items,
            total = total
        )
        ventas.add(venta)

        // RF15: log del cambio de inventario provocado por la venta
        AlmacenamientoLocal.registrarLog(fecha, "venta", "Venta ${venta.id} registrada por un total de $total")

        return ResultadoVenta.Exito(venta)
    }

    /**
     * Cancela una venta YA guardada: la marca como cancelada (no se borra, RF12 es CR)
     * y devuelve al inventario lo que había descontado.
     * Para cancelar mientras se arma el ticket no hace falta nada: el inventario
     * todavía no se ha tocado.
     */
    fun cancelarVenta(ventaId: String, fecha: String): Boolean {
        val venta = ventas.find { it.id == ventaId } ?: return false
        if (venta.cancelada) return false

        for (item in venta.items) {
            val producto = CatalogoProductos.obtenerProductoPorId(item.productoId) ?: continue
            if (producto.esBajoPedido) {
                for (ingrediente in producto.receta) {
                    InventarioMateriales.devolverCantidad(
                        ingrediente.materialId,
                        ingrediente.cantidadUsada * item.cantidad
                    )
                }
            } else {
                producto.stockDisponible += item.cantidad
            }
        }
        venta.cancelada = true
        AlmacenamientoLocal.registrarLog(fecha, "manual", "Venta ${venta.id} cancelada y devuelta al inventario")
        return true
    }

    fun obtenerHistorialVentas(): List<Venta> = ventas.toList()

    fun obtenerVentaPorId(id: String): Venta? = ventas.find { it.id == id }
}
