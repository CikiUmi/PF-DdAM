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
