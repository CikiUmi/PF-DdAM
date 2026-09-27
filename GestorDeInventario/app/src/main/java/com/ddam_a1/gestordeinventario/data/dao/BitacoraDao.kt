package com.ddam_a1.gestordeinventario.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {

    @Query("SELECT * FROM bitacora ORDER BY fecha ASC")
    fun todos(): Flow<List<RegistroLog>>

    @Query("SELECT * FROM bitacora WHERE descripcion LIKE '%' || :texto || '%' ORDER BY fecha ASC")
    suspend fun buscar(texto: String): List<RegistroLog>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(registro: RegistroLog)
}