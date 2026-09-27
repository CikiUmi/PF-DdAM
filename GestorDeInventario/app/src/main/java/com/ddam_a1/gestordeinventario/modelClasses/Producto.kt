package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey @ColumnInfo(name = "_id") val id: String,
    @ColumnInfo(name = "nombre") var nombre: String,
    @ColumnInfo(name = "precio_venta") var precioVenta: Double,        // RF7
    @ColumnInfo(name = "es_bajo_pedido") var esBajoPedido: Boolean,    // RF4
    @ColumnInfo(name = "stock_disponible") var stockDisponible: Int = 0, // RF10
    @ColumnInfo(name = "costo_produccion") var costoProduccion: Double = 0.0, // RF5
    @ColumnInfo(name = "caducidad_mas_cercana") var caducidadMasCercana: String? = null, // RF10
    /** Avisar cuando queden estas piezas o menos (RF19). 0 = no avisar. */
    @ColumnInfo(name = "stock_minimo") var stockMinimo: Int = 0
) {
    /**
     * La receta tampoco es una columna: vive en la tabla `receta`.
     *
     * Misma historia que `lotes` en Material. `@Ignore` en el cuerpo,
     * y el repositorio la rellena despues de leer.
     */
    @Ignore
    var receta: MutableList<IngredienteReceta> = mutableListOf()
}
