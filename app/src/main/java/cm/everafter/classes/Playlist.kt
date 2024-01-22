package cm.everafter.classes

class Playlist(
    val relationship: String = "",
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val imageUri: String = "",
    val songs: List<Song> = emptyList()
){
    // Copy function to create a new Playlist with specified changes
    fun copy(
        relationship: String = this.relationship,
        name: String = this.name,
        description: String = this.description,
        date: String = this.date,
        imageUri: String = this.imageUri,
        songs: List<Song>? = this.songs
    ): Playlist {
        return Playlist(relationship, name, description, date, imageUri, songs ?: emptyList())
    }
}
