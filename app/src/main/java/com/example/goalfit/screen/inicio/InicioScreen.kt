package com.example.goalfit.screen.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.goalfit.ui.theme.LightGray
import com.example.goalfit.ui.theme.LightRed
import com.example.goalfit.ui.theme.Red
import com.example.goalfit.ui.theme.White

@Preview
@Composable
fun InicioScreen(
    navigateToLogin: () -> Unit = {}, navigateToSignUp: () -> Unit = {}
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo o icono de la app
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            )

            {
                // ESTO ES POR SI ME DA TIEMPO A PONER UN LOGO
                Text(
                    text = "GF",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título de la app
            Text(
                text = "GoalFit",
                fontStyle = FontStyle.Italic,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulos
            Text(
                text = "Empieza tu cambio ahora.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Gratis en GoalFit.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Botón de registro
            Button(
                onClick = { navigateToSignUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    "Regístrate gratis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de inicio de sesión
            Button(
                onClick = { navigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x99FFFFFF),
                    contentColor = Color(0xFF333333)
                )
            ) {
                Text(
                    "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Texto de política de privacidad
            Text(
                text = "Al continuar, aceptas nuestros Términos y Política de Privacidad",
                fontSize = 12.sp,
                color = White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
