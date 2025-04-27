// SignUpScreen.kt
package com.example.goalfit.screen.signup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goalfit.R
import com.example.goalfit.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(
    auth: FirebaseAuth,
    onSignUpSuccess: () -> Unit,
    navigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A1A))
                        .clickable { navigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_24),
                        contentDescription = "Atrás",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // App logo or name could go here
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Title
            Text(
                "Crear cuenta",
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                "Regístrate para comenzar",
                color = selectedColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 40.dp)
            )

            // Email field
            Text(
                "Correo electrónico",
                color = White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp, start = 4.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = unselectedColor,
                    focusedContainerColor = unselectedColor.copy(alpha = 0.7f),
                    focusedIndicatorColor = selectedColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = selectedColor,
                    unfocusedTextColor = White,
                    focusedTextColor = White
                ),
                placeholder = { Text("ejemplo@correo.com", color = Color(0xFFAAAAAA)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password field
            Text(
                "Contraseña",
                color = White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp, start = 4.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = unselectedColor,
                    focusedContainerColor = unselectedColor.copy(alpha = 0.7f),
                    focusedIndicatorColor = selectedColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = selectedColor,
                    unfocusedTextColor = White,
                    focusedTextColor = White
                ),
                placeholder = { Text("Crea una contraseña segura", color = Color(0xFFAAAAAA)) }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Register button
            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.i("SignUp", "Registro exitoso")
                                onSignUpSuccess()
                            } else {
                                Log.e("SignUp", "Error en el registro", task.exception)
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor
                )
            ) {
                Text(
                    "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Login suggestion
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "¿Ya tienes una cuenta? ",
                    color = selectedColor,
                    fontSize = 14.sp
                )
                Text(
                    "Iniciar sesión",
                    color = selectedColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { navigateBack() }
                )
            }
        }
    }
}
