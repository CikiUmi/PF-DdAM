package com.ddam_a1.gestordeinventario.ui

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// ============================================================
//  ENTREGARLE EL ARCHIVO AL USUARIO
//
//  La exportacion escribe en `getExternalFilesDir()`, la carpeta privada de la
//  app. Esa decision es correcta —no pide permisos y se limpia al desinstalar—
//  pero tiene una consecuencia que en el emulador no se ve:
//
//  DESDE ANDROID 11, NINGUN GESTOR DE ARCHIVOS PUEDE ENTRAR A `Android/data`.
//
//  O sea que el archivo existe, la ruta que se ensena es real, y el usuario
//  no tiene forma de llegar a el. En el emulador si se ve, pero porque se mira
//  con el Device File Explorer de Android Studio, que entra por adb y se salta
//  esa regla. De ahi que "funcione" en emulador y no en un telefono.
//
//  La hoja de compartir resuelve justo eso: el usuario manda los .csv a Drive,
//  al correo, a Archivos o a donde quiera, y ahi si quedan a su alcance.
//
//  NO SE PUEDE PASAR UN `file://` A OTRA APP desde Android 7: revienta con
//  FileUriExposedException. Por eso va el FileProvider, que entrega un
//  `content://` con permiso temporal y acotado a estos archivos.
// ============================================================

fun compartirCsv(contexto: Context, rutas: List<String>, titulo: String = "Compartir exportación") {
    val uris = ArrayList<Uri>()
    for (ruta in rutas) {
        val archivo = File(ruta)
        // Un archivo que no esta no se comparte, pero tampoco tumba al resto.
        if (!archivo.exists()) continue
        uris.add(
            FileProvider.getUriForFile(
                contexto,
                // La misma authority del manifiesto, armada igual: con el
                // applicationId, para que nunca choque con la de otra app.
                contexto.packageName + ".fileprovider",
                archivo
            )
        )
    }
    if (uris.isEmpty()) return

    val envio = Intent(
        if (uris.size == 1) Intent.ACTION_SEND else Intent.ACTION_SEND_MULTIPLE
    ).apply {
        type = "text/csv"
        if (uris.size == 1) putExtra(Intent.EXTRA_STREAM, uris[0])
        else putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)

        // El ClipData ademas del EXTRA_STREAM: el permiso temporal viaja de
        // forma fiable por el ClipData. Sin el, algunas apps reciben la uri y
        // despues no pueden leerla, que es un fallo dificil de diagnosticar
        // porque solo pasa con ciertos destinos.
        clipData = ClipData("Exportación", arrayOf("text/csv"), ClipData.Item(uris[0])).also { datos ->
            for (i in 1 until uris.size) datos.addItem(ClipData.Item(uris[i]))
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val selector = Intent.createChooser(envio, titulo)
    // Una Activity puede abrir el selector dentro de su propia tarea. Si quien
    // llama no lo es, Android exige tarea nueva o lanza una excepcion.
    if (contexto !is Activity) selector.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    contexto.startActivity(selector)
}
