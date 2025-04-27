package com.example.goalfit.screen.peticiondatos

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.entity.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                withContext (Dispatchers.Main){
                    onSuccess()
                }
            }
        }
        .addOnFailureListener {
            // manejar error si es necesario
        }
}

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
            label = { Text(label, color = Color.White.copy(alpha = 0.8f)) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                containerColor = Color(0x33FFFFFF)
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF333333))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Completa tu perfil",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                "Necesitamos algunos datos para personalizar tu experiencia",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre", color = Color.White.copy(alpha = 0.8f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    containerColor = Color(0x33FFFFFF)
                )
            )

            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad", color = Color.White.copy(alpha = 0.8f)) },
                keyboardOptions = NumericKeyboardOptions(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    containerColor = Color(0x33FFFFFF)
                )
            )

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)", color = Color.White.copy(alpha = 0.8f)) },
                keyboardOptions = NumericKeyboardOptions(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    containerColor = Color(0x33FFFFFF)
                )
            )

            OutlinedTextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text("Altura (m)", color = Color.White.copy(alpha = 0.8f)) },
                keyboardOptions = NumericKeyboardOptions(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    containerColor = Color(0x33FFFFFF)
                )
            )

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

            Spacer(modifier = Modifier.height(32.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50) // Verde que combina bien con naranja
                )
            ) {
                Text(
                    "Guardar perfil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
