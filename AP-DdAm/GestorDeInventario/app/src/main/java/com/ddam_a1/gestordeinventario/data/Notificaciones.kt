package com.ddam_a1.gestordeinventario.datos

import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.modelo.Aviso
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Módulo: Notificaciones y métricas
 * RF16, RF17
 */
object Notificaciones {

    // RF16: Avisos de materiales con stock bajo (según el umbral definido en RF19)
    fun revisarStockBajo(): List<Aviso> {
        return InventarioMateriales.obtenerTodos()
            .filter { InventarioMateriales.esStockBajo(it) }
            .map { Aviso(it.id, "stock_bajo", "El material '${it.nombre}' está bajo en inventario") }
    }

    // RF17: Avisos de materiales próximos a caducar, respetando los días de antelación (RF18)
    fun revisarCaducidadesProximas(fechaHoy: String): List<Aviso> {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val hoy = runCatching { formato.parse(fechaHoy) }.getOrNull() ?: return emptyList()
        val avisos = mutableListOf<Aviso>()

        for (material in InventarioMateriales.obtenerTodos()) {
            for (fechaCad in material.fechasCaducidad) {
                val fecha = runCatching { formato.parse(fechaCad) }.getOrNull() ?: continue
                val diasRestantes = ((fecha.time - hoy.time) / (1000 * 60 * 60 * 24)).toInt()
                if (diasRestantes in 0..material.diasAvisoCaducidad) {
                    avisos.add(Aviso(material.id, "caducidad", "El material '${material.nombre}' caduca el $fechaCad"))
                }
            }
        }
        return avisos
    }
}
