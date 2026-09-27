package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Pantalla 1 - Iniciar sesion (RF28).   Figma 38:712 / 38:842 / 38:960.
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

    Lienzo(separacion = Margenes.xxl, centrado = true) {
        // ---------- Logo ----------
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Margenes.lg)
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "G",
                    // La inicial es decorativa: el nombre completo va justo
                    // abajo, y leer "G, Gestor de Inventario" sobra.
                    modifier = Modifier.clearAndSetSemantics { },
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Margenes.xs)
            ) {
                Text(
                    "Gestor de Inventario",
                    style = MaterialTheme.typography.headlineSmall,   // Lora SemiBold 24
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    "Control simple, negocio eficiente",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ---------- Formulario ----------
        Column(verticalArrangement = Arrangement.spacedBy(Margenes.lg)) {
            CampoTexto(
                usuario, "Usuario", { usuario = it; onLimpiarError() },
                marcador = "admin@ejemplo.com"
            )
            CampoTexto(
                clave, "Contraseña", { clave = it; onLimpiarError() },
                esContrasena = true,
                marcador = "Tu contraseña",
                // El mensaje va debajo del campo y el borde se pone rojo. El
                // error es del par usuario+clave, pero se ancla al segundo
                // campo porque es el ultimo que se toco.
                error = error
            )
        }

        // ---------- Acciones ----------
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Margenes.xl)
        ) {
            BotonPrincipal(
                "Iniciar sesión",
                habilitado = usuario.isNotBlank() && clave.isNotBlank()
            ) { onEntrar(usuario.trim(), clave) }

            if (primerUso) {
                Text(
                    "¿Primera vez? Configura tu negocio",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    ),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radios.chip))
                        .clickable { onConfigurar() }
                        // Area tocable: el texto solo mide 22dp de alto.
                        .padding(horizontal = Margenes.md, vertical = Margenes.md)
                )
            } else {
                Text(
                    "Si no tienes cuenta, pidesela al administrador.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
