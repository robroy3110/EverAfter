package cm.everafter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cm.everafter.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route)
    {
        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController = navController)
        }
        composable(route = Screens.MemoriesScreen.route){
            MemoriesScreen(navController = navController)
        }
        composable(route = Screens.CameraScreen.route){
            CameraScreen(navController = navController)
        }
        composable(route = Screens.GamesScreen.route){
            GamesScreen(navController = navController)
        }
        composable(route = Screens.PlaylistScreen.route){
            PlayListScreen(navController = navController)
        }
        composable(route = Screens.LogInScreen.route){
            LogIn(navController = navController)
        }
        composable(route = Screens.RegisterScreen.route){
            Register(navController = navController)
        }
    }
}