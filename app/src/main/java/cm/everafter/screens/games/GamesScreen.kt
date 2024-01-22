package cm.everafter.screens.games

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import cm.everafter.NotificationService
import cm.everafter.classes.Game
import cm.everafter.screens.home.auth
import cm.everafter.viewModels.UserViewModel
import coil.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


val db = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    navController: NavController,
    notificationService: NotificationService,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Free Games") },
                actions = {
                    Button(
                        onClick = {
                            notificationService.showBasicNotification()
                        }
                    ) {
                        Text(text = "Show basic notifications")
                    }
                }
                )
        }

    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),

            ) {
            var games by remember { mutableStateOf(emptyList<Game>()) }
            val gamesRef = db.getReference("Games")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val freeGamesToday = games.filter { game ->
                val startDate = dateFormat.parse(game.free_start_date)
                val endDate = dateFormat.parse(game.free_end_date)

                // Certifique-se de ter uma Date representando a data atual
                val currentDate = Calendar.getInstance().time
                // Verifica se a data de hoje está entre free_start_date e free_end_date
                currentDate in startDate..endDate
            }


            LaunchedEffect(gamesRef) {
                gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newGames =
                            snapshot.children.mapNotNull { it.getValue(Game::class.java) }
                        games = newGames
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Lidar com erro, se necessário
                    }
                })
            }

            LazyColumn (
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .padding(horizontal = 20.dp)
            ) {
                itemsIndexed(freeGamesToday) { index, game ->
                    viewModel.loggedInUser?.let { it1 -> GameItem(game = game, it1.relationship) }
                }

            }

        }
    }
}

@Composable
fun GameItem(game: Game, relationship: String) {

    var isFavorited by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val game = db.reference.child("Relationships").child(relationship).child("favgames").child(game.title).get().await()
        isFavorited = game.exists()
    }

    Card{
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    AsyncImage(
                        model = game.thumbnail,
                        contentDescription = "Translated description of what the image contains",
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = game.title,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = game.genre,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Adicione o ícone clicável para adicionar/remover dos favoritos
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorited) Color.Red else Color.Gray,
                        modifier = Modifier.clickable {
                            if (isFavorited) {
                                removeFromFavGames(relationship, game.title)
                            } else {
                                addToFavGames(relationship, game.title)
                            }
                            // Alterne o estado de favoritos
                            isFavorited = !isFavorited
                        }
                    )
                }

            }
        }
    }
}

// Adiciona um jogo à lista de jogos favoritos
fun addToFavGames(relationship: String, gameTitle: String) {
    // Adiciona o jogo usando a chave gerada
    db.reference.child("Relationships").child(relationship).child("favgames").child(gameTitle).setValue(gameTitle)
}

fun removeFromFavGames(relationship: String, gameTitle: String) {
    // Remove o jogo usando a chave
    db.reference.child("Relationships").child(relationship).child("favgames").child(gameTitle).removeValue()
}