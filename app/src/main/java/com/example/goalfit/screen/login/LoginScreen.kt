package com.example.goalfit.screen.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goalfit.R
import com.example.goalfit.ui.theme.Black
import com.example.goalfit.ui.theme.DarkGreen
import com.example.goalfit.ui.theme.Gray
import com.example.goalfit.ui.theme.Green
import com.example.goalfit.ui.theme.LightBlue
import com.example.goalfit.ui.theme.LightGray
import com.example.goalfit.ui.theme.Red
import com.example.goalfit.ui.theme.White
import com.example.goalfit.ui.theme.selectedColor
import com.example.goalfit.ui.theme.unselectedColor
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    navigateToHome: () -> Unit,
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
                "Bienvenido de nuevo",
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                "Inicia sesión para continuar",
                color = Color(0xFFAAAAAA),
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
                placeholder = { Text("Ingresa tu contraseña", color = Color(0xFFAAAAAA)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot password text
            Text(
                "¿Olvidaste tu contraseña?",
                color = selectedColor,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp)
                    .clickable { /* Same functionality */ }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login button
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navigateToHome()
                            Log.i("Login", "Login exitoso")
                        } else {
                            Log.e("Login", "Error en el login")
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
                    "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign up suggestion
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "¿No tienes una cuenta? ",
                    color = selectedColor,
                    fontSize = 14.sp
                )
                Text(
                    "Regístrate",
                    color = selectedColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* Same functionality */ }
                )
            }
        }
    }
}
