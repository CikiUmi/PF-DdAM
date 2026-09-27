package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ddam_a1.gestordeinventario.modelClasses.enums.Rol

/**
 * El indice UNIQUE es la garantia de verdad de que no haya dos usuarios con el
 * mismo nombre.
 *
 * La validacion que ya hay en `SesionRepositorio.existeUsuario` sigue haciendo
 * falta, pero para otra cosa: dar el mensaje bonito. Las dos, no una. La
 * validacion puede perder una condicion de carrera; el indice no.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["nombre_usuario"], unique = true)]
)
data class Usuario(
    @PrimaryKey @ColumnInfo(name = "_id") val id: String,
    @ColumnInfo(name = "nombre_usuario") var nombreUsuario: String,
    @ColumnInfo(name = "contrasena_hash") var contrasenaHash: String,
    @ColumnInfo(name = "rol") var rol: Rol
)
