package cm.everafter.viewModels

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import cm.everafter.classes.Playlist
import cm.everafter.classes.Song
import cm.everafter.screens.home.storage
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
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
                            //println(playlist)

                            playlist?.let {
                                //println(it.description)
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
        Log.e("MediaPlayer", "Deu RESET: ")

        mediaPlayer = MediaPlayer().apply {
            // Assuming storagePath is a Firebase Cloud Storage path
            val storageReference = Firebase.storage.getReference(getRelativePath(song.storagePath))
            Log.e("MediaPlayer", storageReference.toString())
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                try {
                    setDataSource(uri.toString())
                    prepare()
                    start()
                    Log.e("MediaPlayer", "Started Song!")
                } catch (e: Exception) {
                    // Handle exception related to setting the data source
                    e.printStackTrace()
                    Log.e("MediaPlayer", "Error setting data source: ${e.message}")

                }
            }.addOnFailureListener { exception ->
                // Handle failure to get download URL
                exception.printStackTrace()
                Log.e("MediaPlayer", "Error getting download URL: ${exception.message}")

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

    fun addSongsToPlaylist(playlistName: String, songs: List<Song>) {
        // Ensure that there's a selected playlist
        if (playlistName.isEmpty()) {
            println("Nao recebeu nome da playlist")
            return
        }

        val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
        val playlistsRef = database.getReference("Playlists")

        // Query to get the playlist with the specified name
        val query = playlistsRef.orderByChild("name").equalTo(playlistName)

        // Fetch data from Firebase using the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Iterate through the dataSnapshot to get the playlist
                    for (playlistSnapshot in snapshot.children) {
                        val playlist = playlistSnapshot.getValue(Playlist::class.java)

                        playlist?.let {
                            val updatedSongs = (it.songs ?: emptyList()) + songs

                            // Update the playlist by adding new songs
                            playlistsRef.child(playlistSnapshot.key ?: "").child("songs").setValue(updatedSongs)
                            _playlistState.value = it.copy(songs = updatedSongs)
                        }

                        // Assuming there's only one playlist with the given name
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                println("Couldn't get the Playlist from Firebase for some reason...")
            }
        })
    }



    // Inside PlaylistViewModel class
    fun deletePlaylist(playlistName: String) {
        val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
        val playlistsRef = database.getReference("Playlists")

        // Query to get the playlist with the specified name
        val query = playlistsRef.orderByChild("name").equalTo(playlistName)

        // Fetch data from Firebase using the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Iterate through the dataSnapshot to get the playlist
                    for (playlistSnapshot in snapshot.children) {
                        // Remove the playlist from the database
                        playlistsRef.child(playlistSnapshot.key ?: "").removeValue()

                        // Clear the playlist state
                        _playlistState.value = null
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if the query is canceled
                println("Couldn't delete the Playlist from Firebase for some reason...")
            }
        })
    }

    fun deleteSongFromPlaylist(playlistName: String, song: Song) {
        val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
        val playlistsRef = database.getReference("Playlists")

        // Query to get the playlist with the specified name
        val query = playlistsRef.orderByChild("name").equalTo(playlistName)

        // Fetch data from Firebase using the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Iterate through the dataSnapshot to get the playlist
                    for (playlistSnapshot in snapshot.children) {
                        val playlist = playlistSnapshot.getValue(Playlist::class.java)

                        playlist?.let {
                            // Filter out the song to be deleted
                            val updatedSongs = it.songs?.filter { it.name != song.name } ?: emptyList()

                            // Update the playlist by removing the song
                            playlistsRef.child(playlistSnapshot.key ?: "").child("songs").setValue(updatedSongs)
                            _playlistState.value = it.copy(songs = updatedSongs)
                        }

                        // Assuming there's only one playlist with the given name
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                println("Couldn't get the Playlist from Firebase for some reason...")
            }
        })
    }


    fun updatePlaylistName(playlistName: String, newPlaylistName: String) {
        val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
        val playlistsRef = database.getReference("Playlists")

        // Query to get the playlist with the specified name
        val query = playlistsRef.orderByChild("name").equalTo(playlistName)

        // Fetch data from Firebase using the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Iterate through the dataSnapshot to get the playlist
                    for (playlistSnapshot in snapshot.children) {
                        // Update the playlist name
                        playlistsRef.child(playlistSnapshot.key ?: "").child("name").setValue(newPlaylistName)

                        // Update the playlist state
                        _playlistState.value = _playlistState.value?.copy(name = newPlaylistName)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if the query is canceled
                println("Couldn't update the Playlist name in Firebase for some reason...")
            }
        })
    }

    fun updatePlaylistImageUri(playlistName: String, fileName: String) {
        val storageRef = storage.reference.child("PlaylistsPics/$fileName")

        // Get the download URL for the image
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            // Update the playlist image URI in the database with the full URL
            updatePlaylistImage(playlistName, uri.toString())
        }.addOnFailureListener { exception ->
            // Handle the failure to get the download URL
            println("Couldn't get the download URL for the uploaded image: ${exception.message}")
        }
    }

    private fun updatePlaylistImage(playlistName: String, newImageUri: String) {
        val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
        val playlistsRef = database.getReference("Playlists")

        // Query to get the playlist with the specified name
        val query = playlistsRef.orderByChild("name").equalTo(playlistName)

        // Fetch data from Firebase using the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Iterate through the dataSnapshot to get the playlist
                    for (playlistSnapshot in snapshot.children) {
                        // Update the playlist image URI
                        playlistsRef.child(playlistSnapshot.key ?: "").child("imageUri")
                            .setValue(newImageUri)

                        // Update the local playlist state
                        _playlistState.value = _playlistState.value?.copy(imageUri = newImageUri)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if the query is canceled
                println("Couldn't update the Playlist image in Firebase for some reason...")
            }
        })
    }


    fun uploadImageToStorage(playlistName: String, imageUri: Uri) {
        Log.e("UPLOAD IMG TO STORAGE", playlistName)
        val storageRef = storage.reference.child("PlaylistsPics/$playlistName.jpg")

        // Upload the file to Firebase Storage
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // File uploaded successfully
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update the playlist's image URI in the database
                    updatePlaylistImage(playlistName, "$playlistName.jpg")
                }
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful upload
                // You might want to show an error message to the user
            }
    }

}


