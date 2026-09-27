package com.ddam_a1.gestordeinventario.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ddam_a1.gestordeinventario.modelClasses.LoteMaterial
import kotlinx.coroutines.flow.Flow

@Dao
interface LoteDao {

    /**
     * TODOS los lotes de TODOS los materiales, en un solo flujo.
     *
     * Parece raro traerlos completos en vez de pedirlos material por material,
     * pero es a proposito: el repositorio necesita armar la lista entera de
     * materiales con sus fechas, y con un solo flujo puede hacer `combine` de
     * las dos consultas. Pedir los lotes uno por uno serian N consultas y N
     * suscripciones.
     *
     * Ordenados por caducidad para que la mas proxima salga primero: con el
     * formato "aaaa-mm-dd" el orden alfabetico ya es el cronologico.
     */
    @Query("SELECT * FROM lotes ORDER BY caducidad ASC")
    fun todos(): Flow<List<LoteMaterial>>

    @Query("SELECT * FROM lotes WHERE material_id = :materialId ORDER BY caducidad ASC")
    suspend fun lotesDe(materialId: String): List<LoteMaterial>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(lote: LoteMaterial)
}
