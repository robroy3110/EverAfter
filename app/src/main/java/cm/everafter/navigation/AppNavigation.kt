package cm.everafter.navigation

import EditPlaylistScreen
import PlayListScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cm.everafter.NotificationService
import cm.everafter.screens.*
import cm.everafter.screens.camera.FotoScreen
import cm.everafter.screens.games.GamesScreen
import cm.everafter.screens.home.HomeScreen
import cm.everafter.screens.home.LogIn
import cm.everafter.screens.home.ProfileScreen
import cm.everafter.screens.home.Register
import cm.everafter.screens.memories.MemoriesScreen
import cm.everafter.screens.playlist.AddSongsScreen
import cm.everafter.screens.playlist.PlaylistDetailsScreen
import cm.everafter.screens.games.GameDetailsScreen
import cm.everafter.viewModels.LocationViewModel

import cm.everafter.viewModels.UserViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun AppNavigation(
    navController: NavHostController,
    notificationService: NotificationService,
    currentLocation: LatLng?,
) {
    val userViewModel: UserViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    locationViewModel.currentLocation = currentLocation
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route)
    {
        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController = navController, viewModel = userViewModel)
        }
        composable(route = Screens.MemoriesScreen.route){
            MemoriesScreen(navController = navController, viewModel= userViewModel,locationViewModel = locationViewModel)
        }
        composable(route = Screens.CameraScreen.route){
            FotoScreen(userViewModel,locationViewModel)
        }
        composable(route = Screens.SearchUserScreen.route){
            SearchUserScreen(navController = navController)
        }
        composable(route = Screens.ProfileScreen.route){
            ProfileScreen(navController = navController, viewModel = userViewModel)
        }
        composable(route = Screens.GamesScreen.route){
            GamesScreen(navController = navController, viewModel = userViewModel)
        }
        composable(route = "${Screens.GameDetailsScreen.route}/{title}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("title")
            if (gameId != null) {
                GameDetailsScreen(navController = navController, viewModel = viewModel(), gameId = gameId)
            }
        }


        composable(route = Screens.PlaylistScreen.route){
            PlayListScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(route = "${Screens.PlaylistDetailsScreen.route}/{name}") { backStackEntry ->
            val playlistName = backStackEntry.arguments?.getString("name")
            PlaylistDetailsScreen(
                navController = navController,
                playlistViewModel = viewModel(),
                playlistName = playlistName,
                userViewModel = userViewModel
            )
        }

        composable(route = "${Screens.EditPlaylistScreen.route}/{name}") { backStackEntry ->
            val playlistName = backStackEntry.arguments?.getString("name")
            if (playlistName != null) {
                EditPlaylistScreen(
                    navController = navController,
                    playlistViewModel = viewModel(),
                    playlistName = playlistName
                )
            }
        }
        composable(route = "${Screens.AddSongsScreen.route}/{name}") {backStackEntry ->
            val playlistName = backStackEntry.arguments?.getString("name")
            if (playlistName != null) {
                AddSongsScreen(navController = navController,
                    playlistViewModel = viewModel(),
                    playlistName = playlistName)
            }
        }
        composable(route = Screens.LogInScreen.route){
            LogIn(navController = navController)
        }
        composable(route = Screens.RegisterScreen.route){
            Register(navController = navController)
        }
    }
}