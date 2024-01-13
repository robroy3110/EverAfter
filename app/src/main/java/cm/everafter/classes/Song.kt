package cm.everafter.classes



class Song(
    val name: String = "",
    val artist: String = "",
    val album: String = "",
    val uri: String = "",
    val storagePath: String = "" // assuming you have a storage path field
) {
    // Empty constructor required by Firebase
    constructor() : this("", "", "", "", "")
}
