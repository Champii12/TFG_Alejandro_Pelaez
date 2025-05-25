package com.example.goalfit.screen.rutinas

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goalfit.screen.crearRutina.UiEjercicio
import com.example.goalfit.screen.crearRutina.NumericKeyboardOptions
import com.example.goalfit.screen.crearRutina.lista_categorias
import com.example.goalfit.screen.home.AppColors
import com.example.goalfit.screen.home.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRutinaScreen(
    nombreRutina: String,
    onNombreChange: (String) -> Unit,
    ejercicios: List<UiEjercicio>,
    onEjerciciosChange: (List<UiEjercicio>) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val isDarkTheme = ThemeManager.isAppInDarkTheme()
    val themeState = ThemeManager.isDarkTheme.value
    val backgroundColor by animateColorAsState(
        targetValue = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White,
        animationSpec = tween(300),
        label = "Background Color Animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.gradient())
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Editar Rutina",
                            color = AppColors.iconTint(),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.semantics {
                                contentDescription = "Volver atrás"
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = AppColors.iconTint()
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { ThemeManager.toggleTheme() }
                        ) {
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
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Nombre de la rutina
                item {
                    OutlinedTextField(
                        value = nombreRutina,
                        onValueChange = onNombreChange,
                        label = { Text("Nombre de la rutina", color = AppColors.iconTint().copy(alpha = 0.8f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Campo para nombre de la rutina" },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColors.accent(),
                            focusedBorderColor = AppColors.accent(),
                            unfocusedBorderColor = AppColors.iconTint().copy(alpha = 0.5f),
                            containerColor = if (isDarkTheme) Color(0x33FFFFFF) else Color(0x22000000)
                        )
                    )
                }

                items(ejercicios.size) { index ->
                    val uiEj = ejercicios[index]
                    var categoriaExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Ejercicio ${index + 1}: ${uiEj.nombre}" },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.cardBg().copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Ejercicio ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.textPrimary(),
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        val copia = ejercicios.toMutableList().also { it.removeAt(index) }
                                        onEjerciciosChange(copia)
                                    },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Eliminar ejercicio ${index + 1}"
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Nombre
                            OutlinedTextField(
                                value = uiEj.nombre,
                                onValueChange = { nuevo ->
                                    val copia = ejercicios.toMutableList().also { it[index].nombre = nuevo }
                                    onEjerciciosChange(copia)
                                },
                                label = { Text("Nombre ejercicio", color = AppColors.textSecondary()) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = AppColors.accent(),
                                    focusedBorderColor = AppColors.accent(),
                                    unfocusedBorderColor = AppColors.textSecondary().copy(alpha = 0.5f),
                                    containerColor = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x11000000)
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            // Series / Reps
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiEj.series,
                                    onValueChange = { s ->
                                        val copia = ejercicios.toMutableList().also { it[index].series = s }
                                        onEjerciciosChange(copia)
                                    },
                                    label = { Text("Series", color = AppColors.textSecondary()) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = NumericKeyboardOptions(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        cursorColor = AppColors.accent(),
                                        focusedBorderColor = AppColors.accent(),
                                        unfocusedBorderColor = AppColors.textSecondary().copy(alpha = 0.5f),
                                        containerColor = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x11000000)
                                    )
                                )
                                OutlinedTextField(
                                    value = uiEj.repeticiones,
                                    onValueChange = { r ->
                                        val copia = ejercicios.toMutableList().also { it[index].repeticiones = r }
                                        onEjerciciosChange(copia)
                                    },
                                    label = { Text("Reps", color = AppColors.textSecondary()) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = NumericKeyboardOptions(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        cursorColor = AppColors.accent(),
                                        focusedBorderColor = AppColors.accent(),
                                        unfocusedBorderColor = AppColors.textSecondary().copy(alpha = 0.5f),
                                        containerColor = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x11000000)
                                    )
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Categoría
                            ExposedDropdownMenuBox(
                                expanded = categoriaExpanded,
                                onExpandedChange = { categoriaExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = uiEj.categoria,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Categoría", color = AppColors.textSecondary()) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        cursorColor = AppColors.accent(),
                                        focusedBorderColor = AppColors.accent(),
                                        unfocusedBorderColor = AppColors.textSecondary().copy(alpha = 0.5f),
                                        containerColor = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x11000000)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = categoriaExpanded,
                                    onDismissRequest = { categoriaExpanded = false },
                                    modifier = Modifier.background(AppColors.cardBg())
                                ) {
                                    lista_categorias.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat, color = AppColors.textPrimary()) },
                                            onClick = {
                                                val copia = ejercicios.toMutableList().also { it[index].categoria = cat }
                                                onEjerciciosChange(copia)
                                                categoriaExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Descripción
                            OutlinedTextField(
                                value = uiEj.descripcion,
                                onValueChange = { desc ->
                                    val copia = ejercicios.toMutableList().also { it[index].descripcion = desc }
                                    onEjerciciosChange(copia)
                                },
                                label = { Text("Descripción (opcional)", color = AppColors.textSecondary()) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = AppColors.accent(),
                                    focusedBorderColor = AppColors.accent(),
                                    unfocusedBorderColor = AppColors.textSecondary().copy(alpha = 0.5f),
                                    containerColor = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x11000000)
                                )
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = { onEjerciciosChange(ejercicios + UiEjercicio()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Añadir nuevo ejercicio" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.accent().copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Añadir ejercicio", fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Actualizar rutina" },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Actualizar Rutina", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditRutinaRoute(
    initialNombre: String,
    initialEjercicios: List<UiEjercicio>,
    onBack: () -> Unit,
    onSaveRoutine: (String, List<UiEjercicio>) -> Unit
) {
    var nombreRutina by remember { mutableStateOf(initialNombre) }
    val ejercicios = remember { mutableStateListOf<UiEjercicio>().apply { addAll(initialEjercicios) } }

    EditRutinaScreen(
        nombreRutina = nombreRutina,
        onNombreChange = { nombreRutina = it },
        ejercicios = ejercicios,
        onEjerciciosChange = { nuevos ->
            ejercicios.clear()
            ejercicios.addAll(nuevos)
        },
        onBack = onBack,
        onSave = { onSaveRoutine(nombreRutina, ejercicios.toList()) }
    )
}
