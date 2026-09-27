package com.ddam_a1.gestordeinventario.di

import android.content.Context
import com.ddam_a1.gestordeinventario.data.dao.AvisoDescartadoDao
import com.ddam_a1.gestordeinventario.data.dao.BitacoraDao
import com.ddam_a1.gestordeinventario.data.database.GestorDatabase
import com.ddam_a1.gestordeinventario.data.dao.LoteDao
import com.ddam_a1.gestordeinventario.data.dao.NegocioDao
import com.ddam_a1.gestordeinventario.data.dao.MaterialDao
import com.ddam_a1.gestordeinventario.data.dao.ProductoDao
import com.ddam_a1.gestordeinventario.data.dao.RecetaDao
import com.ddam_a1.gestordeinventario.data.dao.UsuarioDao
import com.ddam_a1.gestordeinventario.data.dao.VentaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ============================================================
//  DE DONDE SALEN LA BASE Y LOS DAO
//
//  Aqui va @Provides y no @Binds porque SI hay algo que construir: la base
//  necesita el Context, y cada DAO se pide a la base. @Binds solo sirve para
//  decir "esta clase implementa esta interfaz".
//
//  Hilt empata por TIPO DE RETORNO. Con esto ya sabe fabricar un MaterialDao
//  cuando alguien lo pida, y como los repositorios piden varios, resuelve la
//  cadena sola: primero la base, de ahi los DAO, y con los DAO el repositorio.
//
//  Un DAO nuevo = un @Provides nuevo. Cuando hagamos VentaDao, otra funcion.
// ============================================================

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun proveerBaseDatos(@ApplicationContext context: Context): GestorDatabase =
        GestorDatabase.getDatabase(context)

    @Provides
    fun proveerMaterialDao(db: GestorDatabase): MaterialDao = db.materialDao()

    @Provides
    fun proveerLoteDao(db: GestorDatabase): LoteDao = db.loteDao()

    @Provides
    fun proveerProductoDao(db: GestorDatabase): ProductoDao = db.productoDao()

    @Provides
    fun proveerRecetaDao(db: GestorDatabase): RecetaDao = db.recetaDao()

    @Provides
    fun proveerUsuarioDao(db: GestorDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    fun proveerBitacoraDao(db: GestorDatabase): BitacoraDao = db.bitacoraDao()

    @Provides
    fun proveerVentaDao(db: GestorDatabase): VentaDao = db.ventaDao()

    @Provides
    fun proveerAvisoDescartadoDao(db: GestorDatabase): AvisoDescartadoDao = db.avisoDescartadoDao()

    @Provides
    fun proveerNegocioDao(db: GestorDatabase): NegocioDao = db.negocioDao()
}
