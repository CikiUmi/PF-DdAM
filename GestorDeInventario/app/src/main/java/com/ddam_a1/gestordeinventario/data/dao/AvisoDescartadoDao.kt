package com.ddam_a1.gestordeinventario.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ddam_a1.gestordeinventario.modelClasses.AvisoDescartado
import kotlinx.coroutines.flow.Flow

@Dao
interface AvisoDescartadoDao {

    /** En vivo: si marcas uno como leido, la pantalla de avisos se redibuja sola. */
    @Query("SELECT clave FROM avisos_descartados")
    fun clavesStream(): Flow<List<String>>

    @Query("SELECT clave FROM avisos_descartados")
    suspend fun claves(): List<String>

    /** @Upsert y no @Insert: marcar dos veces el mismo aviso no debe tronar. */
    @Upsert
    suspend fun descartar(descarte: AvisoDescartado)

    @Upsert
    suspend fun descartarVarios(descartes: List<AvisoDescartado>)

    /**
     * Borra las marcas de avisos que ya no existen.
     *
     * Es lo que evita que un "leido" viejo tape un aviso nuevo: repones el
     * material, el aviso desaparece, su marca se va con el, y si el stock
     * vuelve a bajar el aviso regresa sin leer.
     */
    @Query("DELETE FROM avisos_descartados WHERE clave NOT IN (:clavesVigentes)")
    suspend fun borrarObsoletos(clavesVigentes: List<String>)

    @Query("DELETE FROM avisos_descartados")
    suspend fun borrarTodos()
}
