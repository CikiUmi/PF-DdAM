package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

/** Envoltura común: fondo del tema, barra de estado respetada y lista con separación. */
@Composable
fun Marco(
    barra: @Composable () -> Unit,
    pie: @Composable () -> Unit = {},
    /**
     * Boton flotante. Va en un Box sobre la lista, no dentro de ella: si
     * fuera un item mas se iria con el desplazamiento y dejaria de estar
     * a mano, que es justo para lo que sirve.
     */
    flotante: @Composable () -> Unit = {},
    contenido: LazyListScope.() -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            barra()
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Margenes.pantalla, end = Margenes.pantalla,
                        top = Margenes.xs,
                        // Espacio de sobra abajo para que el FAB no tape el
                        // ultimo renglon de la lista.
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(Margenes.md),
                    content = contenido
                )
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Margenes.pantalla, bottom = Margenes.lg)
                ) { flotante() }
            }
            pie()
        }
    }
}
