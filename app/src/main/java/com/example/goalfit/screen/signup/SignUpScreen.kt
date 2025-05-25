package com.example.goalfit.screen.signup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goalfit.screen.home.AppColors
import com.example.goalfit.screen.home.ThemeManager
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    auth: FirebaseAuth,
    onSignUpSuccess: () -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Observar el estado del tema
    val isDarkTheme = ThemeManager.isAppInDarkTheme()

    // Forzar recomposición cuando cambia el tema
    val themeState = ThemeManager.isDarkTheme.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.gradient())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra superior con botón de retroceso y botón de tema
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de retroceso
                IconButton(
                    onClick = { navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A1A))
                        .semantics {
                            contentDescription = "Volver atrás"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Botón para alternar entre modo claro y oscuro
                IconButton(
                    onClick = { ThemeManager.toggleTheme() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A1A))
                        .semantics {
                            contentDescription = if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro"
                        }
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título y subtítulo
            Text(
                "Crear cuenta",
                color = AppColors.iconTint(),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                "Regístrate para comenzar",
                color = AppColors.iconTint().copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 40.dp)
            )

            // Campo de correo electrónico
            Text(
                "Correo electrónico",
                color = AppColors.iconTint(),
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
                    unfocusedContainerColor = if (isDarkTheme) Color(0xFF333333) else Color(0x33000000),
                    focusedContainerColor = if (isDarkTheme) Color(0xFF444444) else Color(0x55000000),
                    focusedIndicatorColor = AppColors.accent(),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AppColors.accent(),
                    unfocusedTextColor = AppColors.iconTint(),
                    focusedTextColor = AppColors.iconTint()
                ),
                placeholder = {
                    Text(
                        "ejemplo@correo.com",
                        color = AppColors.iconTint().copy(alpha = 0.6f)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))


            // Contraseña con toggle visibilidad
            Text(
                "Contraseña",
                color = AppColors.iconTint(),
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
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector =
                                if (passwordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                            contentDescription =
                                if (passwordVisible) "Ocultar contraseña"
                                else "Mostrar contraseña"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor =
                        if (isDarkTheme) Color(0xFF333333) else Color(0x33000000),
                    focusedContainerColor =
                        if (isDarkTheme) Color(0xFF444444) else Color(0x55000000),
                    focusedIndicatorColor = AppColors.accent(),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AppColors.accent(),
                    unfocusedTextColor = AppColors.iconTint(),
                    focusedTextColor = AppColors.iconTint()
                ),
                placeholder = {
                    Text("Crea una contraseña segura", color = AppColors.iconTint().copy(alpha = 0.6f))
                }
            )


            Spacer(modifier = Modifier.height(48.dp))

            // Botón de registro
            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Log.i("SignUp", "Registro exitoso")
                            onSignUpSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("SignUp", "Error en el registro", e)
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics {
                        contentDescription = "Botón para registrarse"
                    },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Enlace para iniciar sesión
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .semantics {
                        contentDescription = "Enlace para iniciar sesión si ya tienes cuenta"
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "¿Ya tienes una cuenta? ",
                    color = AppColors.iconTint().copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    "Iniciar sesión",
                    color = AppColors.accent(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { navigateBack() }
                )
            }
        }
    }
}