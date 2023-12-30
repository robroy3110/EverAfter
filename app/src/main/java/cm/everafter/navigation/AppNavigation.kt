package cm.everafter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cm.everafter.Perfil
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
            MemoriesScreen(navController = navController)
        }
        composable(route = Screens.SearchUserScreen.route){
            SearchUserScreen(navController = navController)
        }
        composable(route = Screens.ProfileScreen.route){
            ProfileScreen(navController = navController)
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