package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto

/** Pantalla 2 - Crear cuenta de administrador (RF27, primer uso). */
@Composable
fun PantallaCrearAdmin(onCrear: (usuario: String, clave: String) -> Unit) {
    var negocio by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var clave2 by remember { mutableStateOf("") }
    val valido = usuario.isNotBlank() && clave.length >= 4 && clave == clave2

    Lienzo {
        Text("Paso 1 de 2", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(6.dp))
        Text("Crea la cuenta de administrador", style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(6.dp))
        Text("Tendra todos los permisos y sera la unica que pueda crear a los demas usuarios.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(24.dp))
        // TODO: el nombre del negocio todavia no se guarda en ningun lado.
        CampoTexto(negocio, "Nombre del negocio", { negocio = it })
        Spacer(Modifier.height(12.dp))
        CampoTexto(usuario, "Usuario", { usuario = it })
        Spacer(Modifier.height(12.dp))
        CampoTexto(clave, "Contrasena", { clave = it })
        Spacer(Modifier.height(12.dp))
        CampoTexto(clave2, "Confirmar contrasena", { clave2 = it })

        Spacer(Modifier.height(24.dp))
        BotonPrincipal("Continuar", habilitado = valido) { onCrear(usuario.trim(), clave) }
    }
}
