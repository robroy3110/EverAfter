package cm.everafter.classes

data class Game(
    val id: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val short_description: String = "",
    val game_url: String = "",
    val genre: String = "",
    val platform: String = "",
    val publisher: String = "",
    val developer: String = "",
    val release_date: String = "",
    val freetogame_profile_url: String = "",
    val free_start_date: String = "",
    val free_end_date: String = ""
)