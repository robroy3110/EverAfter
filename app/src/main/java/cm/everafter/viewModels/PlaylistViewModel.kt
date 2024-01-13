package cm.everafter.viewModels

import androidx.lifecycle.ViewModel

class PlaylistViewModel : ViewModel() {
    // Shared state to hold the selected playlist name
    private var _selectedPlaylistName = ""

    val selectedPlaylistName: String
        get() = _selectedPlaylistName

    // Function to update the selected playlist name
    fun selectPlaylist(playlistName: String) {
        _selectedPlaylistName = playlistName
    }
}