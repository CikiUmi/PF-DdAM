package com.ddam_a1.gestordeinventario.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ddam_a1.gestordeinventario.modelClasses.Negocio
import kotlinx.coroutines.flow.Flow

@Dao
interface NegocioDao {

    /**
     * Devuelve null mientras nadie ha terminado el alta.
     *
     * `Flow<Negocio?>` y no `Flow<Negocio>`: la tabla esta vacia hasta que se
     * crea el administrador, y el tipo tiene que poder decirlo. Un valor por
     * omision escondido aqui haria creer a la pantalla que ya hay negocio.
     */
    @Query("SELECT * FROM negocio WHERE _id = " + Negocio.FILA_UNICA)
    fun observar(): Flow<Negocio?>

    @Query("SELECT * FROM negocio WHERE _id = " + Negocio.FILA_UNICA)
    suspend fun leer(): Negocio?

    /** @Upsert: la primera vez inserta, las siguientes pisa la misma fila. */
    @Upsert
    suspend fun guardar(negocio: Negocio)
}
