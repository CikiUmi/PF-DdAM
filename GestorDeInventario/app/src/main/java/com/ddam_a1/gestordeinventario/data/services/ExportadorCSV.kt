package com.ddam_a1.gestordeinventario.data.services

import java.io.File

// ============================================================
//  RF29: EXPORTAR A .csv
//
//  Antes esto solo armaba el texto y lo devolvia: no escribia ningun archivo
//  y recibia una `contrasena` que jamas usaba. Ahora si escribe.
//
//  Donde escribe: la carpeta que le pasen. El repositorio le da la carpeta
//  privada de la app (Android/data/<paquete>/files/...), que NO necesita
//  permisos y el gestor de archivos del telefono si la ve. Compartirlo fuera
//  de ahi es trabajo de la pantalla, con un Intent y el FileProvider que ya
//  quedo declarado en el Manifest.
//
//  Sobre la contrasena: cifrar de verdad un .csv necesita empaquetarlo en un
//  .zip con clave, y eso pide una libreria externa (Zip4j). Se quito el
//  parametro en vez de dejarlo mintiendo.
// ============================================================

object ExportadorCSV {

    /** Arma el CSV en memoria. Sirve para la vista previa sin tocar el disco. */
    fun armar(encabezados: List<String>, filas: List<List<String>>): String {
        val contenido = StringBuilder()
        contenido.append(encabezados.joinToString(",") { escaparCampo(it) }).append("\n")
        filas.forEach { fila ->
            contenido.append(fila.joinToString(",") { escaparCampo(it) }).append("\n")
        }
        return contenido.toString()
    }

    /**
     * Escribe el archivo y devuelve su ruta absoluta.
     *
     * `mkdirs()` porque la primera vez la carpeta no existe todavia.
     * Si el archivo ya estaba, se sobrescribe: exportar dos veces el mismo dia
     * no debe dejar copias sueltas.
     */
    fun escribir(
        carpeta: File,
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>
    ): String {
        if (!carpeta.exists()) carpeta.mkdirs()
        val archivo = File(carpeta, if (nombreArchivo.endsWith(".csv")) nombreArchivo else nombreArchivo + ".csv")
        archivo.writeText(armar(encabezados, filas), Charsets.UTF_8)
        return archivo.absolutePath
    }

    /** Un campo con coma, comilla o salto de linea va entre comillas, y las comillas se duplican. */
    private fun escaparCampo(valor: String): String {
        val necesitaComillas = valor.contains(",") || valor.contains("\"") || valor.contains("\n")
        val limpio = valor.replace("\"", "\"\"")
        return if (necesitaComillas) "\"" + limpio + "\"" else limpio
    }
}
