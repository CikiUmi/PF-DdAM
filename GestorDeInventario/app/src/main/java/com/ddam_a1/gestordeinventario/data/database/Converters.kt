package com.ddam_a1.gestordeinventario.data.database

import androidx.room.TypeConverter
import com.ddam_a1.gestordeinventario.modelClasses.enums.Rol

/**
 * SQLite solo sabe de texto, numeros y blobs. Todo lo demas necesita traduccion.
 *
 * Aqui hay UN solo convertidor, el del rol, porque las fechas de este proyecto
 * ya son String "aaaa-mm-dd" y con ese formato el orden alfabetico es el orden
 * cronologico. En A4 si hizo falta uno de LocalDateTime a Long.
 *
 * Se guarda el NOMBRE del enum y no su posicion (`ordinal`) a proposito: si
 * manana metes un rol nuevo en medio de la lista, las posiciones se recorren y
 * todos tus encargados se volverian administradores, en silencio. El nombre no
 * se mueve.
 */
class Converters {

    @TypeConverter
    fun rolATexto(rol: Rol): String = rol.name

    @TypeConverter
    fun textoARol(valor: String): Rol =
        runCatching { Rol.valueOf(valor) }.getOrDefault(Rol.EMPLEADO)
}