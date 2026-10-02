package com.scorpions.agriscan.ui.navigation

import com.scorpions.agriscan.ui.screens.home.HomeScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.scorpions.agriscan.AgriScanApp
import com.scorpions.agriscan.data.api.PredictionResult
import com.scorpions.agriscan.data.api.RetrofitClient
import com.scorpions.agriscan.data.repository.AuthRepository
import com.scorpions.agriscan.data.repository.HistoryRepository
import com.scorpions.agriscan.data.repository.PredictionRepository
import com.scorpions.agriscan.ui.screens.about.AboutScreen
import com.scorpions.agriscan.ui.screens.auth.AuthViewModel
import com.scorpions.agriscan.ui.screens.auth.LoginScreen
import com.scorpions.agriscan.ui.screens.camera.CameraScreen
import com.scorpions.agriscan.ui.screens.diagnose.DiagnoseScreen
import com.scorpions.agriscan.ui.screens.diagnose.DiagnoseViewModel
import com.scorpions.agriscan.ui.screens.history.HistoryDetailScreen
import com.scorpions.agriscan.ui.screens.history.HistoryScreen
import com.scorpions.agriscan.ui.screens.history.HistoryViewModel
import com.scorpions.agriscan.ui.screens.home.HomeScreen
import com.scorpions.agriscan.ui.screens.home.HomeViewModel
import com.scorpions.agriscan.ui.screens.result.ResultScreen
import com.scorpions.agriscan.ui.theme.AgriGreen700
import com.scorpions.agriscan.ui.theme.Charcoal300

data class BottomNavItemData(
    val route: String,
    val label: String,
    val labelUrdu: String,
    val icon: ImageVector
)

@Composable
fun AgriScanNavHost(
    app: AgriScanApp,
    navController: NavHostController,
    isUrdu: Boolean,
    onLanguageToggle: () -> Unit
) {
    val predictionRepo = remember {
        PredictionRepository(RetrofitClient.api, app.database)
    }
    val historyRepo = remember { HistoryRepository(app.database) }
    val authRepo = remember { AuthRepository(RetrofitClient.api, app.userPreferences) }

    val homeViewModel = remember { HomeViewModel(historyRepo) }
    val diagnoseViewModel = remember { DiagnoseViewModel(predictionRepo) }
    val historyViewModel = remember { HistoryViewModel(historyRepo) }
    val authViewModel = remember { AuthViewModel(authRepo) }

    val bottomNavItems = listOf(
        BottomNavItemData("home", "Home", "ہوم", Icons.Default.Home),
        BottomNavItemData("diagnose", "Diagnose", "تشخیص", Icons.Default.Search),
        BottomNavItemData("history", "History", "تاریخ", Icons.Default.History),
        BottomNavItemData("about", "About", "تعارف", Icons.Default.Info)
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF0F3520).copy(alpha = 0.95f),
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = {
                                Text(
                                    if (isUrdu) item.labelUrdu else item.label
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0A2A1A),      // Dark green
                                selectedTextColor = Color(0xFFD4AF37),      // Gold
                                indicatorColor = Color(0xFFD4AF37),         // Gold pill
                                unselectedIconColor = Color(0xFFF4F1EA).copy(alpha = 0.5f),
                                unselectedTextColor = Color(0xFFF4F1EA).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToDiagnose = {
                        diagnoseViewModel.reset()
                        navController.navigate(Screen.Diagnose.route)
                    },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    isUrdu = isUrdu
                )
            }

            composable(Screen.Diagnose.route) {
                // ... rest of the code stays the same
                DiagnoseScreen(
                    viewModel = diagnoseViewModel,
                    onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                    onResultReady = { encodedJson ->
                        diagnoseViewModel.reset()
                        navController.navigate(Screen.Result.createRoute(encodedJson)) {
                            popUpTo(Screen.Diagnose.route) { inclusive = true }
                        }
                    },
                    isUrdu = isUrdu
                )
            }

            composable(Screen.Camera.route) {
                CameraScreen(
                    onImageCaptured = { uri, file ->
                        diagnoseViewModel.setImage(uri, file)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() },
                    isUrdu = isUrdu
                )
            }

            composable(
                route = Screen.Result.route,
                arguments = listOf(navArgument("predictionJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedJson = backStackEntry.arguments?.getString("predictionJson") ?: ""
                val json = java.net.URLDecoder.decode(encodedJson, "UTF-8")

                val result = parsePredictionResult(json)

                ResultScreen(
                    result = result,
                    onScanAnother = {
                        diagnoseViewModel.reset()
                        navController.navigate(Screen.Diagnose.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    isUrdu = isUrdu
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onPredictionClick = { id ->
                        navController.navigate(Screen.HistoryDetail.createRoute(id))
                    },
                    onNavigateToDiagnose = {
                        diagnoseViewModel.reset()
                        navController.navigate(Screen.Diagnose.route)
                    },
                    isUrdu = isUrdu
                )
            }

            composable(
                route = Screen.HistoryDetail.route,
                arguments = listOf(navArgument("predictionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("predictionId") ?: 0L
                val prediction by historyViewModel.selectedPrediction.collectAsState()

                LaunchedEffect(id) {
                    historyViewModel.selectPrediction(id)
                }

                prediction?.let {
                    HistoryDetailScreen(
                        prediction = it,
                        onBack = { navController.popBackStack() },
                        isUrdu = isUrdu
                    )
                }
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onLanguageToggle = onLanguageToggle,
                    isUrdu = isUrdu
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    isUrdu = isUrdu
                )
            }
        }
    }
}

private fun parsePredictionResult(json: String): PredictionResult {
    return try {
        val disease = extractField(json, "disease")
        val confidence = extractField(json, "confidence").toFloatOrNull() ?: 0f
        val treatment = extractField(json, "treatment")
        val mode = extractField(json, "mode")
        val source = extractField(json, "source")
        val qwenOpinion = extractField(json, "qwenOpinion").ifBlank { null }
        val imageUri = extractField(json, "imageUri").ifBlank { null }

        PredictionResult(
            disease = disease,
            confidence = confidence,
            treatment = treatment,
            mode = mode,
            source = source,
            qwenOpinion = qwenOpinion,
            imageUri = imageUri
        )
    } catch (e: Exception) {
        PredictionResult(
            disease = "Unknown",
            confidence = 0f,
            treatment = "",
            mode = "unknown",
            source = "error",
            qwenOpinion = null,
            imageUri = null,
            error = e.message
        )
    }
}

private fun extractField(json: String, field: String): String {
    val pattern = "\"$field\"\\s*:\\s*\"([^\"]*)\""
    val match = Regex(pattern).find(json)
    if (match != null) return match.groupValues[1]

    val numPattern = "\"$field\"\\s*:\\s*([\\d.]+)"
    val numMatch = Regex(numPattern).find(json)
    return numMatch?.groupValues?.get(1) ?: ""
}
