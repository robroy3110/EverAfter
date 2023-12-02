package cm.everafter.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,

    )

val listOfNavItems = listOf(

    NavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = Screens.HomeScreen.route
    ),
    NavItem(
        label = "Memories",
        icon = Icons.Default.Home,
        route = Screens.MemoriesScreen.route
    ),
    NavItem(
        label = "Camera",
        icon = Icons.Default.Home,
        route = Screens.CameraScreen.route
    ),
    NavItem(
        label = "Free Games",
        icon = Icons.Default.Home,
        route = Screens.GamesScreen.route
    ),
    NavItem(
        label = "Playlist",
        icon = Icons.Default.Home,
        route = Screens.PlaylistScreen.route
    ),
)