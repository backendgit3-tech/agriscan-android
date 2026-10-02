package com.scorpions.agriscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.scorpions.agriscan.ui.navigation.AgriScanNavHost
import com.scorpions.agriscan.ui.theme.AgriScanTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)   // ✅ Pass the parameter
        enableEdgeToEdge()

        setContent {
            val app = application as AgriScanApp
            val language by app.userPreferences.language.collectAsState(initial = "en")
            val isRtl = language == "ur"
            val isUrdu = language == "ur"
            val scope = rememberCoroutineScope()

            AgriScanTheme {
                CompositionLocalProvider(
                    androidx.compose.ui.platform.LocalLayoutDirection provides
                            if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        AgriScanNavHost(
                            app = app,
                            navController = navController,
                            isUrdu = isUrdu,
                            onLanguageToggle = {
                                scope.launch {
                                    val newLang = if (language == "en") "ur" else "en"
                                    app.userPreferences.setLanguage(newLang)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
