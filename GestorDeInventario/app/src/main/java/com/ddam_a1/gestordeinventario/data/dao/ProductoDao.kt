package com.ddam_a1.gestordeinventario.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun todos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE _id = :productoId")
    suspend fun leer(productoId: String): Producto?

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :texto || '%' ORDER BY nombre ASC")
    suspend fun buscar(texto: String): List<Producto>

    @Upsert
    suspend fun guardar(producto: Producto)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun borrar(producto: Producto)

    /** Misma idea que en materiales: la resta ocurre dentro de la base y valida en el WHERE. */
    @Query("""
        UPDATE productos
        SET stock_disponible = stock_disponible - :cantidad
        WHERE _id = :productoId AND stock_disponible >= :cantidad
    """)
    suspend fun descontarStock(productoId: String, cantidad: Int): Int

    @Query("UPDATE productos SET stock_disponible = stock_disponible + :cantidad WHERE _id = :productoId")
    suspend fun sumarStock(productoId: String, cantidad: Int): Int

    @Query("UPDATE productos SET costo_produccion = :costo WHERE _id = :productoId")
    suspend fun definirCostoProduccion(productoId: String, costo: Double): Int

    @Query("UPDATE productos SET stock_minimo = :minimo WHERE _id = :productoId")
    suspend fun definirStockMinimo(productoId: String, minimo: Int): Int

    @Query("UPDATE productos SET precio_venta = :precio WHERE _id = :productoId")
    suspend fun definirPrecioVenta(productoId: String, precio: Double): Int

    @Query("UPDATE productos SET caducidad_mas_cercana = :caducidad WHERE _id = :productoId")
    suspend fun definirCaducidad(productoId: String, caducidad: String?): Int
}
