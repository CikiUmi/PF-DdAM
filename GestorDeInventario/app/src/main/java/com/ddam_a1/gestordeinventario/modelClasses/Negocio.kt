package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
//  EL NEGOCIO: UNA TABLA DE UNA SOLA FILA
//
//  Aqui vive lo que se configura una vez, al arrancar la app, y no cambia
//  todos los dias: como se llama el negocio y si se trabaja en solitario o en
//  equipo.
//
//  POR QUE UNA TABLA Y NO DataStore:
//  DataStore seria la casa "de libro" para una preferencia, pero traeria una
//  dependencia y un patron nuevos (otro tipo de repositorio, otro flujo, otra
//  forma de migrar) para guardar dos campos. Room ya esta montado con sus DAO,
//  sus @Provides y sus Flow. Una tabla mas no cuesta nada y todo lo demas
//  —observar cambios, inyectar, probar— funciona igual que el resto.
//
//  EL TRUCO DE LA FILA UNICA:
//  la llave primaria es una constante. Al hacer @Upsert con id = 1 siempre se
//  pisa la misma fila, nunca se acumulan copias. Es lo mas simple que no se
//  puede romper: no hay forma de acabar con dos negocios.
// ============================================================

@Entity(tableName = "negocio")
data class Negocio(
    @PrimaryKey @ColumnInfo(name = "_id") val id: Int = FILA_UNICA,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    /** RF25: false = solo el administrador, true = equipo con roles. */
    @ColumnInfo(name = "modo_equipo") val modoEquipo: Boolean = false
) {
    companion object {
        const val FILA_UNICA = 1
    }
}
