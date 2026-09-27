package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.OpcionSimple
import com.ddam_a1.gestordeinventario.ui.components.TituloPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

/**
 * Pantalla 3 - Elegir modo de uso (RF25).
 * Figma 38:780 / 38:920 / 38:1028.
 */
@Composable
fun PantallaElegirModo(
    onEmpezar: (equipo: Boolean) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    var equipo by remember { mutableStateOf(false) }

    Lienzo(
        anchoTarjeta = Anchos.tarjetaAncha,
        barra = { BarraSuperior("Modo de trabajo", onAtras = onAtras) },
        pie = { BotonPrincipal("Empezar") { onEmpezar(equipo) } }
    ) {
        TituloPantalla("Elige cómo vas a usar la app", "Paso 2 de 2 · Estilo de administración")

        // selectableGroup: le dice al lector de pantalla que las dos opciones
        // son UNA sola eleccion ("1 de 2"), no dos interruptores sueltos.
        Column(
            Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(Margenes.lg)
        ) {
            OpcionSimple(
                "Solo yo (administrador)",
                "Gestiona solo tu negocio",
                activo = !equipo
            ) { equipo = false }

            OpcionSimple(
                "Mi equipo (con roles)",
                "Invita equipo y asigna roles",
                activo = equipo
            ) { equipo = true }
        }
    }
}
