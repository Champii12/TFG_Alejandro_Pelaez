package com.example.goalfit.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF5722), // Naranja fuerte
                        Color(0xFFFF7043), // Naranja medio
                        Color(0xFFFFAB91)  // Naranja suave
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "¡Bienvenido de nuevo!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = usuarioNombre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resumen de progreso
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendario",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 16.dp)
                            )

                            Column {
                                Text(
                                    text = "Rutinas completadas este mes:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
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
                                    fontSize = 36.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Título de la sección de rutinas
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Rutinas",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Tus rutinas",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Lista de rutinas
                items(rutinas) { rutina ->
                    RutinaCard(rutina = rutina, onClick = { onClickRutina(rutina.idRutina) })
                }

                // Espacio adicional al final
                item {
                    Spacer(modifier = Modifier.height(80.dp))
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con la primera letra de la rutina
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFF7043)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rutina.nombreRutina.first().toString().uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = rutina.nombreRutina,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                val fechaFmt = remember(rutina.fechaCreacion) {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(rutina.fechaCreacion)
                }

                Text(
                    text = "Creada el $fechaFmt",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }

            // Indicador visual para mostrar que es clickeable
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Ver rutina",
                tint = Color(0xFFFF7043),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
