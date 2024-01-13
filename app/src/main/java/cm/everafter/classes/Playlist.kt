package cm.everafter.classes

class Playlist(
    val relationship: String = "",
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "Default Location",
    val imageUri: String = "", // You can use Uri for local image, or String for remote image URL
    val songs: List<Song> = emptyList()
){

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