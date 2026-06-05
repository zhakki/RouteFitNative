package com.example.routefitnative.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routefitnative.viewmodel.TrackingViewModel
import com.example.routefitnative.ui.components.BottomNavItem
import com.example.routefitnative.ui.screens.history.HistoryScreen
import com.example.routefitnative.ui.screens.home.HomeScreen
import com.example.routefitnative.ui.screens.login.LoginScreen
import com.example.routefitnative.ui.screens.map.MapScreen
import com.example.routefitnative.ui.screens.profile.EditProfileScreen
import com.example.routefitnative.ui.screens.profile.ProfileScreen
import com.example.routefitnative.ui.screens.register.RegisterScreen
import com.example.routefitnative.ui.screens.result.ResultScreen
import com.example.routefitnative.ui.screens.route_detail.RouteDetailScreen
import com.example.routefitnative.ui.screens.settings.SettingsScreen
import com.example.routefitnative.ui.screens.statistics.StatisticsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

object RouteFitRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MAP = "map"
    const val HISTORY = "history"
    const val STATISTICS = "statistics"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val SETTINGS = "settings"
    const val RESULT = "result"
    const val ROUTE_DETAIL = "route_detail"
    const val ROUTE_DETAIL_WITH_ID = "route_detail/{routeId}"

    fun routeDetail(routeId: String): String {
        return "$ROUTE_DETAIL/$routeId"
    }
}

@Composable
fun RouteFitNavigation() {
    val navController = rememberNavController()
    // Shared ViewModel for tracking and results
    val trackingViewModel: TrackingViewModel = viewModel()

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
                },
                trackingViewModel = trackingViewModel
            )
        }
        composable(RouteFitRoutes.HISTORY) {
            HistoryScreen(
                onBottomNavItemClick = { item ->
                    navController.navigateToBottomNavItem(item)
                },
                onRouteClick = { routeId ->
                    navController.navigate(RouteFitRoutes.routeDetail(routeId))
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
                },
                onEditProfileClick = {
                    navController.navigate(RouteFitRoutes.EDIT_PROFILE)
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
                },
                onEditProfileClick = {
                    navController.navigate(RouteFitRoutes.EDIT_PROFILE)
                },
                onLogoutClick = {
                    navController.navigate(RouteFitRoutes.LOGIN) {
                        popUpTo(RouteFitRoutes.HOME) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(RouteFitRoutes.EDIT_PROFILE) {
            val navigateProfile = {
                navController.navigate(RouteFitRoutes.PROFILE) {
                    popUpTo(RouteFitRoutes.PROFILE) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            EditProfileScreen(
                onBackClick = navigateProfile,
                onSaveClick = navigateProfile,
                onCancelClick = navigateProfile
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
                onBackHomeClick = navigateHome,
                trackingViewModel = trackingViewModel
            )
        }
        composable(
            route = RouteFitRoutes.ROUTE_DETAIL_WITH_ID,
            arguments = listOf(
                navArgument("routeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId").orEmpty()

            val navigateHistory = {
                navController.navigate(RouteFitRoutes.HISTORY) {
                    launchSingleTop = true
                }
            }

            RouteDetailScreen(
                routeId = routeId,
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
