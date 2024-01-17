package cm.everafter.screens.playlist

import cm.everafter.classes.Song
import com.google.firebase.Firebase
import com.google.firebase.database.database

fun initializeSongs() {
    // Clear existing songs in the "Songs" node
    clearSongsFromDatabase()

    // Create Song instances and associate them with MP3 files
    val song12 = createSong("Always Somewhere", "Scorpions", "Lovedrive", "", "gs://everafter-382e1.appspot.com/Songs/Scorpions_-_Always_Somewhere_HQ_Audio.mp3","gs://everafter-382e1.appspot.com/SongsPics/Scorpions-album-lovedrive.jpg")
    val song2 = createSong("Arabella", "Arctic Monkeys", "AM", "", "gs://everafter-382e1.appspot.com/Songs/Arctic_Monkeys_-_Arabella_Official_Audio.mp3", "gs://everafter-382e1.appspot.com/SongsPics/AM.jpg")

    val song3 = createSong("Black No.1 Little Miss Scare All", "Type O Negative", "Bloody Kisses (Top Shelf Edition)", "", "gs://everafter-382e1.appspot.com/Songs/Black_No._1_Little_Miss_Scare_-All.mp3","gs://everafter-382e1.appspot.com/SongsPics/Bloodykisses.jpg")
    val song4 = createSong("By the Way", "Red Hot Chilli Peppers", "By the Way", "", "gs://everafter-382e1.appspot.com/Songs/By_the_Way.mp3","gs://everafter-382e1.appspot.com/SongsPics/Red_Hot_Chili_Peppers_-_By_the_Way.jpg")
    val song15 = createSong("Bring Me To Life", "Evanescence", "Fallen", "", "gs://everafter-382e1.appspot.com/Songs/Bring_Me_To_Life_-_Evanescence_Audio.mp3", "gs://everafter-382e1.appspot.com/SongsPics/Fallen.jpg")

    val song5 = createSong("Closer", "Nine Inch Nails", "The Downward Spiral", "", "gs://everafter-382e1.appspot.com/Songs/Closer.mp3", "gs://everafter-382e1.appspot.com/SongsPics/The_Downward_Spiral__by_Nine_Inch_Nails.png")
    val song10 = createSong("Cigarette", "OFFONOFF Feat. Tablo", "boy.", "", "gs://everafter-382e1.appspot.com/Songs/OFFONOFF_-_Cigarette_Feat._Tablo___MISO.mp3","gs://everafter-382e1.appspot.com/SongsPics/offonoff.jpg")


    val song6 = createSong("Heart Shaped Box", "Nirvana", "In Utero (Deluxe Edition)", "", "gs://everafter-382e1.appspot.com/Songs/Nirvana_-_Heart_Shaped_Box.mp3", "gs://everafter-382e1.appspot.com/SongsPics/InUtero.jpeg")

    val song7 = createSong("I WANNA BE YOUR SLAVE", "Maneskin", "Teatro d'ira - Vol.I", "", "gs://everafter-382e1.appspot.com/Songs/Maneskin_-_I_WANNA_BE_YOUR_SLAVE_Audio.mp3","gs://everafter-382e1.appspot.com/SongsPics/Måneskin_Teatro_d'ira_Vol._I.png")

    val song13 = createSong("Jeremy", "Pearl Jam", "Ten", "", "gs://everafter-382e1.appspot.com/Songs/Pearl_Jam_-_Jeremy_Video.mp3", "gs://everafter-382e1.appspot.com/SongsPics/Pearl_Jam_-_Ten.jpg")

    val song11 = createSong("Need You Now", "Lady Antebellum", "Need You Now", "", "gs://everafter-382e1.appspot.com/Songs/01.Need_You_Now-_Lady_Antebellum_Audio.mp3", "gs://everafter-382e1.appspot.com/SongsPics/Need_You_Now.jpg")

    val song9 = createSong("Roslyn", "Bon Iver, St. Vincent ", "The Twilight Saga (Original Motion Picture Soundtrack)", "", "gs://everafter-382e1.appspot.com/Songs/8._Roslyn_-_Bon_Iver___St._Vincent.mp3", "gs://everafter-382e1.appspot.com/SongsPics/bon_iver.jpeg")

    val song14 = createSong("Since I Don't Have You", "The Skyliners", "Since I Don't Have You", "", "gs://everafter-382e1.appspot.com/Songs/The_Skyliners_-_Since_I_Dont_Have_You.mp3", "gs://everafter-382e1.appspot.com/SongsPics/the_skyliners.jpg")

    val song1 = createSong("Valerie", "Amy Winehouse", "Back To Black", "", "gs://everafter-382e1.appspot.com/Songs/Amy_Winehouse_-_Valerie.mp3","gs://everafter-382e1.appspot.com/SongsPics/Capa_de_Back_to_Black.jpg")

    val song8 = createSong("Undisclosed Desires", "Muse", "The Resistance", "", "gs://everafter-382e1.appspot.com/Songs/Muse_-_Undisclosed_Desires.mp3","gs://everafter-382e1.appspot.com/SongsPics/muse.jpg")



    // Save the Song instances to database (Firebase Realtime Database)
    saveSongsToDatabase(listOf(song1, song2, song3, song4, song5, song6, song7, song8, song9,song10,song11, song12,song13,song14, song15))
}
fun clearSongsFromDatabase() {
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val songsRef = database.getReference("Songs")

    // Remove all songs from the "Songs" node
    songsRef.removeValue()
}

fun createSong(name: String, artist: String, album: String, uri: String, storagePath: String, imagePath: String): Song {
    // Create a Song instance with details and storagePath
    return Song(name, artist, album, uri, storagePath, imagePath)
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