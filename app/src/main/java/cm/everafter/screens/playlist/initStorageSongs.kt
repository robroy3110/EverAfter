package cm.everafter.screens.playlist

import cm.everafter.classes.Song
import com.google.firebase.Firebase
import com.google.firebase.database.database

fun initializeSongs() {
    // Clear existing songs in the "Songs" node
    clearSongsFromDatabase()

    // Create Song instances and associate them with MP3 files
    val song1 = createSong("Valerie", "Amy Winehouse", "Back To Black", "", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    /*
    val song2 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song3 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song4 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song5 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song6 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song7 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song8 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song9 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song10 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song11 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song12 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song13 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song14 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
    val song15 = createSong("Valerie", "Amy Winehouse", "Back To Black", "AWValerie", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3")
*/


    // Save the Song instances to database (Firebase Realtime Database)
    saveSongsToDatabase(listOf(song1))
}
fun clearSongsFromDatabase() {
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val songsRef = database.getReference("Songs")

    // Remove all songs from the "Songs" node
    songsRef.removeValue()
}

fun createSong(name: String, artist: String, album: String, uri: String, storagePath: String): Song {
    // Create a Song instance with details and storagePath
    return Song(name, artist, album, uri, storagePath)
}

fun saveSongsToDatabase(songs: List<Song>) {
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val songsRef = database.getReference("Songs")
    // add each song to the database
    for (song in songs) {
        // Generate a unique key for the song
        val songKey = songsRef.push().key ?: continue

        // Save the song to the Firebase Realtime Database
        songsRef.child(songKey).setValue(song)
    }
}