package cm.everafter.viewModels

import androidx.lifecycle.ViewModel

import android.media.MediaPlayer
import android.net.Uri
import cm.everafter.classes.Playlist

import cm.everafter.classes.Song
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaylistViewModel : ViewModel() {
    // Shared state to hold the selected playlist name
    private var _selectedPlaylistName = ""
    private var mediaPlayer: MediaPlayer? = null

    private val _playlistState = MutableStateFlow<Playlist?>(null)
    val playlistState: StateFlow<Playlist?> = _playlistState

    fun getPlaylist(playlistName: String?) {
        if (playlistName != null) {
            val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
            val playlistsRef = database.getReference("Playlists")

            // Query to get the playlist with the specified name
            val query = playlistsRef.orderByChild("name").equalTo(playlistName)
            println("----------------- ViewModel -----------------")

            // Fetch data from Firebase using the query
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Iterate through the dataSnapshot to get the playlist
                        for (playlistSnapshot in snapshot.children) {
                            val playlist = playlistSnapshot.getValue(Playlist::class.java)
                            println(playlist)

                            playlist?.let {
                                println(it.description)
                                _playlistState.value = it

                            }
                            // Assuming there's only one playlist with the given name
                            break
                        }
                    } else {
                        // Playlist with the specified name not found
                        _playlistState.value = null
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    println("Couldn't get the Playlist from Firebase for some reason...")
                }
            })
        }
    }

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
