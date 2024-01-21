package cm.everafter.classes

data class RelationShip(val pointsTotal: Int = 0,val pointsGames: Int= 0, val pointsMusic: Int = 0,val pointsDate: Int = 0,val pointsPictures: Int = 0, val date: String = "0-0-0", val user1: String = "", val user2: String = "", val favgames: List<Game>)