package cm.everafter.screens.games

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import cm.everafter.R
import cm.everafter.classes.Game
import cm.everafter.screens.home.calcularDiferencaDias
import cm.everafter.ui.theme.EverAfterTheme
import cm.everafter.viewModels.GameViewModel
import coil.compose.AsyncImage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    navController: NavController,
    viewModel: GameViewModel,
    gameId: String
) {
    // Obter o estado do gameDetails do viewModel
    val gameDetails by viewModel.gameDetails.collectAsState()
    val context = LocalContext.current

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
                        Text(text = game.title, fontWeight = FontWeight.Bold)
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = game.thumbnail,
                                contentDescription = "Translated description of what the image contains",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Defina a altura da imagem conforme necessário
                                    .clip(shape = RoundedCornerShape(16.dp)) // Adiciona bordas arredondadas à imagem
                                    .background(MaterialTheme.colorScheme.primary)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                item {
                    // Data de início do jogo grátis e botão "Claim"
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val freeEndDate = dateFormat.parse(game.free_end_date) ?: Date() // Altere conforme necessário

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Free until ${dateFormat.format(freeEndDate)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Button(onClick = {
                            // Ação de reivindicar
                            val url = game.game_url

                            // Cria uma Intent para abrir o navegador
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                            // Inicia a Intent
                            context.startActivity(intent)
                        }) {
                            Text(text = "Claim", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }

                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                            .background(
                                color = Color(0xFFD9D9D9), // Color D9D9D9
                                shape = RoundedCornerShape(12.dp) // Adjust the corner radius as needed
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.short_description,
                            modifier = Modifier.wrapContentSize(),
                            fontSize = 18.sp
                        )
                    }
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
            .padding(start = 16.dp)
    ) {

        InformationItem(label = "Genre", value = game.genre)
        InformationItem(label = "Platform", value = game.platform)
        InformationItem(label = "Developer", value = game.developer)
        InformationItem(label = "Publisher", value = game.publisher)
        InformationItem(label = "Release Date", value = game.release_date)

        // Adicione mais informações conforme necessário
    }
}

@Composable
fun InformationItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Ajuste o padding conforme necessário
    ) {
        // Rótulo e valor
        Column(
            modifier = Modifier.padding(start = 16.dp) // Adicione padding à Column
        ) {
            Text(
                text = label,
                style = typography.h6, // Ajuste o tamanho da fonte conforme necessário
                modifier = Modifier.padding(bottom = 4.dp) // Adicione padding inferior à label
            )
            Text(
                text = value,
                style = typography.body1,
                modifier = Modifier.padding(bottom = 4.dp) // Adicione padding inferior ao value
            )
        }
    }
}

