package com.ddam_a1.gestordeinventario.almacenamiento

import com.ddam_a1.gestordeinventario.modelo.RegistroLog
import java.util.UUID

/**
 * Módulo: Almacenamiento local
 * RF15, RF29, RF30
 */
object AlmacenamientoLocal {

    private val logs = mutableListOf<RegistroLog>()

    // RF15: Guardar log de cambios en el inventario (ventas y cambios manuales)
    fun registrarLog(fecha: String, tipo: String, descripcion: String): RegistroLog {
        val log = RegistroLog(UUID.randomUUID().toString(), fecha, tipo, descripcion)
        logs.add(log)
        return log
    }

    // RF15: Consultar / buscar en el historial de cambios
    fun consultarHistorial(textoBusqueda: String? = null): List<RegistroLog> {
        if (textoBusqueda.isNullOrBlank()) return logs.toList()
        return logs.filter { it.descripcion.contains(textoBusqueda, ignoreCase = true) }
    }

    /**
     * RF29: Exportar datos a un archivo .csv protegido con contraseña.
     * Nota: aquí se arma el contenido en CSV; para cifrar el archivo final con
     * contraseña de verdad en Android se recomienda usar una librería como
     * Zip4j (genera un .zip con contraseña) al momento de guardar el archivo.
     */
    fun exportarACSV(
        nombreArchivo: String,
        encabezados: List<String>,
        filas: List<List<String>>,
        contrasena: String
    ): String {
        val contenido = StringBuilder()
        contenido.append(encabezados.joinToString(",") { escaparCampo(it) }).append("\n")
        filas.forEach { fila ->
            contenido.append(fila.joinToString(",") { escaparCampo(it) }).append("\n")
        }

        // Aquí se devuelve el contenido listo para escribirlo a un archivo
        // (con FileOutputStream) y luego comprimirlo/cifrarlo con la contraseña dada.
        return contenido.toString()
    }

    /** Un campo con coma, comilla o salto de línea debe ir entre comillas y con las comillas duplicadas. */
    private fun escaparCampo(valor: String): String {
        val necesitaComillas = valor.contains(",") || valor.contains("\"") || valor.contains("\n")
        val limpio = valor.replace("\"", "\"\"")
        return if (necesitaComillas) "\"$limpio\"" else limpio
    }
}
