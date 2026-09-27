package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey @ColumnInfo (name= "_id") val id: String,
    @ColumnInfo (name= "fecha") val fecha: String,
    /**
     * La hora, "HH:mm", en columna APARTE de la fecha.
     *
     * Metida dentro de `fecha` habria roto todo lo que compara fechas: el
     * filtro por periodo, la grafica por dia de la semana y el CSV dan por
     * hecho que `fecha` es exactamente "aaaa-mm-dd". Separada, ordena dentro
     * del dia y no estorba a nadie.
     */
    @ColumnInfo (name= "hora") val hora: String = "",
    @ColumnInfo (name= "total") val total: Double,
    @ColumnInfo (name= "cancelada")var cancelada: Boolean = false // solo se puede cancelar durante el proceso de creación

) {
    // fuera del constructure, ignore le dice que no es columna
    // se llena según se va llenando. Caducidad funciona igual
    @Ignore
    var items: MutableList<ItemVendido> = mutableListOf()

}
