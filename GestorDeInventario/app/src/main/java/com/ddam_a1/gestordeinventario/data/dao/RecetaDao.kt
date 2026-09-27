package com.ddam_a1.gestordeinventario.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ddam_a1.gestordeinventario.modelClasses.IngredienteReceta
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    /** Todas las recetas de todos los productos, para armarlos de un jalon. */
    @Query("SELECT * FROM receta")
    fun todas(): Flow<List<IngredienteReceta>>

    @Query("SELECT * FROM receta WHERE producto_id = :productoId")
    suspend fun recetaDe(productoId: String): List<IngredienteReceta>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregar(ingrediente: IngredienteReceta)

    @Query("DELETE FROM receta WHERE producto_id = :productoId")
    suspend fun borrarRecetaDe(productoId: String)

    /**
     * Deja la receta EXACTAMENTE con estos ingredientes.
     *
     * `@Transaction` es lo importante: borrar y volver a insertar son dos
     * operaciones, y entre una y otra la receta esta vacia. Si la app se muere
     * justo ahi, el producto se queda sin ingredientes. Con @Transaction o
     * pasan las dos, o no pasa ninguna.
     *
     * Aqui si se puede escribir el cuerpo del metodo porque la interfaz del DAO
     * lo permite cuando el metodo no es abstracto.
     */
    @Transaction
    suspend fun reemplazar(productoId: String, ingredientes: List<IngredienteReceta>) {
        borrarRecetaDe(productoId)
        ingredientes.forEach { agregar(it) }
    }
}
