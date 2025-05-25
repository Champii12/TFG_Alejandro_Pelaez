package com.example.goalfit.screen.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.goalfit.core.entity.UserEntity
import com.example.goalfit.screen.peticiondatos.DropdownSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditar(
    user: UserEntity,
    onBack: () -> Unit,
    onSave: (updated: UserEntity, newImageUri: Uri?) -> Unit
) {
    val context = LocalContext.current
    var nombre by rememberSaveable { mutableStateOf(user.nombre) }
    var edad by rememberSaveable { mutableStateOf(user.edad.toString()) }
    var altura by rememberSaveable { mutableStateOf(user.altura.toString()) }
    var peso by rememberSaveable { mutableStateOf(user.peso.toString()) }

    val experienciaOptions = listOf("Principiante", "Intermedio", "Avanzado")
    var selectedNivel by rememberSaveable {
        mutableStateOf(user.nivelExperiencia.takeIf { it in experienciaOptions } ?: experienciaOptions[0])
    }

    val objetivoOptions = listOf("Perder peso", "Ganancia muscular", "Mantener forma")
    var selectedObjetivo by rememberSaveable {
        mutableStateOf(user.objetivo.takeIf { it in objetivoOptions } ?: objetivoOptions[0])
    }

    // Guardamos URI y persistimos permiso
    var newImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Permiso persistente de lectura
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            newImageUri = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { launcher.launch(arrayOf("image/*")) },
                contentAlignment = Alignment.Center
            ) {
                val model = newImageUri ?: user.photoUrl?.let { Uri.parse(it) }
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp))
                }
            }
            TextButton(onClick = { launcher.launch(arrayOf("image/*")) }) {
                Text("Cambiar foto")
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text("Altura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownSelector(
                label = "Nivel de Experiencia",
                options = experienciaOptions,
                selectedOption = selectedNivel,
                onOptionSelected = { selectedNivel = it }
            )
            DropdownSelector(
                label = "Objetivo",
                options = objetivoOptions,
                selectedOption = selectedObjetivo,
                onOptionSelected = { selectedObjetivo = it }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val updated = user.copy(
                        nombre = nombre,
                        edad = edad.toIntOrNull().orZero(user.edad),
                        altura = altura.toDoubleOrNull().orZero(user.altura),
                        peso = peso.toDoubleOrNull().orZero(user.peso),
                        nivelExperiencia = selectedNivel,
                        objetivo = selectedObjetivo,
                        // guardamos URI local como String
                        photoUrl = newImageUri?.toString()
                    )
                    onSave(updated, newImageUri)
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

// Funciones de extensión para evitar nulls
private fun Int?.orZero(fallback: Int) = this ?: fallback
private fun Double?.orZero(fallback: Double) = this ?: fallback
