package cm.everafter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cm.everafter.navigation.AppNavigation
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import cm.everafter.navigation.NavItem
import cm.everafter.navigation.Screens

import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EverAfter(notificationService: NotificationService, currentLocation: LatLng?) {
    val context = LocalContext.current
    val listOfNavItems = listOf(
        NavItem(
            label = "Home",
            icon = Icons.Filled.Favorite,
            route = Screens.HomeScreen.route
        ),
        NavItem(
            label = "Memories",
            icon = Icons.Filled.Image,
            route = Screens.MemoriesScreen.route
        ),
        NavItem(
            label = "Camera",
            icon = Icons.Default.AddCircle,
            route = Screens.CameraScreen.route
        ),
        NavItem(
            label = "Games",
            icon = Icons.Filled.VideogameAsset,
            route = Screens.GamesScreen.route
        ),
        NavItem(
            label = "Playlists",
            icon = Icons.Filled.MusicNote,
            route = Screens.PlaylistScreen.route
        ),
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route == "login_screen" || currentDestination?.route == "register_screen") {
        Scaffold(
            modifier = Modifier,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                AppNavigation(
                    navController = navController,
                    notificationService = notificationService,
                    currentLocation = currentLocation
                )
            }
        }
    } else {
        Scaffold(
            modifier = Modifier,
            bottomBar = {
                NavigationBar {

                    listOfNavItems.forEach { navItem ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                            onClick = {
                                navController.navigate(navItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = navItem.label)
                            }
                        )
                    }
                }

            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                AppNavigation(navController = navController, notificationService, currentLocation)
            }
        }
    }

}



