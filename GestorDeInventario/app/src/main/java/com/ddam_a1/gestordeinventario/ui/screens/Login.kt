package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.Iconos

/**
 * Pantalla 1 - Iniciar sesion (RF28).
 *
 * No verifica la contrasena: entrega usuario y clave. Quien sabe si el hash
 * coincide es el repositorio, y quien traduce ese "no" a un mensaje es el
 * NavHost.
 */
@Composable
fun PantallaLogin(
    primerUso: Boolean,
    error: String?,
    onEntrar: (usuario: String, clave: String) -> Unit,
    onLimpiarError: () -> Unit,
    onConfigurar: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    Lienzo {
        Box(
            Modifier.size(84.dp).clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.inversePrimary)
                    )
                )
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) { Icon(Iconos.Caja, null, tint = Color.White, modifier = Modifier.size(38.dp)) }

        Spacer(Modifier.height(20.dp))
        Text("Gestor de inventario", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        Text("Materiales, productos y ventas de tu negocio",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp))

        Spacer(Modifier.height(32.dp))
        CampoTexto(usuario, "Usuario", { usuario = it; onLimpiarError() })
        Spacer(Modifier.height(12.dp))
        CampoTexto(clave, "Contrasena", { clave = it; onLimpiarError() })

        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Text(error, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(22.dp))
        BotonPrincipal("Iniciar sesion", habilitado = usuario.isNotBlank() && clave.isNotBlank()) {
            onEntrar(usuario.trim(), clave)
        }

        Spacer(Modifier.height(18.dp))
        if (primerUso) {
            Text("Primera vez en este dispositivo?\nConfigura tu negocio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable { onConfigurar() })
        } else {
            Text("Si no tienes cuenta, pidesela al administrador.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}
