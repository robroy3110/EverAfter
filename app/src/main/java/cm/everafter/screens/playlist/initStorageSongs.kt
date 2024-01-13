package cm.everafter.screens.playlist

import cm.everafter.classes.Song
import com.google.firebase.Firebase
import com.google.firebase.database.database

fun initializeSongs() {
    // Create Song instances and associate them with MP3 files
    val song1 = createSong("Valerie", "Amy Winehouse", "Album 1", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    // TODO:Add more songs

    // Save the Song instances to database (Firebase Realtime Database)
    saveSongsToDatabase(listOf(song1))
}

fun createSong(name: String, artist: String, album: String, uri: String, storagePath: String): Song {
    // Create a Song instance with details and storagePath
    return Song(name, artist, album, uri, storagePath)
}

fun saveSongsToDatabase(songs: List<Song>) {
    // Database save logic here

    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val songsRef = database.getReference("Songs")

    // Iterate over the list of songs and add each song to the database
    for (song in songs) {
        // Generate a unique key for the song
        val songKey = songsRef.push().key ?: continue

        // Save the song to the Firebase Realtime Database
        songsRef.child(songKey).setValue(song)
    }
}