package com.ddam_a1.gestordeinventario

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ddam_a1.gestordeinventario.notificaciones.RevisorDeCaducidades
import com.ddam_a1.gestordeinventario.notificaciones.crearCanalDeAvisos
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// ============================================================
//  EL ARRANQUE DE LA APP
//
//  Implementa Configuration.Provider para darle a WorkManager una fabrica de
//  Workers que sepa de Hilt. Sin esto, WorkManager construiria el Worker con
//  su constructor de dos parametros y reventaria al no poder pasarle el
//  repositorio.
//
//  Como la configuracion ahora la da esta clase, en el Manifest hay que
//  APAGAR el inicializador automatico de WorkManager (tools:node="remove").
//  Si se dejaran los dos, WorkManager se inicializaria antes que Hilt con la
//  fabrica equivocada.
// ============================================================

@HiltAndroidApp
class GestorDeInventarioApp : Application(), Configuration.Provider {

    // lateinit y no constructor: a una Application la construye Android, no
    // Hilt. Hilt le mete el campo despues, en cuanto arranca.
    @Inject
    lateinit var fabricaDeWorkers: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(fabricaDeWorkers)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Crear el canal no cuesta nada si ya existe, y programar el trabajo
        // con KEEP no duplica el que ya estuviera puesto.
        crearCanalDeAvisos(this)
        RevisorDeCaducidades.programar(this)
    }
}
