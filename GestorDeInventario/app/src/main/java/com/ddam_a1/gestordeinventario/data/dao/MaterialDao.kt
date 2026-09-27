package com.ddam_a1.gestordeinventario.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.ddam_a1.gestordeinventario.modelClasses.Material
import kotlinx.coroutines.flow.Flow

/**
 * Recordatorio de A4: `suspend` y `Flow` NUNCA van juntos en un metodo del DAO.
 * `suspend` es "hazlo y termina"; `Flow` es "quedate emitiendo". Juntos no
 * tienen sentido y Room lo rechaza.
 */
@Dao
interface MaterialDao {

    @Query("SELECT * FROM materiales ORDER BY nombre ASC")
    fun todos(): Flow<List<Material>>

    @Query("SELECT * FROM materiales WHERE _id = :materialId")
    suspend fun leer(materialId: String): Material?

    @Query("SELECT * FROM materiales WHERE nombre LIKE '%' || :texto || '%' ORDER BY nombre ASC")
    suspend fun buscar(texto: String): List<Material>

    /** @Upsert y no @Insert(REPLACE): REPLACE borra e inserta, y ese DELETE invisible se llevaria los lotes por CASCADE. */
    @Upsert
    suspend fun guardar(material: Material)

    @Update
    suspend fun actualizar(material: Material)

    @Delete
    suspend fun borrar(material: Material)

    // ============================================================
    //  MOVER CANTIDAD: EN SQL, NO EN KOTLIN
    //
    //  Lo facil seria leer el material, restarle en memoria y guardarlo. Pero
    //  entre la lectura y el guardado cabe otra operacion, y la segunda pisaria
    //  a la primera: vendes dos cosas a la vez y solo se descuenta una.
    //
    //  Haciendolo con un UPDATE, la resta ocurre DENTRO de la base, en un solo
    //  paso que nadie puede partir a la mitad. Y el `AND cantidad_disponible >=
    //  :cantidad` es la validacion en el mismo lugar: si no alcanza, no cambia
    //  nada y devuelve 0 renglones afectados.
    // ============================================================

    @Query("""
        UPDATE materiales
        SET cantidad_disponible = cantidad_disponible - :cantidad
        WHERE _id = :materialId AND cantidad_disponible >= :cantidad
    """)
    suspend fun descontar(materialId: String, cantidad: Double): Int

    @Query("UPDATE materiales SET cantidad_disponible = cantidad_disponible + :cantidad WHERE _id = :materialId")
    suspend fun sumar(materialId: String, cantidad: Double): Int

    @Query("UPDATE materiales SET stock_minimo = :minimo WHERE _id = :materialId")
    suspend fun definirStockMinimo(materialId: String, minimo: Double): Int

    @Query("UPDATE materiales SET dias_aviso_caducidad = :dias WHERE _id = :materialId")
    suspend fun definirDiasAviso(materialId: String, dias: Int): Int
}
