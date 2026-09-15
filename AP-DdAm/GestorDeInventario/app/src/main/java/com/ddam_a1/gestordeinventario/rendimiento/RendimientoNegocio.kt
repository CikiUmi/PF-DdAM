package com.ddam_a1.gestordeinventario.rendimiento

import com.ddam_a1.gestordeinventario.modelo.Venta

// RF24: períodos disponibles para filtrar métricas
enum class Periodo { DIARIO, SEMANAL, MENSUAL }

/**
 * Módulo: Rendimiento del negocio
 * RF21, RF23, RF24
 */
object RendimientoNegocio {

    // RF23: Ingresos totales de un conjunto de ventas
    fun calcularIngresos(ventas: List<Venta>): Double =
        ventas.filter { !it.cancelada }.sumOf { it.total }

    // RF23: Ganancias = ingresos - costo de producción de lo vendido.
    // Usa el costo CONGELADO en cada item (RF30): cambiar hoy el costo de un
    // material no altera la ganancia de una venta de ayer.
    fun calcularGanancias(ventas: List<Venta>): Double {
        var costoTotal = 0.0
        for (venta in ventas.filter { !it.cancelada }) {
            for (item in venta.items) {
                costoTotal += item.costoUnitarioProduccion * item.cantidad
            }
        }
        return calcularIngresos(ventas) - costoTotal
    }

    // RF23: Pérdidas (cuando el costo de producción supera el ingreso, ej. mermas o ventas con descuento)
    fun calcularPerdidas(ventas: List<Venta>): Double {
        val ganancia = calcularGanancias(ventas)
        return if (ganancia < 0) -ganancia else 0.0
    }

    // RF23: Productos más vendidos (para mostrar en tablas y gráficas)
    fun productosMasVendidos(ventas: List<Venta>, top: Int = 5): List<Pair<String, Int>> {
        val conteo = mutableMapOf<String, Int>()
        for (venta in ventas.filter { !it.cancelada }) {
            for (item in venta.items) {
                conteo[item.productoId] = (conteo[item.productoId] ?: 0) + item.cantidad
            }
        }
        return conteo.toList().sortedByDescending { it.second }.take(top)
    }

    // RF24: Filtrar ventas por período (fechas en formato "yyyy-MM-dd")
    fun filtrarVentasPorPeriodo(ventas: List<Venta>, periodo: Periodo, fechaReferencia: String): List<Venta> {
        return when (periodo) {
            Periodo.DIARIO -> ventas.filter { it.fecha == fechaReferencia }
            // Simplificado: agrupa por año-mes. Para semana exacta se puede usar java.time.WeekFields.
            Periodo.SEMANAL -> ventas.filter { it.fecha.substring(0, 7) == fechaReferencia.substring(0, 7) }
            Periodo.MENSUAL -> ventas.filter { it.fecha.substring(0, 7) == fechaReferencia.substring(0, 7) }
        }
    }

    // RF21: Métricas disponibles para que el administrador elija cuáles mostrar en el menú principal
    fun metricasDisponibles(): List<String> = listOf("Ganancias", "Pérdidas", "Ingresos", "Productos más vendidos")
}
