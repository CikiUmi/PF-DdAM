package com.ddam_a1.gestordeinventario.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ddam_a1.gestordeinventario.modelClasses.Usuario

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
