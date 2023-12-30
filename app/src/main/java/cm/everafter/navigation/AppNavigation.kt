package cm.everafter.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cm.everafter.Perfil
import cm.everafter.screens.*
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
            MemoriesScreen(navController = navController)
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
        composable(route = Screens.LogInScreen.route){
            LogIn(navController = navController)
        }
        composable(route = Screens.RegisterScreen.route){
            Register(navController = navController)
        }
    }
}