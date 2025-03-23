package com.example.goalfit.screen.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
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
fun LoginScreen(auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column (modifier = Modifier
        .fillMaxSize()
        .background(Black)
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row (

        ){

            Icon(painter = painterResource(
                id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = White,
                modifier = Modifier
                    .padding(vertical = 16.dp).size(24.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Text("Email", color = White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = unselectedColor,
                focusedIndicatorColor = selectedColor,
            )

        )
        Spacer(modifier = Modifier.height(48.dp))
        Text("Password", color = White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = unselectedColor,
                focusedIndicatorColor = selectedColor,
        ))
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                if (task.isSuccessful){
                    Log.i("Login", "Login exitoso")
                } else{
                    Log.e("Login", "Error en el login")
                }
            }
        } ) {
            Text("Iniciar Sesión")
        }


    }

}
