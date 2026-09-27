package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

// ============================================================
//  UN LOTE DE UN MATERIAL
//
//  UN MATERIAL -> MUCHOS LOTES, cada uno con su caducidad.
//
//  LA LLAVE FORANEA le dice a SQLite que `material_id` tiene que ser el `_id`
//  de un material que EXISTA, asi que no puede haber lotes huerfanos. Y
//  `onDelete = CASCADE` es el regalo: al borrar un material, sus lotes se van
//  solos. Sin eso habria que acordarse de limpiarlos a mano en cada lugar que
//  borra un material, y el dia que se te olvide, la base se llena de basura
//  invisible.
//
//  EL INDICE: la consulta de siempre va a ser "los lotes de este material".
//  Sin indice SQLite lee la tabla entera cada vez, y ademas Room te marca un
//  warning si declaras una foranea sin indexar.
// ============================================================

@Entity(
    tableName = "lotes",
    foreignKeys = [
        ForeignKey(
            entity = Material::class,
            parentColumns = ["_id"],
            childColumns = ["material_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["material_id"])]
)
data class LoteMaterial(
    @PrimaryKey @ColumnInfo(name = "_id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "material_id")
    val materialId: String,

    /**
     * Texto "aaaa-mm-dd" y no una fecha de verdad, a proposito.
     *
     * Con ese formato el orden alfabetico ES el orden cronologico, asi que un
     * `ORDER BY caducidad ASC` ya devuelve la mas proxima primero y no hace
     * falta un TypeConverter. Si algun dia cambias a LocalDate, ahi si.
     */
    @ColumnInfo(name = "caducidad")
    val caducidad: String
)
