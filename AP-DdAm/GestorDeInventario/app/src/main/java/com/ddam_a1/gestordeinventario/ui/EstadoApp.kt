package com.ddam_a1.gestordeinventario.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ddam_a1.gestordeinventario.modelo.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Estado que recuerda la interfaz. Los datos viven en los módulos. */
object EstadoApp {
    var usuario by mutableStateOf<Usuario?>(null)

    /** RF21: métricas elegidas para el menú principal. */
    val metricasElegidas = mutableStateListOf("Ingresos", "Ganancias", "Productos más vendidos", "Pérdidas")

    /** Contador que avisa a Compose que los datos cambiaron. */
    var version by mutableIntStateOf(0)
        private set

    fun datosCambiaron() { version++ }
    fun cerrarSesion() { usuario = null }
}

fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
fun dinero(v: Double): String = "$" + String.format(Locale.getDefault(), "%,.2f", v)
fun cant(v: Double): String =
    if (v % 1.0 == 0.0) v.toInt().toString() else String.format(Locale.getDefault(), "%.2f", v)
