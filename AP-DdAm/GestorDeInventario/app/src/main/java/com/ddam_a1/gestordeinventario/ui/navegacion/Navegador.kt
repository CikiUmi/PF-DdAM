package com.ddam_a1.gestordeinventario.ui.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos

/** Pila de navegación mínima, sin dependencias extra. */
class Navegador(inicial: Ruta) {
    private val pila = mutableStateListOf(inicial)
    val actual: Ruta get() = pila.last()
    val puedeVolver: Boolean get() = pila.size > 1
    fun ir(r: Ruta) { pila.add(r) }
    fun irARaiz(r: Ruta) { pila.clear(); pila.add(r) }
    fun volver() { if (puedeVolver) pila.removeAt(pila.lastIndex) }
}

@Composable
fun recordarNavegador(inicial: Ruta): Navegador = remember { Navegador(inicial) }
