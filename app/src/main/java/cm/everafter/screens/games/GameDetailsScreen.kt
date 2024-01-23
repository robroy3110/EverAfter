package cm.everafter.screens.games

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import cm.everafter.R
import cm.everafter.classes.Game
import cm.everafter.ui.theme.EverAfterTheme
import cm.everafter.viewModels.GameViewModel
import coil.compose.AsyncImage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    navController: NavController,
    viewModel: GameViewModel,
    gameId: String
) {
    // Obter o estado do gameDetails do viewModel
    val gameDetails by viewModel.gameDetails.collectAsState()

    DisposableEffect(gameId) {
        viewModel.getGameDetails(gameId)

        onDispose {
            // Cleanup logic if needed
        }
    }

    // Scaffold com TopAppBar e botão de retorno
    Scaffold(
        topBar = {
            gameDetails?.let { game ->
                TopAppBar(
                    title = {
                        Text(text = game.title)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(Icons.Filled.ArrowBackIos, contentDescription = "Go back")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        gameDetails?.let { game ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item {
                    // Exibição da imagem do jogo (se disponível)
                    if (game.thumbnail.isNotEmpty()) {
                        AsyncImage(
                            model = game.thumbnail,
                            contentDescription = "Translated description of what the image contains",
                            modifier = Modifier
                                .size(125.dp) // Defina o tamanho da imagem conforme necessário
                                .clip(shape = RoundedCornerShape(4.dp)) // Adiciona bordas arredondadas à imagem
                        )
                    }
                }

                item {
                    // Título do jogo
                    Text(
                        text = game.title,
                        style = typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    // Descrição curta do jogo
                    Text(
                        text = game.shortDescription,
                        style = typography.body1,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    // Outras informações do jogo
                    InformationSection(game = game)
                }
            }
        }
    }
}


@Composable
fun InformationSection(game: Game) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        InformationItem(icon = Icons.Filled.PlayArrow, label = "Gênero", value = game.genre)
        InformationItem(icon = Icons.Filled.Visibility, label = "Plataforma", value = game.platform)
        InformationItem(icon = Icons.Filled.Info, label = "Desenvolvedor", value = game.developer)
        InformationItem(icon = Icons.Filled.Warning, label = "Editora", value = game.publisher)
        InformationItem(icon = Icons.Filled.DateRange, label = "Data de Lançamento", value = game.releaseDate)

        // Adicione mais informações conforme necessário
    }
}

@Composable
fun InformationItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Ícone
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        // Rótulo e valor
        Column {
            Text(text = label, style = typography.body2)
            Text(text = value, style = typography.body1)
        }
    }
}
