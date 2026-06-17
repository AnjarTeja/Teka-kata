package com.example.tekakata.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.tekakata.ui.RewardScreen
import com.example.tekakata.ui.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(
            route = "splash",
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "main",
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            MainScreen(
                onNavigateToLevelSelect = {
                    navController.navigate("levels")
                },
                onNavigateToGame = { levelId ->
                    navController.navigate("game/$levelId")
                },
                onNavigateToRewards = {
                    navController.navigate("rewards")
                }
            )
        }

        composable(
            route = "levels",
            enterTransition = {
                slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
            },
            popEnterTransition = {
                fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(200))
            }
        ) {
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
            route = "rewards",
            enterTransition = {
                slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(200))
            }
        ) {
            RewardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "game/{levelId}",
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType }
            ),
            enterTransition = {
                slideInHorizontally(tween(350)) { it } + fadeIn(tween(350))
            },
            exitTransition = {
                slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(200))
            }
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
