package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ---------- Modulo: Almacenamiento local ----------

@Entity(tableName = "bitacora")
data class RegistroLog(
    @PrimaryKey @ColumnInfo(name = "_id") val id: String,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "tipo") val tipo: String, // "venta" o "manual"
    @ColumnInfo(name = "descripcion") val descripcion: String
)
