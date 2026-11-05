package com.subloop.userinterface.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.subloop.data.SubLoopRepository
import com.subloop.userinterface.screens.DetailScreen
import com.subloop.userinterface.screens.LoginScreen
import com.subloop.userinterface.screens.MainShell
import com.subloop.userinterface.screens.RegisterScreen
import com.subloop.viewmodel.AuthViewModel
import com.subloop.viewmodel.AuthViewModelFactory
import com.subloop.viewmodel.SubscriptionViewModel
import com.subloop.viewmodel.SubscriptionViewModelFactory

@Composable
fun SubLoopApp(repository: SubLoopRepository) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))
    val authState by authViewModel.uiState.collectAsState()
    val start = if (repository.currentUserId == null) LoginRoute else MainRoute

    NavHost(navController = navController, startDestination = start) {
        composable<LoginRoute> {
            LaunchedEffect(Unit) { authViewModel.clearError() }
            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(MainRoute) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            }
            LoginScreen(
                state = authState,
                onLogin = authViewModel::login,
                onRegister = { navController.navigate(RegisterRoute) },
            )
        }

        composable<RegisterRoute> {
            LaunchedEffect(Unit) { authViewModel.clearError() }
            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(MainRoute) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            }
            RegisterScreen(
                state = authState,
                onRegister = authViewModel::register,
                onLogin = { navController.popBackStack() },
            )
        }

        composable<MainRoute> { entry ->
            val subscriptionViewModel: SubscriptionViewModel = viewModel(
                viewModelStoreOwner = entry,
                factory = SubscriptionViewModelFactory(repository),
            )
            MainShell(
                viewModel = subscriptionViewModel,
                onOpenDetail = { navController.navigate(DetailRoute(it)) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                },
            )
        }

        composable<DetailRoute> { entry ->
            val route = entry.toRoute<DetailRoute>()
            val mainEntry = navController.getBackStackEntry<MainRoute>()
            val subscriptionViewModel: SubscriptionViewModel = viewModel(
                viewModelStoreOwner = mainEntry,
                factory = SubscriptionViewModelFactory(repository),
            )
            DetailScreen(
                subscriptionId = route.subscriptionId,
                viewModel = subscriptionViewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
