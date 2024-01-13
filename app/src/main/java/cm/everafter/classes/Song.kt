package cm.everafter.classes



class Song(
    val name: String,
    val artist: String,
    val album: String,
    val uri: String, // Unique identifier for the song
    val storagePath: String // Firebase Storage path or URL for the MP3 file
)
