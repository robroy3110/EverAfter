package cm.everafter

class Playlist(
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "Default Location",
    val imageUri: String? = null, // You can use Uri for local image, or String for remote image URL
    val songs: List<Song> = emptyList()
){
    fun copy(
        name: String = this.name,
        description: String = this.description,
        date: String = this.date,
        location: String = this.location,
        imageUri: String? = this.imageUri,
        songs: List<Song> = this.songs
    ): Playlist {
        return Playlist(name, description, date, location, imageUri, songs)
    }
}
/*

Playlist instance actually should have:
- Relationship;
- Name
- Description
- Date
- Location (use default current location, maybe using the google API);
- Image
- List Of Songs added by the user, from spotify api

 */