package cm.everafter.screens.games

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import cm.everafter.NotificationService
import cm.everafter.classes.Game
import cm.everafter.navigation.Screens
import cm.everafter.screens.home.auth
import cm.everafter.viewModels.UserViewModel
import coil.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


val db = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")

sealed class GamesView {
    object AllGamesView : GamesView()
    object FavoriteGamesView : GamesView()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    navController: NavController,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    var selectedView by remember { mutableStateOf<GamesView>(GamesView.AllGamesView) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Free Games") },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                selectedView = GamesView.AllGamesView
                            }
                        ) {
                            Icon(Icons.Default.List, contentDescription = "All Games")
                        }
                        Button(
                            onClick = {
                                selectedView = GamesView.FavoriteGamesView
                            }
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = "Favorites")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (selectedView) {
            is GamesView.AllGamesView -> {
                AllGamesView(navController, viewModel, modifier)
            }
            is GamesView.FavoriteGamesView -> {
                FavoriteGamesView(navController, viewModel, modifier)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGamesView(
    navController: NavController,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier,
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
                    .padding(top = 100.dp)
            ) {
                itemsIndexed(freeGamesToday) { index, game ->
                    viewModel.loggedInUser?.let {
                        GameItem(
                            game = game,
                            relationship = it.relationship,
                            onClick = {
                                // Navegar para a tela de detalhes do jogo quando o item for clicado
                                navController.navigate("${Screens.GameDetailsScreen.route}/${game.title}")
                            }
                        )
                    }
                }
            }

        }
}

@Composable
fun FavoriteGamesView(
    navController: NavController,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),

        ) {
        var games by remember { mutableStateOf<List<Game?>>(mutableListOf()) }

        LaunchedEffect(Unit) {
            games = getGames(viewModel.loggedInUser!!.relationship)
        }

        LazyColumn (
            modifier = modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            itemsIndexed(games) { index, game ->
                viewModel.loggedInUser?.let { it1 ->
                    if (game != null) {
                        GameItemFav(game = game, it1.relationship, LocalContext.current)
                    }
                }
            }

        }

    }

}


