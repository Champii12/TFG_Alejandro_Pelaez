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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Preview
@Composable
fun InicioScreen(
    navigateToLogin: () -> Unit = {}, navigateToSignUp: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LightGray, Gray), startY = 0f, endY = 500f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "GoalFit",
            fontStyle = FontStyle.Italic,
            fontSize = 32.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp))
        Text(text = "Empieza tu cambio ahora.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = LightRed,
            modifier = Modifier.padding(8.dp))
        Text(text = "Gratis en GoalFit.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = LightRed,
            modifier = Modifier.padding(8.dp))

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {navigateToSignUp()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text("Registrate gratis")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {navigateToLogin()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red)
        ) {
            Text("Inicia sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

