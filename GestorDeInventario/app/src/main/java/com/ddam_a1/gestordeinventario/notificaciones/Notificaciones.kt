package com.ddam_a1.gestordeinventario.notificaciones

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ddam_a1.gestordeinventario.MainActivity
import com.ddam_a1.gestordeinventario.R
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.modelClasses.TipoAviso

// ============================================================
//  LAS NOTIFICACIONES DEL SISTEMA
//
//  Un aviso de la app y una notificacion de Android NO son lo mismo:
//
//    Aviso         se calcula del estado real, vive mientras el problema exista
//                  y se ve dentro de la pantalla de Avisos.
//    Notificacion  es una copia suya que Android ensena por fuera, para que te
//                  enteres sin abrir la app.
//
//  La verdad sigue siendo el Aviso. Si repones el material, el aviso deja de
//  existir solo; la notificacion se borra al tocarla o al deslizarla, y no
//  vuelve a salir porque en la siguiente revision ya no habra aviso que copiar.
// ============================================================

/** Un canal por tema: asi el usuario puede callar las caducidades sin callar todo. */
const val CANAL_CADUCIDADES = "caducidades"

/**
 * Crea el canal. Se llama en cada arranque a proposito: volver a crear uno que
 * ya existe no hace nada, y asi no hay que recordar si ya se creo.
 *
 * Antes de Android 8 no habia canales y la notificacion sale igual, por eso el
 * `if` en vez de un `@RequiresApi` que obligaria a poner condicionales arriba.
 */
fun crearCanalDeAvisos(contexto: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            CANAL_CADUCIDADES,
            "Caducidades",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        canal.description = "Avisa cuando un lote está por caducar."
        contexto.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(canal)
    }
}

/**
 * De todos los avisos de hoy, cuales merecen salir al sistema.
 *
 * Vive aqui y no dentro del Worker porque el boton de prueba de Configuracion
 * tiene que aplicar EXACTAMENTE la misma regla: si filtraran por separado, el
 * boton podria decir que todo bien mientras la revision diaria calla algo.
 *
 * `leido` lo pone el repositorio consultando los descartes: lo que ya callaste
 * dentro de la app no te persigue afuera.
 */
fun avisosQueSeNotifican(todos: List<Aviso>): List<Aviso> =
    todos.filter { !it.leido && it.tipo in TIPOS_QUE_SE_NOTIFICAN }

/**
 * Los que valen una interrupcion: lo que esta POR caducar, sea material o
 * producto.
 *
 * Fuera quedan los dos de stock bajo, que se resuelven en la proxima compra y
 * no tienen fecha limite, y los dos de ya caducado: la notificacion sirve para
 * llegar a tiempo y ahi ya no se llega a nada. Eso se ensena dentro de la app,
 * en la franja de Inicio y en la pantalla de Avisos, donde no interrumpe.
 *
 * Es una lista y no una condicion suelta para que se lea de un vistazo QUE se
 * notifica: agregar o quitar un tipo es tocar este renglon.
 */
private val TIPOS_QUE_SE_NOTIFICAN =
    setOf(TipoAviso.CADUCIDAD, TipoAviso.CADUCIDAD_PRODUCTO)

/**
 * Manda una notificacion por cada aviso.
 *
 * El id de cada una sale de `aviso.clave`, que ya identifica al aviso de forma
 * estable. Consecuencia buena: si el trabajo corre otra vez y el problema sigue
 * ahi, la notificacion se REEMPLAZA en vez de apilarse una copia nueva cada dia.
 *
 * Devuelve cuantas se mandaron; si el usuario tiene las notificaciones
 * apagadas, devuelve 0 sin reventar.
 */
fun notificarAvisos(contexto: Context, avisos: List<Aviso>): Int {
    val gestor = NotificationManagerCompat.from(contexto)
    if (!gestor.areNotificationsEnabled()) return 0

    // Todas las notificaciones abren la app. Deep link a la pantalla de Avisos
    // seria mejor, pero pide rutas con enlace declaradas en el NavHost.
    val abrir = Intent(contexto, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val alTocar = PendingIntent.getActivity(
        contexto, 0, abrir,
        // IMMUTABLE es obligatorio desde Android 12: nadie mas puede cambiar
        // este Intent despues de creado.
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    var mandadas = 0
    for (aviso in avisos) {
        val notificacion: Notification = NotificationCompat.Builder(contexto, CANAL_CADUCIDADES)
            .setSmallIcon(R.drawable.ic_aviso)
            // EL NOMBRE PRIMERO. En la barra de estado se ven tres o cuatro
            // notificaciones a la vez y solo se lee el titulo: "Material por
            // caducar" repetido cuatro veces no distingue nada, y obliga a
            // abrir cada una para saber de que material habla.
            //
            // `aviso.titulo` es el nombre del material o del producto, que el
            // repositorio ya guarda aparte del mensaje justo para esto.
            .setContentTitle(aviso.titulo + ": Por caducar")
            .setContentText(aviso.mensaje)
            // El mensaje trae nombre, cantidad y fecha: en una linea se corta.
            .setStyle(NotificationCompat.BigTextStyle().bigText(aviso.mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(alTocar)
            .setAutoCancel(true)
            .build()
        try {
            gestor.notify(aviso.clave.hashCode(), notificacion)
            mandadas++
        } catch (e: SecurityException) {
            // En Android 13+ el permiso se puede revocar entre la revision de
            // arriba y este notify. No es un fallo del trabajo: se deja pasar.
            return mandadas
        }
    }
    return mandadas
}
