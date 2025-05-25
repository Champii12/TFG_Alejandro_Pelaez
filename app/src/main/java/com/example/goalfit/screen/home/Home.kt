package com.example.goalfit.screen.home

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.goalfit.core.entity.ProgresoEntity
import com.example.goalfit.core.entity.RutinaEntity
import com.example.goalfit.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

// Objeto para gestionar el tema de la aplicación
object ThemeManager {
    private val _useSystemTheme = mutableStateOf(true)
    val useSystemTheme: State<Boolean> = _useSystemTheme

    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    @Composable
    fun isAppInDarkTheme(): Boolean {
        return if (useSystemTheme.value) {
            isSystemInDarkTheme()
        } else {
            isDarkTheme.value
        }
    }

    fun toggleTheme() {
        _useSystemTheme.value = false
        _isDarkTheme.value = !_isDarkTheme.value
    }
}

// Objeto de colores actualizado para responder al cambio de tema
object AppColors {
    private val lightGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFF5722), Color(0xFFFF7043), Color(0xFFFFAB91))
    )
    private val lightCardBg = Color.White
    private val lightTextPrimary = Color(0xFF333333)
    private val lightTextSecondary = Color(0xFF666666)
    private val lightAccent = Color(0xFFFF7043)
    private val lightCardTransparent = Color.White.copy(alpha = 0.15f)
    private val lightIconTint = Color.White

    private val darkGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3D1D14), Color(0xFF4A2419), Color(0xFF5A2E20))
    )
    private val darkCardBg = Color(0xFF2D2D2D)
    private val darkTextPrimary = Color.White
    private val darkTextSecondary = Color(0xFFBBBBBB)
    private val darkAccent = Color(0xFFFF8A65)
    private val darkCardTransparent = Color(0xFF3D3D3D).copy(alpha = 0.3f)
    private val darkIconTint = Color(0xFFEEEEEE)

    @Composable fun gradient(): Brush = if (ThemeManager.isAppInDarkTheme()) darkGradient else lightGradient
    @Composable fun cardBg(): Color = if (ThemeManager.isAppInDarkTheme()) darkCardBg else lightCardBg
    @Composable fun textPrimary(): Color = if (ThemeManager.isAppInDarkTheme()) darkTextPrimary else lightTextPrimary
    @Composable fun textSecondary(): Color = if (ThemeManager.isAppInDarkTheme()) darkTextSecondary else lightTextSecondary
    @Composable fun accent(): Color = if (ThemeManager.isAppInDarkTheme()) darkAccent else lightAccent
    @Composable fun cardTransparent(): Color = if (ThemeManager.isAppInDarkTheme()) darkCardTransparent else lightCardTransparent
    @Composable fun iconTint(): Color = if (ThemeManager.isAppInDarkTheme()) darkIconTint else lightIconTint
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    usuarioNombre: String,
    photoUrl: String?,
    rutinas: List<RutinaEntity>,
    progresos: List<ProgresoEntity>,
    onClickRutina: (Int) -> Unit,
    onEditRutina: (Int) -> Unit,
    onDeleteRutina: (Int) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onCreateRutina: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val menuExpanded by homeViewModel.menuExpanded.collectAsState()
    val density = LocalDensity.current
    val lazyListState = rememberLazyListState()
    val isScrolled = remember { derivedStateOf { lazyListState.firstVisibleItemIndex > 0 } }
    val fabElevation by animateDpAsState(
        targetValue = if (isScrolled.value) 8.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "FAB Elevation Animation"
    )
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    val isDarkTheme = ThemeManager.isAppInDarkTheme()
    val themeState = ThemeManager.isDarkTheme.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.gradient())
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { homeViewModel.toggleMenu() },
                            modifier = Modifier.semantics {
                                contentDescription = "Abrir menú de usuario"
                            }
                        ) {
                            if (photoUrl != null) {
                                AsyncImage(
                                    model = Uri.parse(photoUrl),
                                    contentDescription = "Avatar usuario",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Avatar genérico",
                                    tint = AppColors.iconTint()
                                )
                            }
                        }
                    },
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.semantics {
                                contentDescription = "Bienvenido de nuevo, $usuarioNombre"
                            }
                        ) {
                            Text(
                                "¡Bienvenido de nuevo!",
                                style = MaterialTheme.typography.titleLarge,
                                color = AppColors.iconTint(),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                usuarioNombre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.iconTint().copy(alpha = 0.8f)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { ThemeManager.toggleTheme() },
                            modifier = Modifier.semantics {
                                contentDescription = if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro"
                            }
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                contentDescription = null,
                                tint = AppColors.iconTint()
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onCreateRutina,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nueva Rutina") },
                    containerColor = AppColors.cardBg(),
                    contentColor = AppColors.accent(),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.semantics {
                        contentDescription = "Crear nueva rutina"
                    },
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = fabElevation)
                )
            },
            floatingActionButtonPosition = FabPosition.Start,
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.gradient())
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { ProgresoCard(progresos = progresos, homeViewModel = homeViewModel) }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.semantics {
                                contentDescription = "Sección de tus rutinas"
                            }
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = AppColors.iconTint()
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Tus rutinas",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.iconTint()
                            )
                        }
                    }
                    items(rutinas) { rutina ->
                        AnimatedVisibility(
                            visibleState = visibleState,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { with(density) { 40.dp.roundToPx() } },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                            exit = fadeOut()
                        ) {
                            var itemMenuExpanded by remember { mutableStateOf(false) }
                            Box {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = { onClickRutina(rutina.idRutina) },
                                            onLongClick = { itemMenuExpanded = true }
                                        )
                                        .clip(RoundedCornerShape(16.dp))
                                        .semantics { contentDescription = "Rutina: ${rutina.nombreRutina}" },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg())
                                ) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(AppColors.accent()),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                rutina.nombreRutina.first().uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        }
                                        Column(
                                            Modifier
                                                .padding(start = 16.dp)
                                                .weight(1f)
                                        ) {
                                            Text(
                                                rutina.nombreRutina,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = AppColors.textPrimary(),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            val fechaFmt = remember(rutina.fechaCreacion) {
                                                SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                                    .format(rutina.fechaCreacion)
                                            }
                                            Text(
                                                "Creada el $fechaFmt",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AppColors.textSecondary()
                                            )
                                        }
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = AppColors.accent(),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = itemMenuExpanded,
                                    onDismissRequest = { itemMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Editar") },
                                        onClick = {
                                            itemMenuExpanded = false
                                            onEditRutina(rutina.idRutina)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Eliminar") },
                                        onClick = {
                                            itemMenuExpanded = false
                                            onDeleteRutina(rutina.idRutina)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        AnimatedVisibility(
            visible = menuExpanded,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300, easing = EaseOutQuad)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300, easing = EaseInQuad)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { homeViewModel.closeMenu() }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                        .align(Alignment.CenterStart)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.accent()),
                                contentAlignment = Alignment.Center
                            ) {
                                if (photoUrl != null) {
                                    AsyncImage(
                                        model = Uri.parse(photoUrl),
                                        contentDescription = "Avatar menú",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Text(
                                text = usuarioNombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.textPrimary(),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            )
                            IconButton(onClick = { homeViewModel.closeMenu() }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar menú",
                                    tint = AppColors.textPrimary()
                                )
                            }
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = AppColors.textSecondary().copy(alpha = 0.3f)
                        )
                        MenuOption(
                            icon = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            text = if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                            onClick = { ThemeManager.toggleTheme() }
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = AppColors.textSecondary().copy(alpha = 0.3f)
                        )
                        MenuOption(icon = Icons.Default.Edit, text = "Editar perfil", onClick = {
                            homeViewModel.closeMenu(); onEditProfile()
                        })
                        MenuOption(icon = Icons.Default.FitnessCenter, text = "Mis rutinas", onClick = {
                            homeViewModel.closeMenu()
                        })
                        MenuOption(icon = Icons.Default.Add, text = "Crear nueva rutina", onClick = {
                            homeViewModel.closeMenu(); onCreateRutina()
                        })
                        MenuOption(icon = Icons.Default.BarChart, text = "Estadísticas", onClick = {
                            homeViewModel.closeMenu()
                        })
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = AppColors.textSecondary().copy(alpha = 0.3f)
                        )
                        MenuOption(icon = Icons.Default.Settings, text = "Configuración", onClick = {
                            homeViewModel.closeMenu()
                        })
                        Spacer(modifier = Modifier.weight(1f))
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = AppColors.textSecondary().copy(alpha = 0.3f)
                        )
                        MenuOption(icon = Icons.Default.Logout, text = "Cerrar sesión", onClick = {
                            homeViewModel.closeMenu(); onLogout()
                        }, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    tint: Color = AppColors.textPrimary()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = tint, modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun ProgresoCard(
    progresos: List<ProgresoEntity>,
    homeViewModel: HomeViewModel
) {
    val completadas = homeViewModel.getRutinasCompletadasMes(progresos)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Rutinas completadas este mes: $completadas" },
        colors = CardDefaults.cardColors(containerColor = AppColors.cardTransparent()),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AppColors.iconTint(), modifier = Modifier
                .size(48.dp)
                .padding(end = 16.dp))
            Column {
                Text("Rutinas completadas este mes:", style = MaterialTheme.typography.titleMedium, color = AppColors.iconTint())
                Text("$completadas", style = MaterialTheme.typography.headlineMedium, color = AppColors.iconTint(), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RutinaCard(
    rutina: RutinaEntity,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Card Elevation Animation"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                isPressed = true
                onClick()
            }
            .semantics { contentDescription = "Rutina: ${rutina.nombreRutina}" },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AppColors.accent()), contentAlignment = Alignment.Center) {
                Text(rutina.nombreRutina.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column(modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)) {
                Text(rutina.nombreRutina, style = MaterialTheme.typography.titleMedium, color = AppColors.textPrimary(), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                val fechaFmt = remember(rutina.fechaCreacion) {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(rutina.fechaCreacion)
                }
                Text("Creada el $fechaFmt", style = MaterialTheme.typography.bodySmall, color = AppColors.textSecondary())
            }
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = AppColors.accent(), modifier = Modifier.size(24.dp))
        }
    }
}
