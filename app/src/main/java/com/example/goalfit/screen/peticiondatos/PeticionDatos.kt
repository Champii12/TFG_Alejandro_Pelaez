package com.example.goalfit.screen.peticiondatos

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Reutilizable: selector desplegable
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
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label, color = Color.White.copy(alpha = 0.8f)) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0x33FFFFFF),
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                cursorColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
        ExposedDropdownMenu(
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeticionDatos(
    dbFirestore: FirebaseFirestore,
    onDataSaved: () -> Unit
) {
    val context = LocalContext.current
    val localDb = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

    val experienciaOptions = listOf("Principiante", "Intermedio", "Avanzado")
    var selectedExperiencia by remember { mutableStateOf(experienciaOptions[0]) }

    val objetivoOptions = listOf("Perder peso", "Ganancia muscular", "Mantener forma")
    var selectedObjetivo by remember { mutableStateOf(objetivoOptions[0]) }

    var newImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            newImageUri = it
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF5722),
                        Color(0xFFFF7043),
                        Color(0xFFFFAB91)
                    )
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                "Completa tu perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(16.dp))

            Box(
                Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .clickable { launcher.launch(arrayOf("image/*")) },
                contentAlignment = Alignment.Center
            ) {
                if (newImageUri != null) {
                    AsyncImage(
                        model = newImageUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            TextButton(onClick = { launcher.launch(arrayOf("image/*")) }) {
                Text("Elegir foto", color = Color.White)
            }
            Spacer(Modifier.height(16.dp))

            @Composable
            fun CampoTexto(
                value: String,
                onValueChange: (String) -> Unit,
                label: String,
                keyboard: KeyboardType = KeyboardType.Text
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = Color.White.copy(alpha = 0.8f)) },
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0x33FFFFFF),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            CampoTexto(nombre, { nombre = it }, "Nombre")
            CampoTexto(edad, { edad = it }, "Edad", KeyboardType.Number)
            CampoTexto(peso, { peso = it }, "Peso (kg)", KeyboardType.Number)
            CampoTexto(altura, { altura = it }, "Altura (m)", KeyboardType.Number)

            Spacer(Modifier.height(8.dp))
            DropdownSelector("Nivel de experiencia", experienciaOptions, selectedExperiencia) { selectedExperiencia = it }
            Spacer(Modifier.height(8.dp))
            DropdownSelector("Objetivo", objetivoOptions, selectedObjetivo) { selectedObjetivo = it }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                            Log.e("PeticionDatos", "Usuario no autenticado")
                            return@launch
                        }
                        val localUser = UserEntity(
                            usuarioID = uid,
                            nombre = nombre,
                            edad = edad.toIntOrNull() ?: 0,
                            peso = peso.toDoubleOrNull() ?: 0.0,
                            altura = altura.toDoubleOrNull() ?: 0.0,
                            nivelExperiencia = selectedExperiencia,
                            objetivo = selectedObjetivo,
                            photoUrl = newImageUri?.toString()
                        )
                        withContext(Dispatchers.IO) {
                            localDb.userDao().insertarUsuario(localUser)
                        }
                        dbFirestore.collection("usuarios")
                            .document(uid)
                            .set(localUser)
                            .await()
                        onDataSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Guardar perfil", color = Color.White, textAlign = TextAlign.Center)
            }
        }
    }
}

private fun Int?.orZero() = this ?: 0
private fun Double?.orZero() = this ?: 0.0
