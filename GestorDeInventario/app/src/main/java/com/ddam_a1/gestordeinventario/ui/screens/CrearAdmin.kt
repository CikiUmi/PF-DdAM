package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.components.*
import com.ddam_a1.gestordeinventario.data.Usuarios

/** Pantalla 2 · Crear cuenta de administrador (RF27, primer uso). */
@Composable
fun PantallaCrearAdmin(onContinuar: () -> Unit) {
    var negocio by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var clave2 by remember { mutableStateOf("") }
    val valido = usuario.isNotBlank() && clave.length >= 4 && clave == clave2

    Lienzo {
        Text("Paso 1 de 2", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(6.dp))
        Text("Crea la cuenta de administrador", style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(6.dp))
        Text("Tendrá todos los permisos y será la única que pueda crear a los demás usuarios.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(24.dp))
        CampoTexto(negocio, "Nombre del negocio", { negocio = it })
        Spacer(Modifier.height(12.dp))
        CampoTexto(usuario, "Usuario", { usuario = it })
        Spacer(Modifier.height(12.dp))
        CampoTexto(clave, "Contraseña", { clave = it })
        Spacer(Modifier.height(12.dp))
        CampoTexto(clave2, "Confirmar contraseña", { clave2 = it })

        Spacer(Modifier.height(24.dp))
        BotonPrincipal("Continuar", habilitado = valido) {
            Usuarios.crearUsuarioAdministrador(usuario.trim(), clave)
            EstadoApp.datosCambiaron()
            onContinuar()
        }
    }
}
