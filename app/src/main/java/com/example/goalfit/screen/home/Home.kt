package com.example.goalfit.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goalfit.core.entity.RutinaEntity
import com.example.goalfit.core.entity.ProgresoEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    usuarioNombre: String,
    rutinas: List<RutinaEntity>,
    progresos: List<ProgresoEntity>,
    onClickRutina: (Int) -> Unit,
    onLogout: () -> Unit        // <-- callback para cerrar sesión
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Hola, $usuarioNombre!",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- Resumen de progreso ---
            Text(
                text = "Rutinas completadas este mes:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            val completadasMes = progresos.count {
                val cal = Calendar.getInstance().apply { time = it.fecha }
                val now = Calendar.getInstance()
                cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
            }
            Text(
                text = "$completadasMes",
                style = MaterialTheme.typography.headlineMedium,
                fontStyle = FontStyle.Italic,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- Lista de rutinas ---
            Text(
                text = "Tus rutinas:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(rutinas) { rutina ->
                    RutinaCard(rutina = rutina, onClick = { onClickRutina(rutina.idRutina) })
                }
            }
        }
    }
}

@Composable
private fun RutinaCard(
    rutina: RutinaEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = rutina.nombreRutina,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            val fechaFmt = remember(rutina.fechaCreacion) {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(rutina.fechaCreacion)
            }
            Text(
                text = "Creada el $fechaFmt",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
