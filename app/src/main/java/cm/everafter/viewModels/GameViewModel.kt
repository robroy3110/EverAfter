package cm.everafter.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.everafter.classes.Game
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _gameDetails = MutableStateFlow<Game?>(null)
    val gameDetails: StateFlow<Game?> = _gameDetails

    fun getGameDetails(gameId: String) {
        val database = FirebaseDatabase.getInstance("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
        val gamesRef = database.getReference("FreeGames")

        // Query to get the game with the specified ID
        val query = gamesRef.child(gameId)

        // Fetch data from Firebase using the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(Game::class.java)

                viewModelScope.launch {
                    _gameDetails.value = game
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                // You might want to log or handle this error in a better way
                println("Couldn't get the Game from Firebase for some reason...")
            }
        })
    }
}
