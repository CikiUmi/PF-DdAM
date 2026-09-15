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
import com.ddam_a1.gestordeinventario.usuarios.Usuarios

@Composable
private fun Lienzo(contenido: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(28.dp),
            verticalArrangement = Arrangement.Center,
            content = contenido
        )
    }
}

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
                .background(Brush.linearGradient(listOf(Primario, PrimarioClaro)))
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

/** Pantalla 3 · Elegir modo individual o equipo. */
@Composable
fun PantallaElegirModo(onEmpezar: () -> Unit) {
    var equipo by remember { mutableStateOf(false) }

    Lienzo {
        Text("Paso 2 de 2", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(6.dp))
        Text("¿Quién va a usar la app?", style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(20.dp))

        OpcionModo("Solo yo", "Un único usuario administrador. Sin roles ni permisos que configurar.",
            !equipo) { equipo = false }
        Spacer(Modifier.height(12.dp))
        OpcionModo("Mi equipo", "Varios usuarios en este dispositivo, cada uno con su rol: administrador, encargado o empleado.",
            equipo) { equipo = true }

        Spacer(Modifier.height(20.dp))
        Text("Puedes cambiarlo después en Configuración.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        BotonPrincipal("Empezar") {
            Usuarios.elegirModo(equipo)
            EstadoApp.datosCambiaron()
            onEmpezar()
        }
    }
}

@Composable
private fun OpcionModo(titulo: String, detalle: String, activo: Boolean, onClick: () -> Unit) {
    val borde = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(if (activo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .border(1.5.dp, borde, RoundedCornerShape(20.dp))
            .clickable { onClick() }.padding(16.dp)
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape)
                .border(2.dp, borde, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (activo) Box(Modifier.size(11.dp).clip(CircleShape).background(borde))
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
