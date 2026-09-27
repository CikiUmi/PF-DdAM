package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey @ColumnInfo (name= "_id") val id: String,
    @ColumnInfo (name= "fecha") val fecha: String,
    @ColumnInfo (name= "total") val total: Double,
    @ColumnInfo (name= "cancelada")var cancelada: Boolean = false // solo se puede cancelar durante el proceso de creación

) {
    // fuera del constructure, ignore le dice que no es columna
    // se llena según se va llenando. Caducidad funciona igual
    @Ignore
    var items: MutableList<ItemVendido> = mutableListOf()

}
