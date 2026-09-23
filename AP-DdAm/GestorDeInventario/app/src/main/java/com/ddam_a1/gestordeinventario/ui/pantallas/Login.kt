package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelo.Usuario
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.datos.Usuarios

/** Pantalla 1 · Iniciar sesión (RF28). */
@Composable
fun PantallaLogin(onEntrar: (Usuario) -> Unit, onConfigurar: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val primerUso = Usuarios.esPrimerUso()

    Lienzo {
        Box(
            Modifier.size(84.dp).clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.inversePrimary)))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) { Icon(Iconos.Caja, null, tint = Color.White, modifier = Modifier.size(38.dp)) }

        Spacer(Modifier.height(20.dp))
        Text("Gestor de inventario", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        Text("Materiales, productos y ventas de tu negocio",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))

        Spacer(Modifier.height(32.dp))
        CampoTexto(usuario, "Usuario", { usuario = it; error = null })
        Spacer(Modifier.height(12.dp))
        CampoTexto(clave, "Contraseña", { clave = it; error = null })

        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Text(error!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(22.dp))
        BotonPrincipal("Iniciar sesión", habilitado = usuario.isNotBlank() && clave.isNotBlank()) {
            val u = Usuarios.iniciarSesion(usuario.trim(), clave)
            if (u == null) error = "Usuario o contraseña incorrectos" else onEntrar(u)
        }

        Spacer(Modifier.height(18.dp))
        if (primerUso) {
            Text("¿Primera vez en este dispositivo?\nConfigura tu negocio",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable { onConfigurar() })
        } else {
            Text("Si no tienes cuenta, pídesela al administrador.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}
