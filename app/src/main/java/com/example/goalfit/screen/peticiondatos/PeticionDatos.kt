package com.example.goalfit.screen.peticiondatos

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.entity.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- DTO para Firestore (sin campo usuarioID) ---
data class UsuarioFirestore(
    val nombre: String,
    val edad: Int,
    val peso: Double,
    val altura: Double,
    val nivelExperiencia: String,
    val objetivo: String
)

// --- Función para crear el usuario tanto en Firestore como en Room ---
// Llama a onSuccess() una vez guardado en ambas bases
fun crearUsuario(
    dbFirestore: FirebaseFirestore,
    usuarioFs: UsuarioFirestore,
    localDb: AppDatabase,
    onSuccess: () -> Unit
) {
    dbFirestore.collection("usuarios")
        .add(usuarioFs)
        .addOnSuccessListener { docRef ->
            val newId = docRef.id
            val localUser = UserEntity(
                usuarioID        = newId,
                nombre           = usuarioFs.nombre,
                edad             = usuarioFs.edad,
                peso             = usuarioFs.peso,
                altura           = usuarioFs.altura,
                nivelExperiencia = usuarioFs.nivelExperiencia,
                objetivo         = usuarioFs.objetivo
            )
            CoroutineScope(Dispatchers.IO).launch {
                localDb.userDao().insertarUsuario(localUser)
                onSuccess()
            }
        }
        .addOnFailureListener {
            // manejar error si es necesario
        }
}

// --- Selector de Dropdown reutilizable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { /* no-op */ },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun NumericKeyboardOptions() = KeyboardOptions(keyboardType = KeyboardType.Number)

// --- Pantalla de petición de datos ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeticionDatos(
    dbFirestore: FirebaseFirestore,
    navController: NavHostController
) {
    val context = LocalContext.current
    val localDb = AppDatabase.getInstance(context)

    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

    val experienciaOptions = listOf("Principiante", "Intermedio", "Avanzado")
    var selectedExperiencia by remember { mutableStateOf(experienciaOptions[0]) }

    val objetivoOptions = listOf("Perder peso", "Ganancia muscular", "Mantener forma")
    var selectedObjetivo by remember { mutableStateOf(objetivoOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp)
    ) {
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
            keyboardOptions = NumericKeyboardOptions(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = NumericKeyboardOptions(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Altura (m)") },
            keyboardOptions = NumericKeyboardOptions(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = "Nivel de Experiencia",
            options = experienciaOptions,
            selectedOption = selectedExperiencia,
            onOptionSelected = { selectedExperiencia = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = "Objetivo",
            options = objetivoOptions,
            selectedOption = selectedObjetivo,
            onOptionSelected = { selectedObjetivo = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuarioFs = UsuarioFirestore(
                    nombre           = nombre,
                    edad             = edad.toIntOrNull()    ?: 0,
                    peso             = peso.toDoubleOrNull() ?: 0.0,
                    altura           = altura.toDoubleOrNull() ?: 0.0,
                    nivelExperiencia = selectedExperiencia,
                    objetivo         = selectedObjetivo
                )
                crearUsuario(
                    dbFirestore = dbFirestore,
                    usuarioFs   = usuarioFs,
                    localDb     = localDb
                ) {
                    navController.navigate("home") {
                        popUpTo("peticiondatos") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Usuario")
        }
    }
}