@Composable
fun GameItem(game: Game, relationship: String, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        var isFavorited by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val game = db.reference.child("Relationships").child(relationship).child("favgames")
                .child(game.title).get().await()
            isFavorited = game.exists()
        }

        var context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD9D9D9)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(16.dp))

                    AsyncImage(
                        model = game.thumbnail,
                        contentDescription = "Translated description of what the image contains",
                        modifier = Modifier
                            .size(85.dp) // Defina o tamanho da imagem conforme necessário
                            .clip(shape = RoundedCornerShape(4.dp)) // Adiciona bordas arredondadas à imagem
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Defina um width fixo para a Column que contém o título
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = game.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2, // Define o número máximo de linhas para o título
                            overflow = TextOverflow.Ellipsis // Adiciona reticências (...) quando o texto é cortado
                        )

                        Spacer(modifier = Modifier.height(4.dp)) // Adiciona espaçamento vertical

                        Text(
                            text = game.genre,
                            fontWeight = FontWeight.Normal
                        )
                    }


                    // Adicione o ícone clicável para adicionar/remover dos favoritos
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorited) Color(0xFF8C52FF) else Color.Gray,
                        modifier = Modifier.size(24.dp) // Define o tamanho do ícone
                            .clickable {
                                if (isFavorited) {
                                    Toast.makeText(context, game.title + " was removed from your downloaded games list!", Toast.LENGTH_SHORT).show()
                                    removeFromFavGames(relationship, game.title)
                                } else {
                                    Toast.makeText(context, game.title + " was added to your downloaded games list!", Toast.LENGTH_SHORT).show()
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

@Composable
fun GameItemFav(game: Game, relationship: String, context: Context) {

    var isFavorited by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var pointsGames by remember { mutableStateOf(0) }
    var pointsTotal by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val game = db.reference.child("Relationships").child(relationship).child("favgames").child(game.title).get().await()
        var gamePointsSnapshot  =  db.reference.child("Relationships").child(relationship).child("pointsGames").get().await()
        var totalPointsSnapshot  =  db.reference.child("Relationships").child(relationship).child("pointsTotal").get().await()

        isFavorited = game.exists()
        // Verifique se o snapshot contém algum valor antes de tentar obter as crianças
        if (gamePointsSnapshot.exists()) {
            // Obtém a pontuação dos jogos como uma string
            val gamePointsString = gamePointsSnapshot.value.toString()
            // Converte a string para um inteiro (assumindo que a string representa um número)
            pointsGames = gamePointsString.toIntOrNull() ?: 0
        } else {
            // Se não houver dados, defina a pontuação como 0 ou outro valor padrão
            pointsGames = 0
        }

        // Verifique se o snapshot contém algum valor antes de tentar obter as crianças
        if (totalPointsSnapshot.exists()) {
            // Obtém a pontuação dos jogos como uma string
            val totalPointsString = totalPointsSnapshot.value.toString()
            // Converte a string para um inteiro (assumindo que a string representa um número)
            pointsTotal = totalPointsString.toIntOrNull() ?: 0
        } else {
            // Se não houver dados, defina a pontuação como 0 ou outro valor padrão
            pointsTotal = 0
        }

    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD9D9D9)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Adicione o CheckBox à esquerda da AsyncImage
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { newCheckedState ->
                        isChecked = newCheckedState
                        // Adicione a lógica aqui para a ação desejada ao marcar/desmarcar
                        if (isChecked) {

                            pointsTotal += 20
                            // Por exemplo: adicionar 20 aos pointsGames
                            pointsGames += 20
                            // Atualizar a pontuação no banco de dados, se necessário
                            db.reference.child("Relationships").child(relationship).child("pointsGames").setValue(pointsGames)
                            db.reference.child("Relationships").child(relationship).child("pointsGames").setValue(pointsTotal)

                            // Mostrar um Toast informando sobre a ação
                            Toast.makeText(context, "You both won 20 for playing this game today", Toast.LENGTH_SHORT).show()
                        } else {

                            pointsTotal -= 20
                            // Por exemplo: subtrair 20 dos pointsGames (opcional)
                            pointsGames -= 20
                            // Atualizar a pontuação no banco de dados, se necessário
                            db.reference.child("Relationships").child(relationship).child("pointsGames").setValue(pointsGames)
                            db.reference.child("Relationships").child(relationship).child("pointsGames").setValue(pointsTotal)

                            // Mostrar um Toast informando sobre a ação
                            Toast.makeText(context, "Undo", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp)
                )


                AsyncImage(
                    model = game.thumbnail,
                    contentDescription = "Translated description of what the image contains",
                    modifier = Modifier
                        .size(85.dp) // Defina o tamanho da imagem conforme necessário
                        .clip(shape = RoundedCornerShape(4.dp)) // Adiciona bordas arredondadas à imagem
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Defina um width fixo para a Column que contém o título
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = game.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2, // Define o número máximo de linhas para o título
                        overflow = TextOverflow.Ellipsis // Adiciona reticências (...) quando o texto é cortado
                    )

                    Spacer(modifier = Modifier.height(4.dp)) // Adiciona espaçamento vertical

                    Text(
                        text = game.genre,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Adicione o ícone clicável para adicionar/remover dos favoritos
                Icon(
                    imageVector = if (isFavorited) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorited) Color(0xFF8C52FF) else Color.Gray,
                    modifier = Modifier.size(24.dp) // Define o tamanho do ícone
                        .clickable {
                            if (isFavorited) {
                                Toast.makeText(context, game.title + " was removed from your downloaded games list!", Toast.LENGTH_SHORT).show()
                                removeFromFavGames(relationship, game.title)
                            } else {
                                Toast.makeText(context, game.title + " was added to your downloaded games list!", Toast.LENGTH_SHORT).show()
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


// Adiciona um jogo à lista de jogos favoritos
fun addToFavGames(relationship: String, gameTitle: String) {
    // Adiciona o jogo usando a chave gerada
    db.reference.child("Relationships").child(relationship).child("favgames").child(gameTitle).setValue(gameTitle)
}

fun removeFromFavGames(relationship: String, gameTitle: String) {
    // Remove o jogo usando a chave
    db.reference.child("Relationships").child(relationship).child("favgames").child(gameTitle).removeValue()
}

suspend fun getGames(relationship: String): List<Game?> {
    var gamesTemp = db.reference.child("Relationships").child(relationship).child("favgames").get().await()
    var games = mutableListOf<Game?>()

    if (gamesTemp.exists()) {
        for (game in gamesTemp.children) {
            var gameTemp = db.reference.child("FreeGames").child(game.value.toString()).get().await()
            if (gameTemp.exists()) {
                val final = gameTemp.getValue(Game::class.java)
                games.add(final)
            }
        }
    }

    return games
}