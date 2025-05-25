package com.example.goalfit.screen.rutinas

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.goalfit.core.viewmodel.EjercicioRun
import com.example.goalfit.screen.home.AppColors
import com.example.goalfit.screen.home.ThemeManager
import kotlinx.coroutines.delay

private enum class Mode { exercise, selectRest, rest, finished }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunRutinaScreen(
    rutinaNombre: String,
    ejercicios: List<EjercicioRun>,
    onFinishRutina: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // Construimos un ImageLoader que soporte GIFs
    val gifLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    // Tema
    val isDark = ThemeManager.isAppInDarkTheme()
    // para forzar recomposición al cambiar tema
    val themeState = ThemeManager.isDarkTheme.value

    // Si no hay ejercicios
    if (ejercicios.isEmpty()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(AppColors.gradient()),
            contentAlignment = Alignment.Center
        ) {
            Card(
                Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg().copy(alpha = 0.9f))
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.accent(),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No hay ejercicios para ejecutar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.textPrimary(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onCancel,
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.accent())
                    ) {
                        Text("Volver", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    // Estados de la rutina
    var index by remember { mutableStateOf(0) }
    var mode by remember { mutableStateOf(Mode.exercise) }
    var timeLeft by remember { mutableStateOf(0) }
    var restDur by remember { mutableStateOf(0) }

    // Animación de progreso
    val totalTime = when (mode) {
        Mode.exercise -> ejercicios[index].duracionSegundos
        Mode.rest -> restDur
        else -> 1
    }
    val progress by animateFloatAsState(
        targetValue = if (totalTime > 0) 1f - (timeLeft.toFloat() / totalTime) else 0f,
        animationSpec = tween(300),
        label = "Progress"
    )

    // Countdown
    LaunchedEffect(index, mode, restDur) {
        timeLeft = when (mode) {
            Mode.exercise -> ejercicios[index].duracionSegundos
            Mode.rest -> restDur
            else -> 0
        }
        while (timeLeft > 0) {
            delay(1_000L)
            timeLeft--
        }
        mode = when (mode) {
            Mode.exercise ->
                if (index < ejercicios.lastIndex) Mode.selectRest else Mode.finished
            Mode.rest -> {
                index++
                if (index <= ejercicios.lastIndex) Mode.exercise else Mode.finished
            }
            else -> mode
        }
    }

    // Color de botones y bordes — naranja
    val buttonColor by animateColorAsState(
        targetValue = if (mode != Mode.finished) Color(0xFFFF7043) else Color.Gray,
        animationSpec = tween(300),
        label = "ButtonColor"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(AppColors.gradient())
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Rutina: $rutinaNombre", color = AppColors.iconTint(), fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.ArrowBack, null, tint = AppColors.iconTint())
                        }
                    },
                    actions = {
                        IconButton(onClick = { ThemeManager.toggleTheme() }) {
                            Icon(
                                if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                null, tint = AppColors.iconTint()
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = AppColors.iconTint()
                    )
                )
            },
            bottomBar = {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, buttonColor),
                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg().copy(alpha = 0.9f))
                ) {
                    when (mode) {
                        Mode.finished -> {
                            Button(
                                onClick = onFinishRutina,
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Finalizar Rutina", fontWeight = FontWeight.Bold)
                            }
                        }
                        Mode.exercise -> ExerciseBar(index, ejercicios.size) {
                            mode = if (index < ejercicios.lastIndex) Mode.selectRest else Mode.finished
                        }
                        Mode.selectRest -> SelectRestBar(
                            onSelect = { m ->
                                restDur = m * 60
                                mode = Mode.rest
                            },
                            onSkip = {
                                index++
                                mode = if (index < ejercicios.size) Mode.exercise else Mode.finished
                            }
                        )
                        Mode.rest -> RestTimerBar(timeLeft, progress)
                    }
                }
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = mode) { current ->
                    when (current) {
                        Mode.exercise -> {
                            val ej = ejercicios[index]
                            val name = ej.nombre.lowercase().replace(' ', '_')
                            val assetPath = "file:///android_asset/gifs/$name.gif"
                            // Pintor con GIF loader
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(assetPath)
                                    .build(),
                                imageLoader = gifLoader
                            )
                            Card(
                                Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(16.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg())
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.Black.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painter,
                                            contentDescription = ej.nombre,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(Modifier.height(24.dp))
                                    Text(
                                        ej.nombre,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = AppColors.textPrimary(),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Box(
                                        Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.accent().copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "%d:%02d".format(timeLeft / 60, timeLeft % 60),
                                            style = MaterialTheme.typography.displaySmall,
                                            color = AppColors.accent(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    LinearProgressIndicator(
                                        progress = progress,
                                        Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = AppColors.accent(),
                                        trackColor = AppColors.accent().copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                        else -> { /* resto de modos ya renderizado en bottomBar */ }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseBar(
    index: Int,
    total: Int,
    onSkip: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Ejercicio ${index + 1}/$total", style = MaterialTheme.typography.bodyLarge, color = AppColors.textPrimary())
        TextButton(onClick = onSkip, colors = ButtonDefaults.textButtonColors(contentColor = AppColors.accent())) {
            Text("Saltar ejercicio")
        }
    }
}

@Composable
private fun SelectRestBar(onSelect: (Int) -> Unit, onSkip: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Selecciona tiempo de descanso:", style = MaterialTheme.typography.bodyMedium, color = AppColors.textPrimary())
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 2, 5).forEach { m ->
                Button(onClick = { onSelect(m) }, Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.accent())) {
                    Icon(Icons.Default.Timer, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("$m min")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSkip, Modifier.align(Alignment.End), colors = ButtonDefaults.textButtonColors(contentColor = AppColors.accent())) {
            Text("Saltar descanso")
        }
    }
}

@Composable
private fun RestTimerBar(timeLeft: Int, progress: Float) {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("%d:%02d".format(timeLeft / 60, timeLeft % 60), style = MaterialTheme.typography.headlineMedium, color = AppColors.textPrimary(), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = AppColors.accent(),
            trackColor = AppColors.accent().copy(alpha = 0.2f)
        )
    }
}
