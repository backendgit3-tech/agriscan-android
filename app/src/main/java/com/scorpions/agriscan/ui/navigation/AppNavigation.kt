package com.scorpions.agriscan.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Diagnose : Screen("diagnose")
    data object Camera : Screen("camera")
    data object Result : Screen("result/{predictionJson}") {
        fun createRoute(encodedJson: String) = "result/$encodedJson"
    }
    data object History : Screen("history")
    data object HistoryDetail : Screen("history/{predictionId}") {
        fun createRoute(id: Long) = "history/$id"
    }
    data object About : Screen("about")
    data object Login : Screen("login")
}

sealed class BottomNavItem(val route: String, val label: String, val labelUrdu: String) {
    data object Home : BottomNavItem("home", "Home", "ہوم")
    data object Diagnose : BottomNavItem("diagnose", "Diagnose", "تشخیص")
    data object History : BottomNavItem("history", "History", "تاریخ")
    data object About : BottomNavItem("about", "About", "تعارف")
}
