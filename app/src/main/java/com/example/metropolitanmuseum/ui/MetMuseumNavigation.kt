package com.example.metropolitanmuseum.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.metropolitanmuseum.ui.screens.DetailScreen
import com.example.metropolitanmuseum.ui.screens.FavoritesScreen
import com.example.metropolitanmuseum.ui.screens.MainScreen
import com.example.metropolitanmuseum.ui.screens.SearchScreen

@Composable
fun MetMuseumNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                onNavigateToDetail = { objectId ->
                    navController.navigate("detail/$objectId")
                },
                onNavigateToSearch = {
                    navController.navigate("search")
                },
                onNavigateToFavorites = {
                    navController.navigate("favorites")
                }
            )
        }

        composable("search") {
            SearchScreen(
                onNavigateToDetail = { objectId ->
                    navController.navigate("detail/$objectId")
                },
                onNavigateToFavorites = {
                    navController.navigate("favorites")
                }
            )
        }

        composable(
            "detail/{objectId}",
            arguments = listOf(navArgument("objectId") { type = NavType.IntType })
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                onNavigateToDetail = { objectId ->
                    navController.navigate("detail/$objectId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}