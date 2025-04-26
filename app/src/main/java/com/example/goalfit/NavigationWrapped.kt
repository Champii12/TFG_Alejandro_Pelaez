// NavigationWrapped.kt
package com.example.goalfit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.goalfit.core.entity.ProgresoEntity
import com.example.goalfit.core.entity.RutinaEntity
import com.example.goalfit.screen.home.Home
import com.example.goalfit.screen.peticiondatos.PeticionDatos
import com.example.goalfit.screen.inicio.InicioScreen
import com.example.goalfit.screen.login.LoginScreen
import com.example.goalfit.screen.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavigationWrapped(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    // Si ya hay usuario, arrancamos en "home", si no en "inicio"
    val destinoInicial = if (auth.currentUser != null) "home" else "inicio"

    NavHost(
        navController = navHostController,
        startDestination = destinoInicial
    ) {
        composable("inicio") {
            InicioScreen(
                navigateToLogin = { navHostController.navigate("login") },
                navigateToSignUp = { navHostController.navigate("signup") }
            )
        }
        composable("login") {
            LoginScreen(auth) {
                navHostController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable("signup") {
            SignUpScreen(auth) {
                navHostController.navigate("peticiondatos") {
                    popUpTo("signup") { inclusive = true }
                }
            }
        }
        composable("peticiondatos") {
            PeticionDatos(
                dbFirestore = db,
                navController = navHostController
            )
        }
        composable("home") {
            // 1) Nombre del usuario (lo sacas de FirebaseAuth)
            val usuarioNombre = auth.currentUser?.displayName
                ?: auth.currentUser?.email
                ?: "Usuario"

            // 2) Listas vacías mientras implementas DAO/ViewModel
            val rutinas = emptyList<RutinaEntity>()
            val progresos = emptyList<ProgresoEntity>()

            Home(
                usuarioNombre = usuarioNombre,
                rutinas = rutinas,
                progresos = progresos,
                onClickRutina = { rutinaId ->
                    navHostController.navigate("detalleRutina/$rutinaId")
                },
                onLogout = {
                    auth.signOut()
                    navHostController.navigate("inicio") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
