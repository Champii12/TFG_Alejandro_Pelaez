package com.example.goalfit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.goalfit.screen.peticiondatos.PeticionDatos
import com.example.goalfit.screen.inicio.InicioScreen
import com.example.goalfit.screen.login.LoginScreen
import com.example.goalfit.screen.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationWrapped(navHostController: NavHostController, auth: FirebaseAuth) {
    NavHost(navController = navHostController, startDestination = "inicio") {
        composable ("inicio") {
            InicioScreen(
                navigateToLogin = { navHostController.navigate("login") },
                navigateToSignUp = { navHostController.navigate("signup") }
            )
        }
        composable("login" ) {
            LoginScreen(auth)
        }
        composable("signup") {
            SignUpScreen(auth)
        }
        composable("peticiondatos") {
            PeticionDatos()
        }
    }
}