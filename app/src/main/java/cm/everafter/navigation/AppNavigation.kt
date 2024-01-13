package cm.everafter.navigation

import PlayListScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cm.everafter.screens.*
import cm.everafter.screens.camera.FotoScreen
import cm.everafter.screens.games.GamesScreen
import cm.everafter.screens.home.LogIn
import cm.everafter.screens.home.ProfileScreen
import cm.everafter.screens.home.Register
import cm.everafter.screens.memories.MemoriesScreen
import cm.everafter.screens.playlist.EditPlaylistScreen

import cm.everafter.viewModels.UserViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route)
    {
        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController = navController, viewModel = userViewModel)
        }
        composable(route = Screens.MemoriesScreen.route){
            MemoriesScreen(navController = navController)
        }
        composable(route = Screens.CameraScreen.route){
            FotoScreen(navController = navController)
        }
        composable(route = Screens.SearchUserScreen.route){
            SearchUserScreen(navController = navController)
        }
        composable(route = Screens.ProfileScreen.route){
            ProfileScreen(navController = navController, viewModel = userViewModel)
        }
        composable(route = Screens.GamesScreen.route){
            GamesScreen(navController = navController)
        }
        composable(route = Screens.PlaylistScreen.route){
            PlayListScreen(navController = navController)
        }
        composable(route = Screens.EditPlaylistScreen.route){
            EditPlaylistScreen(navController = navController)
        }
        composable(route = Screens.LogInScreen.route){
            LogIn(navController = navController)
        }
        composable(route = Screens.RegisterScreen.route){
            Register(navController = navController)
        }
    }
}