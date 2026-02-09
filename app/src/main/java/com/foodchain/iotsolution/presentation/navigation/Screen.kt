package com.foodchain.iotsolution.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Home : Screen("home")
    data object DeviceList : Screen("device_list")
    data object DeviceDetail : Screen("device_detail/{deviceId}") {
        fun createRoute(deviceId: String) = "device_detail/$deviceId"
    }
    data object AddDevice : Screen("add_device")
    data object Map : Screen("map")
    data object Settings : Screen("settings")
}
