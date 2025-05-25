// RutinaDetalle.kt
package com.example.goalfit.screen.rutinas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.goalfit.core.entity.EjercicioEntity
import com.example.goalfit.screen.home.AppColors
import com.example.goalfit.screen.home.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaDetalle(
    rutinaNombre: String,
    ejercicios: List<EjercicioEntity>,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    val tieneEjercicios = ejercicios.isNotEmpty()

    // Tema y animación del color del botón
    val isDarkTheme = ThemeManager.isAppInDarkTheme()
    val themeState = ThemeManager.isDarkTheme.value
    val buttonColor by animateColorAsState(
        targetValue = if (tieneEjercicios) Color(0xFF4CAF50) else Color.Gray,
        animationSpec = tween(300),
        label = "Button Color Animation"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.gradient()),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        rutinaNombre,
                        color = AppColors.iconTint(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás", tint = AppColors.iconTint())
                    }
                },
                actions = {
                    IconButton(onClick = { ThemeManager.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = if (isDarkTheme) "Claro" else "Oscuro",
                            tint = AppColors.iconTint()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = AppColors.iconTint()
                )
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, buttonColor),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg().copy(alpha = 0.9f))
            ) {
                if (tieneEjercicios) {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(8.dp)
                            .semantics { contentDescription = "Empezar rutina" },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Empezar Rutina", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        "No hay ejercicios para esta rutina",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = AppColors.textPrimary()
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tieneEjercicios) {
                item {
                    Text(
                        "Ejercicios en esta rutina",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.iconTint(),
                        fontWeight = FontWeight.Bold
                    )
                }
                items(ejercicios) { ejercicio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, AppColors.accent()), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .semantics { contentDescription = "Ejercicio: ${ejercicio.nombre}" },
                        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg()),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar inicial
                                Box(
                                    Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(AppColors.accent()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        ejercicio.nombre.first().uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.width(12.dp))

                                Column {
                                    Text(
                                        ejercicio.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = AppColors.textPrimary(),
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Mostrar series sólo si no es null
                                    ejercicio.categoria?.let { s ->
                                        Text(
                                            text = "$s categoría",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.textSecondary()
                                        )
                                    }

                                    // Mostrar repeticiones sólo si no es null
                                    ejercicio.descripcion?.let { r ->
                                        Text(
                                            text = "$r descripción",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.textSecondary()
                                        )
                                    }
                                }
                            }

                            // Descripción sólo si no está en blanco
                            ejercicio.descripcion
                                .takeIf { !it.isNullOrBlank() }
                                ?.let { desc ->
                                    Spacer(Modifier.height(8.dp))
                                    Divider(color = AppColors.textSecondary().copy(alpha = 0.2f))
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        desc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppColors.textSecondary()
                                    )
                                }

                            // Chip de categoría sólo si no está en blanco
                            ejercicio.categoria
                                .takeIf { !it.isNullOrBlank() }
                                ?.let { cat ->
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Card(
                                            border = BorderStroke(1.dp, AppColors.accent()),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = AppColors.accent().copy(alpha = 0.2f)
                                            )
                                        ) {
                                            Text(
                                                text = cat,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AppColors.accent()
                                            )
                                        }
                                    }
                                }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AppColors.accent()),
                        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg().copy(alpha = 0.9f))
                    ) {
                        Text(
                            "Añade ejercicios desde la pantalla de edición para poder empezar.",
                            Modifier.padding(24.dp),
                            textAlign = TextAlign.Center,
                            color = AppColors.textPrimary()
                        )
                    }
                }
            }
        }
    }
}
