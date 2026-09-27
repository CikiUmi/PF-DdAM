package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID


// ---------- Módulo: Ventas ----------
@Entity(
    tableName = "items_venta",
    foreignKeys = [
        ForeignKey(
            entity = Venta::class,
            parentColumns = ["_id"],
            childColumns = ["venta_id"],
            onDelete = ForeignKey.CASCADE
        )
    ], //hacemos un index :D
    indices = [Index("venta_id")]
)
data class ItemVendido(
    @ColumnInfo(name = "producto_id") val productoId: String,
    @ColumnInfo(name = "cantidad") val cantidad: Int,
    @ColumnInfo(name = "precio_unitario") val precioUnitario: Double,
    @ColumnInfo(name = "costo_unitario_produccion") val costoUnitarioProduccion: Double,
    @ColumnInfo(name = "venta_id") val ventaId: String = "",
    @PrimaryKey @ColumnInfo(name = "_id") val id: String = UUID.randomUUID().toString()
)