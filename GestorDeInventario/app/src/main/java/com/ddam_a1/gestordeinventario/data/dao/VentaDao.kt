package com.ddam_a1.gestordeinventario.data.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import com.ddam_a1.gestordeinventario.modelClasses.ItemVendido
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import kotlinx.coroutines.flow.Flow

// ============================================================
//  @Embedded + @Relation: la unica consulta del proyecto que
//  trae un objeto CON SUS HIJOS.
//
//  @Embedded  -> "las columnas de `ventas` van aqui adentro"
//  @Relation  -> "ademas ve a items_venta y traeme los renglones
//                 cuyo venta_id sea igual al _id de esta venta"
//
//  Esto NO es un JOIN. Room hace DOS consultas: una a ventas y
//  otra a items_venta, y arma los objetos en Kotlin. Por eso el
//  metodo lleva @Transaction (abajo se explica).
// ============================================================

data class VentaConItems(
    @Embedded val venta: Venta,
    @Relation(parentColumn = "_id", entityColumn = "venta_id")
    val items: List<ItemVendido>
)

@Dao
interface VentaDao {
    // @Transaction: como por dentro son dos consultas, sin transaccion
    // alguien podria insertar entre la primera y la segunda, y te tocaria
    // una venta con los items de otro momento. Room te avisa con un warning
    // si se te olvida en un metodo que devuelve @Relation.

    @Transaction
    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun todas(): Flow<List<VentaConItems>>

    @Transaction
    @Query("SELECT * FROM ventas WHERE _id = :ventaId")
    suspend fun leer(ventaId: String): VentaConItems?

    // @Upsert y no @Insert(REPLACE): REPLACE hace DELETE + INSERT, y ese
    // DELETE invisible se llevaria los items_venta por CASCADE.
    @Upsert
    suspend fun guardarVenta(venta: Venta)

    // Los items se insertan DESPUES de la venta: la llave foranea exige que
    // la venta ya exista, si no SQLite rechaza el insert.
    @Insert
    suspend fun guardarItems(items: List<ItemVendido>)

    // Mutar `venta.cancelada = true` en memoria no escribe nada.
    // SQLite no tiene booleano: guarda 0 y 1. Por eso `cancelada = 1`.
    // El `AND cancelada = 0` hace que cancelar dos veces devuelva 0.
    @Query("UPDATE ventas SET cancelada = 1 WHERE _id = :ventaId AND cancelada = 0")
    suspend fun cancelar(ventaId: String): Int
}