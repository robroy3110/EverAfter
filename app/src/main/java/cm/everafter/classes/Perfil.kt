package cm.everafter.classes

data class Perfil( val name: String = "", val image: String = "", val relationship: String = "", val username: String = "", val notifications: List<String> = mutableListOf())