package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
//  LO UNICO QUE SE GUARDA DE UN AVISO: QUE YA LO VISTE
//
//  Los avisos NO son una tabla. Se calculan del estado real (stock, umbral,
//  caducidad) cada vez que algo cambia. Guardarlos duplicaria la verdad: si
//  repones el material, el aviso guardado quedaria mintiendo hasta que alguien
//  lo limpiara.
//
//  Aqui solo vive la marca de "leido". Y cuando un aviso deja de existir
//  —porque repusiste el material— su marca se borra sola (ver
//  `borrarObsoletos`): si el stock vuelve a bajar despues, el aviso reaparece
//  sin leer, que es lo que uno espera.
// ============================================================

@Entity(tableName = "avisos_descartados")
data class AvisoDescartado(
    @PrimaryKey @ColumnInfo(name = "clave") val clave: String,
    @ColumnInfo(name = "fecha_descarte") val fechaDescarte: String
)
