package cm.everafter.viewModels

import androidx.lifecycle.ViewModel

import android.media.MediaPlayer
import android.net.Uri

import cm.everafter.classes.Song
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class PlaylistViewModel : ViewModel() {
    // Shared state to hold the selected playlist name
    private var _selectedPlaylistName = ""
    private var mediaPlayer: MediaPlayer? = null

    val selectedPlaylistName: String
        get() = _selectedPlaylistName

    // Function to update the selected playlist name
    fun selectPlaylist(playlistName: String) {
        _selectedPlaylistName = playlistName
    }

    // Function to play a song
    fun playSong(song: Song) {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer().apply {
            // Assuming storagePath is a Firebase Cloud Storage path
            val storageReference = Firebase.storage.getReference(getRelativePath(song.storagePath))
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                try {
                    setDataSource(uri.toString())
                    prepare()
                    start()
                } catch (e: Exception) {
                    // Handle exception related to setting the data source
                    e.printStackTrace()
                }
            }.addOnFailureListener { exception ->
                // Handle failure to get download URL
                exception.printStackTrace()
            }
        }
    }

    // Function to get the relative path from a full URL
    private fun getRelativePath(fullUrl: String): String {
        val uri = Uri.parse(fullUrl)
        return uri.path ?: ""
    }


    // Function to stop playback
    fun stopPlayback() {
        mediaPlayer?.stop()
    }

    // Release resources when the ViewModel is cleared
    override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }
}
