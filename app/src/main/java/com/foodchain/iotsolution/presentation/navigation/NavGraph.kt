package com.foodchain.iotsolution.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.foodchain.iotsolution.presentation.auth.login.LoginScreen
import com.foodchain.iotsolution.presentation.auth.signup.SignUpScreen
import com.foodchain.iotsolution.presentation.device.add.AddDeviceScreen
import com.foodchain.iotsolution.presentation.device.detail.DeviceDetailScreen
import com.foodchain.iotsolution.presentation.device.list.DeviceListScreen
import com.foodchain.iotsolution.presentation.home.HomeScreen
import com.foodchain.iotsolution.presentation.map.MapScreen
import com.foodchain.iotsolution.presentation.settings.SettingsScreen
import com.foodchain.iotsolution.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth
        navigation(startDestination = Screen.Login.route, route = "auth") {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignUp.route)
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Main
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDeviceList = {
                    navController.navigate(Screen.DeviceList.route)
                },
                onNavigateToDeviceDetail = { deviceId ->
                    navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.DeviceList.route) {
            DeviceListScreen(
                onNavigateToDeviceDetail = { deviceId ->
                    navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                },
                onNavigateToAddDevice = {
                    navController.navigate(Screen.AddDevice.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.DeviceDetail.route,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) {
            DeviceDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddDevice.route) {
            AddDeviceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDeviceDetail = { deviceId ->
                    navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
