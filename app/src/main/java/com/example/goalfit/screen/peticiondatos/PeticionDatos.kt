package com.example.goalfit.screen.peticiondatos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

@Composable
fun PeticionDatos() {
    val database = Firebase.firestore
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(48.dp)) {
        Text(text = "HOLA MUNDO ESTOY LOGUEADO")
    }
}

fun crearUsuario(){

}