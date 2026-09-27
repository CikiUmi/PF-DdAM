package com.ddam_a1.gestordeinventario.di

import com.ddam_a1.gestordeinventario.data.repos.InventarioRepositorio
import com.ddam_a1.gestordeinventario.data.repos.local.InventarioRepositorioLocal
import com.ddam_a1.gestordeinventario.data.repos.SesionRepositorio
import com.ddam_a1.gestordeinventario.data.repos.local.SesionRepositorioLocal
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ============================================================
//  DONDE SE DECIDE QUE IMPLEMENTACION SE USA
//
//  Este modulo es el interruptor. El ViewModel pide `InventarioRepositorio`
//  (la interfaz) y Hilt mira aqui para saber que fabricar.
//
//  Se usa @Binds y no @Provides porque no hay nada que construir a mano: solo
//  se esta diciendo "cuando alguien pida la interfaz, dale esta clase". Hilt
//  genera menos codigo asi. Un @Binds siempre va en una clase abstracta.
//
//  YA LLEGO ROOM. Esta es la unica linea del proyecto que cambio para
//  pasar de listas en memoria a base de datos: `Memoria` -> `Local`. Ni el
//  ViewModel ni las 25 pantallas se enteraron.
//
//  Las implementaciones en memoria siguen ahi: para volver, se cambia de
//  regreso y ya.
//
//  El modulo de la base de datos (los @Provides de GestorDatabase y los DAO)
//  va aparte, en DatabaseModule.kt, cuando exista.
// ============================================================

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositorioModule {

    @Binds
    @Singleton
    abstract fun enlazarInventarioRepositorio(
        implementacion: InventarioRepositorioLocal
    ): InventarioRepositorio

    @Binds
    @Singleton
    abstract fun enlazarSesionRepositorio(
        implementacion: SesionRepositorioLocal
    ): SesionRepositorio
}
