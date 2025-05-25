package com.example.goalfit

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.data.RoutineRepository
import com.example.goalfit.core.entity.UserEntity
import com.example.goalfit.core.viewmodel.RoutineViewModel
import com.example.goalfit.core.viewmodel.RoutineViewModelFactory
import com.example.goalfit.core.viewmodel.RunRutinaViewModel
import com.example.goalfit.core.viewmodel.RunRutinaViewModelFactory
import com.example.goalfit.core.viewmodel.RutinaDetailViewModel
import com.example.goalfit.core.viewmodel.RutinaDetailViewModelFactory
import com.example.goalfit.home.HomeViewModel
import com.example.goalfit.screen.crearRutina.CreateRutinaRoute
import com.example.goalfit.screen.crearRutina.UiEjercicio
import com.example.goalfit.screen.home.Home
import com.example.goalfit.screen.home.HomeViewModelFactory
import com.example.goalfit.screen.home.PantallaEditar
import com.example.goalfit.screen.inicio.InicioScreen
import com.example.goalfit.screen.login.LoginScreen
import com.example.goalfit.screen.peticiondatos.PeticionDatos
import com.example.goalfit.screen.rutinas.EditRutinaRoute
import com.example.goalfit.screen.rutinas.RunRutinaScreen
import com.example.goalfit.screen.rutinas.RutinaDetalle
import com.example.goalfit.screen.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun NavigationWrapped(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    val context = LocalContext.current.applicationContext as Application
    val startDestination = if (auth.currentUser != null) "home" else "inicio"

    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable("inicio") {
            InicioScreen(
                navigateToLogin = { navHostController.navigate("login") },
                navigateToSignUp = { navHostController.navigate("signup") }
            )
        }

        composable("login") {
            LoginScreen(
                auth             = auth,
                navigateToHome   = { navHostController.navigate("home") },
                navigateBack     = { navHostController.popBackStack() },
                navigateToSignUp = { navHostController.navigate("signup") }
            )
        }

        composable("signup") {
            SignUpScreen(
                auth = auth,
                onSignUpSuccess = { navHostController.navigate("peticiondatos") },
                navigateBack = { navHostController.popBackStack() }
            )
        }

        composable("peticiondatos") {
            PeticionDatos(
                dbFirestore = db,
                onDataSaved = {
                    navHostController.navigate("home") {
                        popUpTo("peticiondatos") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navHostController.navigate("inicio") {
                        popUpTo("home") { inclusive = true }
                    }
                }
                return@composable
            }

            val homeVm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(
                    app = context,
                    firestore = db,
                    auth = auth
                )
            )
            val rutinas by homeVm.rutinas.collectAsState()
            val progresos by homeVm.progresos.collectAsState()

            val userDao = AppDatabase.getInstance(context).userDao()
            val uid = currentUser.uid
            val userState by userDao.getByIdFlow(uid).collectAsState(initial = null)

            val usuarioNombre = userState?.nombre
                ?.takeIf { it.isNotBlank() }
                ?: currentUser.email
                ?: "Usuario"

            Home(
                usuarioNombre = usuarioNombre,
                photoUrl = userState?.photoUrl,
                rutinas = rutinas.map { it.rutina },
                progresos = progresos,
                onClickRutina = { idRutina ->
                    val nombre = rutinas
                        .map { it.rutina }
                        .firstOrNull { it.idRutina == idRutina }
                        ?.nombreRutina
                        .orEmpty()
                    navHostController.navigate("detalleRutina/$idRutina/$nombre")
                },
                onEditRutina = { idRutina ->
                    // Navegar a la pantalla de edición de rutina
                    navHostController.navigate("editRutina/$idRutina")
                },
                onDeleteRutina = { idRutina ->
                    // Llamar al ViewModel para eliminar
                    homeVm.deleteRutina(idRutina)
                },
                onEditProfile = { navHostController.navigate("editProfile") },
                onLogout = {
                    auth.signOut()
                    navHostController.navigate("inicio") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onCreateRutina = { navHostController.navigate("createRutina") }
            )
        }

        composable("editProfile") {
            val repo = RoutineRepository(
                firestore = db,
                auth = auth,
                localDb = AppDatabase.getInstance(context)
            )
            LaunchedEffect(Unit) { repo.ensureUserLocal() }

            val userDao = AppDatabase.getInstance(context).userDao()
            val uid = auth.currentUser!!.uid
            val userState by userDao.getByIdFlow(uid).collectAsState(initial = null)
            val scope = rememberCoroutineScope()

            if (userState == null) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                PantallaEditar(
                    user = userState!!,
                    onBack = { navHostController.popBackStack() },
                    onSave = { updated, newImageUri ->
                        scope.launch {
                            val localUser = updated.copy(photoUrl = newImageUri?.toString())
                            userDao.insertarUsuario(localUser)
                            db.collection("usuarios")
                                .document(uid)
                                .update(
                                    mapOf(
                                        "photoUrl" to newImageUri?.toString(),
                                        "nombre" to updated.nombre,
                                        "edad" to updated.edad,
                                        "altura" to updated.altura,
                                        "peso" to updated.peso,
                                        "nivelExperiencia" to updated.nivelExperiencia,
                                        "objetivo" to updated.objetivo
                                    )
                                )
                                .await()
                            navHostController.popBackStack()
                        }
                    }
                )
            }
        }

        composable("createRutina") {
            val repo = RoutineRepository(
                firestore = db,
                auth = auth,
                localDb = AppDatabase.getInstance(context)
            )
            val rutinaVm: RoutineViewModel = viewModel(
                factory = RoutineViewModelFactory(repo)
            )
            CreateRutinaRoute(
                viewModel = rutinaVm,
                onBack = { navHostController.popBackStack() },
                onComplete = { navHostController.popBackStack() }
            )
        }

        composable(
            "detalleRutina/{rutinaId}/{rutinaNombre}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("rutinaNombre") { type = NavType.StringType }
            )
        ) { backStack ->
            val rutinaId = backStack.arguments!!.getInt("rutinaId")
            val rutinaNombre = backStack.arguments!!.getString("rutinaNombre")!!
            val detailVm: RutinaDetailViewModel = viewModel(
                factory = RutinaDetailViewModelFactory(context, rutinaId)
            )
            val ejercicios by detailVm.ejercicios.collectAsState()
            RutinaDetalle(
                rutinaNombre = rutinaNombre,
                ejercicios = ejercicios,
                onBack = { navHostController.popBackStack() },
                onStart = {
                    navHostController.navigate("runRutina/$rutinaId/$rutinaNombre")
                }
            )
        }

        composable(
            "runRutina/{rutinaId}/{rutinaNombre}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("rutinaNombre") { type = NavType.StringType }
            )
        ) { backStack ->
            val rutinaId = backStack.arguments!!.getInt("rutinaId")
            val rutinaNombre = backStack.arguments!!.getString("rutinaNombre")!!

            val runVm: RunRutinaViewModel = viewModel(
                factory = RunRutinaViewModelFactory(
                    app = context,
                    rutinaId = rutinaId,
                    repo = RoutineRepository(db, auth, AppDatabase.getInstance(context))
                )
            )
            val ejerciciosRaw by runVm.ejerciciosConDuracion.collectAsState()

            val ejerciciosPorSerie = remember(ejerciciosRaw) {
                val maxSeries = ejerciciosRaw.maxOfOrNull { it.series ?: 1 } ?: 1
                (1..maxSeries).flatMap { serie ->
                    ejerciciosRaw.filter { (it.series ?: 1) >= serie }
                }
            }


            RunRutinaScreen(
                rutinaNombre = rutinaNombre,
                ejercicios = ejerciciosPorSerie,
                onFinishRutina = {
                    runVm.registrarProgreso {
                        navHostController.popBackStack("home", inclusive = false)
                    }
                },
                onCancel = { navHostController.popBackStack() }
            )
        }


        composable(
            "editRutina/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStack ->
            val rutinaId = backStack.arguments!!.getInt("rutinaId")
            val rutinaDao = AppDatabase.getInstance(context).rutinaDao()
            var nombreRutina by remember { mutableStateOf("") }
            LaunchedEffect(rutinaId) {
                rutinaDao.getById(rutinaId)?.let { nombreRutina = it.nombreRutina }
            }

            val detailVm: RutinaDetailViewModel = viewModel(
                factory = RutinaDetailViewModelFactory(context, rutinaId)
            )
            val ejerciciosDomain by detailVm.ejercicios.collectAsState(initial = emptyList())
            val initialEjercicios = remember(ejerciciosDomain) {
                ejerciciosDomain.map { dom ->
                    UiEjercicio(
                        nombre = dom.nombre,
                        series = "",
                        repeticiones = "",
                        categoria = dom.categoria ?: "",
                        descripcion = dom.descripcion ?: ""
                    )
                }
            }

            EditRutinaRoute(
                initialNombre = nombreRutina,
                initialEjercicios = initialEjercicios,
                onBack = { navHostController.popBackStack() },
                onSaveRoutine = { newName, nuevosEj ->
                    // TODO: llamar a ViewModel/Repo para guardar cambios
                    navHostController.popBackStack()
                }
            )
        }
    }
}