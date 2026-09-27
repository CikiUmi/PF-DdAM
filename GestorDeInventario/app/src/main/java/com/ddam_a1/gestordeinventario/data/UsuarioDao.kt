package com.ddam_a1.gestordeinventario.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios ORDER BY nombre_usuario ASC")
    fun todos(): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun cuantos(): Int

    /**
     * `COLLATE NOCASE` hace la comparacion sin distinguir mayusculas dentro de
     * SQLite, que es mas rapido y mas confiable que traerse todos los usuarios
     * a Kotlin para compararlos ahi.
     */
    @Query("SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = :nombre COLLATE NOCASE")
    suspend fun cuantosConNombre(nombre: String): Int

    /** Para el login: se busca por nombre Y por hash, nunca se lee la contrasena. */
    @Query("SELECT * FROM usuarios WHERE nombre_usuario = :nombre AND contrasena_hash = :hash LIMIT 1")
    suspend fun autenticar(nombre: String, hash: String): Usuario?

    /**
     * ABORT y no IGNORE: si el indice UNIQUE del nombre se viola, queremos que
     * falle. `IGNORE` no haria nada en silencio, que es justo el bug que no se
     * ve venir (trampa de A4).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun agregar(usuario: Usuario)

    @Update
    suspend fun actualizar(usuario: Usuario)
}
