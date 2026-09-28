package com.ddam_a1.gestordeinventario.data.negocio

import com.ddam_a1.gestordeinventario.modelClasses.Venta
import com.ddam_a1.gestordeinventario.modelClasses.enums.Periodo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Módulo: Rendimiento del negocio
 * RF21, RF23, RF24
 */
object RendimientoNegocio {

    // ============================================================
    //  UNA VENTA CANCELADA NO CUENTA. EN NINGUN LADO.
    //
    //  Se cancela y su mercancia vuelve al inventario, asi que contarla seria
    //  cobrar dos veces. La regla se escribe UNA vez y la usan todos los
    //  calculos y tambien las graficas: estaba repetida en cuatro sitios y
    //  bastaba con que uno se olvidara —la grafica de barras— para que la
    //  pantalla se contradijera a si misma, con los ingresos en cero y una
    //  barra de 914 encima.
    //
    //  La cancelada SI se ve en el historial, con su etiqueta: ahi el usuario
    //  esta leyendo lo que paso, no cuanto gano.
    // ============================================================

    fun ventasQueCuentan(ventas: List<Venta>): List<Venta> = ventas.filter { !it.cancelada }

    // RF23: Ingresos totales de un conjunto de ventas
    fun calcularIngresos(ventas: List<Venta>): Double =
        ventasQueCuentan(ventas).sumOf { it.total }

    // RF23: Ganancias = ingresos - costo de producción de lo vendido.
    // Usa el costo CONGELADO en cada item (RF30): cambiar hoy el costo de un
    // material no altera la ganancia de una venta de ayer.
    fun calcularGanancias(ventas: List<Venta>): Double {
        var costoTotal = 0.0
        for (venta in ventasQueCuentan(ventas)) {
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
        for (venta in ventasQueCuentan(ventas)) {
            for (item in venta.items) {
                conteo[item.productoId] = (conteo[item.productoId] ?: 0) + item.cantidad
            }
        }
        return conteo.toList().sortedByDescending { it.second }.take(top)
    }

    // ============================================================
    //  RF24: EL PERIODO
    //
    //  SEMANAL y MENSUAL filtraban EXACTAMENTE igual —los dos por año-mes—,
    //  asi que "Semana" y "Mes" ensenaban el mismo numero y el chip parecia no
    //  hacer nada. Ahora la semana es la semana de verdad: de lunes a domingo
    //  alrededor de la fecha de referencia.
    //
    //  Las fechas son "aaaa-mm-dd", y con ese formato el orden alfabetico ES
    //  el cronologico: comparar los textos con >= y <= es comparar fechas, sin
    //  convertir cada una de las ventas.
    // ============================================================

    fun filtrarVentasPorPeriodo(
        ventas: List<Venta>,
        periodo: Periodo,
        fechaReferencia: String
    ): List<Venta> = when (periodo) {
        Periodo.DIARIO -> ventas.filter { it.fecha == fechaReferencia }
        Periodo.SEMANAL -> {
            val lunes = corrimientoDeDias(fechaReferencia, -diasDesdeElLunes(fechaReferencia))
            val domingo = corrimientoDeDias(lunes, 6)
            ventas.filter { it.fecha >= lunes && it.fecha <= domingo }
        }
        Periodo.MENSUAL ->
            ventas.filter { it.fecha.take(7) == fechaReferencia.take(7) }
    }

    /** Cuantos dias han pasado desde el lunes de esa semana. Lunes = 0. */
    private fun diasDesdeElLunes(fecha: String): Int {
        val cal = aCalendario(fecha) ?: return 0
        // Calendar.MONDAY es 2 y DOMINGO 1: se recorre para que la semana
        // empiece en lunes, como en el diseno y como en la grafica.
        return (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }

    /** La misma fecha corrida `dias` dias, en el mismo formato de texto. */
    private fun corrimientoDeDias(fecha: String, dias: Int): String {
        val cal = aCalendario(fecha) ?: return fecha
        cal.add(Calendar.DAY_OF_MONTH, dias)
        return formato().format(cal.time)
    }

    private fun aCalendario(fecha: String): Calendar? {
        val d = runCatching { formato().parse(fecha) }.getOrNull() ?: return null
        return Calendar.getInstance().apply { time = d }
    }

    private fun formato() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // RF21: Métricas disponibles para que el administrador elija cuáles mostrar en el menú principal
    fun metricasDisponibles(): List<String> = listOf("Ganancias", "Pérdidas", "Ingresos", "Productos más vendidos")
}