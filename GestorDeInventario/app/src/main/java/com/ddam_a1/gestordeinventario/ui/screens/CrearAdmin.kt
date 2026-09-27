package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.TituloPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

/**
 * Pantalla 2 - Crear cuenta de administrador (RF27, primer uso).
 * Figma 38:744 / 38:878 / 38:992.
 */
@Composable
fun PantallaCrearAdmin(
    onCrear: (usuario: String, clave: String, negocio: String) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    var negocio by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var clave2 by remember { mutableStateOf("") }

    val clavesDistintas = clave2.isNotEmpty() && clave != clave2
    val valido = usuario.isNotBlank() && clave.length >= 8 && clave == clave2

    Lienzo(
        anchoTarjeta = Anchos.tarjetaFormulario,
        barra = { BarraSuperior("Nuevo Administrador", onAtras = onAtras) },
        pie = {
            BotonPrincipal("Continuar", habilitado = valido) {
                onCrear(usuario.trim(), clave, negocio.trim())
            }
        }
    ) {
        TituloPantalla("Configura tu cuenta", "Configuración inicial · paso 1 de 2")

        Column(verticalArrangement = Arrangement.spacedBy(Margenes.lg)) {
            // Se guarda en la tabla `negocio`, en la misma transaccion que el
            // administrador (SesionRepositorioLocal.crearUsuarioAdministrador).
            CampoTexto(negocio, "Nombre del negocio", { negocio = it },
                marcador = "Ej. Mi Tienda S.A.C.")

            CampoTexto(usuario, "Usuario", { usuario = it },
                marcador = "Ej. admin_tienda")

            CampoTexto(clave, "Contraseña", { clave = it },
                esContrasena = true,
                marcador = "Mínimo 8 caracteres",
                // Solo se queja cuando ya escribiste algo: regañar por un campo
                // vacio que acabas de tocar es ruido.
                error = if (clave.isNotEmpty() && clave.length < 8)
                    "La contraseña debe tener al menos 8 caracteres" else null)

            CampoTexto(clave2, "Confirmar contraseña", { clave2 = it },
                esContrasena = true,
                marcador = "Repite la contraseña",
                error = if (clavesDistintas) "Las contraseñas no coinciden" else null)
        }
    }
}
