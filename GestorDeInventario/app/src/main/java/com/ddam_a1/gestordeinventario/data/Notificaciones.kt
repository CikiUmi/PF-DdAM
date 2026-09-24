package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.TipoAviso
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Modulo: Notificaciones
 * RF16, RF17
 */
object Notificaciones {

    // RF16: materiales con stock bajo (umbral de RF19)
    fun revisarStockBajo(): List<Aviso> =
        InventarioMateriales.obtenerTodos()
            .filter { InventarioMateriales.esStockBajo(it) }
            .map {
                Aviso(it.id, TipoAviso.STOCK_BAJO_MATERIAL,
                    "El material '" + it.nombre + "' esta bajo en inventario")
            }

    /**
     * RF16 tambien para productos terminados.
     *
     * Los de bajo pedido quedan fuera: se elaboran al momento, nunca tienen
     * stock que se pueda acabar. Y un umbral en cero significa "no me avises".
     */
    fun revisarStockBajoProductos(): List<Aviso> =
        CatalogoProductos.obtenerTodos()
            .filter { !it.esBajoPedido && it.stockMinimo > 0 && it.stockDisponible <= it.stockMinimo }
            .map {
                Aviso(it.id, TipoAviso.STOCK_BAJO_PRODUCTO,
                    "Quedan " + it.stockDisponible + " piezas de '" + it.nombre + "'")
            }

    // RF17: materiales proximos a caducar, con los dias de antelacion de RF18
    fun revisarCaducidadesProximas(fechaHoy: String): List<Aviso> {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val hoy = runCatching { formato.parse(fechaHoy) }.getOrNull() ?: return emptyList()
        val avisos = mutableListOf<Aviso>()

        for (material in InventarioMateriales.obtenerTodos()) {
            for (fechaCad in material.fechasCaducidad) {
                val fecha = runCatching { formato.parse(fechaCad) }.getOrNull() ?: continue
                val diasRestantes = ((fecha.time - hoy.time) / (1000 * 60 * 60 * 24)).toInt()
                if (diasRestantes in 0..material.diasAvisoCaducidad) {
                    avisos.add(
                        Aviso(material.id, TipoAviso.CADUCIDAD,
                            "El material '" + material.nombre + "' caduca el " + fechaCad)
                    )
                }
            }
        }
        return avisos
    }
}
