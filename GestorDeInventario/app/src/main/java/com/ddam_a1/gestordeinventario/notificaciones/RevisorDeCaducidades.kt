package com.ddam_a1.gestordeinventario.notificaciones

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ddam_a1.gestordeinventario.data.repos.InventarioRepositorio
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// ============================================================
//  EL TRABAJO QUE REVISA LAS CADUCIDADES
//
//  Corre UNA VEZ AL DIA, con la app cerrada. Quien lo despierta es Android,
//  no la app: por eso no sirve una corrutina en el ViewModel, que muere con la
//  pantalla.
//
//  @HiltWorker + @AssistedInject es lo que permite pedir el repositorio aqui
//  dentro. Un Worker normal lo construye WorkManager, que no sabe nada de
//  Hilt; la fabrica que se conecta en GestorDeInventarioApp es la que hace de
//  puente. Los dos parametros @Assisted son los que pone WorkManager; el
//  repositorio lo pone Hilt.
//
//  No decide NADA por su cuenta: le pregunta al repositorio que avisos hay hoy
//  (la misma funcion que usa la pantalla de Avisos) y copia los de caducidad
//  al sistema. Una sola regla, en un solo sitio.
// ============================================================

@HiltWorker
class RevisorDeCaducidades @AssistedInject constructor(
    @Assisted private val contexto: Context,
    @Assisted parametros: WorkerParameters,
    private val repo: InventarioRepositorio
) : CoroutineWorker(contexto, parametros) {

    override suspend fun doWork(): Result {
        return try {
            val avisos = avisosQueSeNotifican(repo.avisos(hoyISO()))
            notificarAvisos(contexto, avisos)
            Result.success()
        } catch (e: Exception) {
            // `retry` y no `failure`: si la base estaba ocupada o el disco
            // lleno, mañana (o cuando Android quiera) vale la pena reintentar.
            Result.retry()
        }
    }

    companion object {

        /**
         * Nombre unico del trabajo. Con `KEEP`, si ya hay uno programado no se
         * crea otro: sin esto, cada arranque de la app dejaria una revision
         * diaria mas encima de la anterior.
         */
        private const val TRABAJO = "revision-diaria-de-caducidades"

        /**
         * `hoy()` vive en ui/Formato.kt y esto no es interfaz. Es una linea,
         * asi que se escribe aqui en vez de invertir la dependencia.
         */
        private fun hoyISO(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        fun programar(contexto: Context) {
            // Un dia es el intervalo minimo comodo de WorkManager (el minimo
            // real son 15 minutos). Android decide el momento exacto dentro de
            // esa ventana para juntarlo con otros trabajos y no gastar bateria.
            val peticion = PeriodicWorkRequestBuilder<RevisorDeCaducidades>(1, TimeUnit.DAYS)
                .build()

            WorkManager.getInstance(contexto).enqueueUniquePeriodicWork(
                TRABAJO,
                ExistingPeriodicWorkPolicy.KEEP,
                peticion
            )
        }
    }
}
