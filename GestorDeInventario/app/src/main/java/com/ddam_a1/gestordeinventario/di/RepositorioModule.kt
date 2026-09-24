package com.ddam_a1.gestordeinventario.di

import com.ddam_a1.gestordeinventario.data.InventarioRepositorio
import com.ddam_a1.gestordeinventario.data.InventarioRepositorioMemoria
import com.ddam_a1.gestordeinventario.data.SesionRepositorio
import com.ddam_a1.gestordeinventario.data.SesionRepositorioMemoria
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
//  EL DIA QUE LLEGUE ROOM: cambias `InventarioRepositorioMemoria` por
//  `InventarioRepositorioLocal` en la linea de abajo. Una linea. Nada mas del
//  proyecto se entera, porque nadie mas conoce el nombre de la implementacion.
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
        implementacion: InventarioRepositorioMemoria
    ): InventarioRepositorio

    @Binds
    @Singleton
    abstract fun enlazarSesionRepositorio(
        implementacion: SesionRepositorioMemoria
    ): SesionRepositorio
}
