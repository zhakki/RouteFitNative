package com.example.routefitnative.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.routefitnative.ui.components.BottomNavItem
import com.example.routefitnative.ui.screens.history.HistoryScreen
import com.example.routefitnative.ui.screens.home.HomeScreen
import com.example.routefitnative.ui.screens.login.LoginScreen
import com.example.routefitnative.ui.screens.map.MapScreen
import com.example.routefitnative.ui.screens.profile.ProfileScreen
import com.example.routefitnative.ui.screens.register.RegisterScreen
import com.example.routefitnative.ui.screens.result.ResultScreen
import com.example.routefitnative.ui.screens.route_detail.RouteDetailScreen
import com.example.routefitnative.ui.screens.settings.SettingsScreen
import com.example.routefitnative.ui.screens.statistics.StatisticsScreen

object RouteFitRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MAP = "map"
    const val HISTORY = "history"
    const val STATISTICS = "statistics"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val RESULT = "result"
    const val ROUTE_DETAIL = "route_detail"
}

@Composable
fun RouteFitNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RouteFitRoutes.LOGIN
    ) {
        composable(RouteFitRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(RouteFitRoutes.HOME) {
                        popUpTo(RouteFitRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(RouteFitRoutes.REGISTER)
                }
            )
        }
        composable(RouteFitRoutes.REGISTER) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(RouteFitRoutes.HOME) {
                        popUpTo(RouteFitRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onBackToLoginClick = {
                    navController.navigate(RouteFitRoutes.LOGIN) {
                        popUpTo(RouteFitRoutes.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(RouteFitRoutes.HOME) {
            HomeScreen(
                onBottomNavItemClick = { item ->
                    navController.navigateToBottomNavItem(item)
                }
            )
        }
        composable(RouteFitRoutes.MAP) {
            MapScreen(
                onBottomNavItemClick = { item ->
                    navController.navigateToBottomNavItem(item)
                },
                onStopClick = {
                    navController.navigate(RouteFitRoutes.RESULT)
                }
            )
        }
        composable(RouteFitRoutes.HISTORY) {
            HistoryScreen(
                onBottomNavItemClick = { item ->
                    navController.navigateToBottomNavItem(item)
                },
                onRouteClick = {
                    navController.navigate(RouteFitRoutes.ROUTE_DETAIL)
                }
            )
        }
        composable(RouteFitRoutes.STATISTICS) {
            StatisticsScreen(
                onBottomNavItemClick = { item ->
                    navController.navigateToBottomNavItem(item)
                }
            )
        }
        composable(RouteFitRoutes.PROFILE) {
            ProfileScreen(
                onBottomNavItemClick = { item ->
                    navController.navigateToBottomNavItem(item)
                },
                onSettingsClick = {
                    navController.navigate(RouteFitRoutes.SETTINGS)
                }
            )
        }
        composable(RouteFitRoutes.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.navigate(RouteFitRoutes.PROFILE) {
                        popUpTo(RouteFitRoutes.PROFILE) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(RouteFitRoutes.RESULT) {
            val navigateHome = {
                navController.navigate(RouteFitRoutes.HOME) {
                    popUpTo(RouteFitRoutes.HOME) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            ResultScreen(
                onBackClick = navigateHome,
                onBackHomeClick = navigateHome
            )
        }
        composable(RouteFitRoutes.ROUTE_DETAIL) {
            val navigateHistory = {
                navController.navigate(RouteFitRoutes.HISTORY) {
                    launchSingleTop = true
                }
            }

            RouteDetailScreen(
                onBackClick = navigateHistory,
                onBackToHistoryClick = navigateHistory
            )
        }
    }
}

private fun NavHostController.navigateToBottomNavItem(item: BottomNavItem) {
    val route = when (item) {
        BottomNavItem.Home -> RouteFitRoutes.HOME
        BottomNavItem.Map -> RouteFitRoutes.MAP
        BottomNavItem.History -> RouteFitRoutes.HISTORY
        BottomNavItem.Statistics -> RouteFitRoutes.STATISTICS
        BottomNavItem.Profile -> RouteFitRoutes.PROFILE
    }

    navigate(route) {
        popUpTo(RouteFitRoutes.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
