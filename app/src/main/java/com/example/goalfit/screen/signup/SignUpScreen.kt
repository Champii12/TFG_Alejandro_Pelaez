// SignUpScreen.kt
package com.example.goalfit.screen.signup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = White,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(24.dp)
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
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("SignUp", "Registro exitoso")
                        onSignUpSuccess()            // ← aquí navegamos
                    } else {
                        Log.e("SignUp", "Error en el registro", task.exception)
                    }
                }
        }) {
            Text("Registrarse")
        }
    }
}
