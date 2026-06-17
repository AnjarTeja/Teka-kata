package com.example.tekakata.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tekakata.ui.GameScreen
import com.example.tekakata.ui.GameViewModel
import com.example.tekakata.ui.LevelSelectScreen
import com.example.tekakata.ui.MainScreen
import com.example.tekakata.ui.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onNavigateToLevelSelect = {
                    navController.navigate("levels")
                },
                onNavigateToGame = { levelId ->
                    navController.navigate("game/$levelId")
                }
            )
        }

        composable("levels") {
            LevelSelectScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLevel = { levelId ->
                    navController.navigate("game/$levelId")
                }
            )
        }

        composable(
            route = "game/{levelId}",
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            val gameViewModel: GameViewModel = viewModel()

            GameScreen(
                levelId = levelId,
                viewModel = gameViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLevel = { nextLevel ->
                    navController.navigate("game/$nextLevel") {
                        popUpTo("main")
                    }
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
