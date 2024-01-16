package cm.everafter.navigation

sealed class Screens(val route: String) {
    object HomeScreen : Screens("home_screen")
    object CameraScreen : Screens("camera_screen")
    object GamesScreen : Screens("games_screen")
    object PlaylistScreen : Screens("playlist_screen")
    object EditPlaylistScreen : Screens("edit_playlist_screen/{name}")
    object AddSongsScreen : Screens("add_songs_screen")
    object ProfileScreen : Screens("profile_screen")
    object MemoriesScreen : Screens("memories_screen")
    object LogInScreen : Screens("login_screen")
    object RegisterScreen : Screens("register_screen")

    object SearchUserScreen : Screens("searchUser_screen")





}
