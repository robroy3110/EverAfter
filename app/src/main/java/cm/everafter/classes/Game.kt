package cm.everafter.classes

data class Game(
    val id: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val shortDescription: String = "",
    val gameUrl: String = "",
    val genre: String = "",
    val platform: String = "",
    val publisher: String = "",
    val developer: String = "",
    val releaseDate: String = "",
    val freeToGameProfileUrl: String = "",
    val free_start_date: String = "",
    val free_end_date: String = ""
)