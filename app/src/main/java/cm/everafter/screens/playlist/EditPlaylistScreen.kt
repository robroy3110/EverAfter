package cm.everafter.screens.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.R
import cm.everafter.classes.Song
import cm.everafter.navigation.Screens
import cm.everafter.viewModels.PlaylistViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cm.everafter.classes.Playlist
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaylistScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel,
    playlistName: String?,
    modifier: Modifier = Modifier
) {
    // Trigger the effect when playlistName changes
    LaunchedEffect(playlistName) {
        println("----------------- EditPlaylist Screen -----------------")

        if (playlistName != null) {
            playlistViewModel.getPlaylist(playlistName)
        }
    }

    // Observe changes to the playlistState
    val playlistState by playlistViewModel.playlistState.collectAsState()

    // Print the playlistState when it changes
    LaunchedEffect(playlistState) {
        println("Playlist State: $playlistState")
    }

    // Use remember to store the result of the effect
    val playlistDetails = remember(playlistState) {
        playlistState
    }

    // Content of the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AppBar with back and edit buttons
        TopAppBar(
            title = { /* You can add a title here if needed */ },
            navigationIcon = {
                IconButton(
                    onClick = {
                        // Navigate back to the previous screen
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        tint = Color(0xFF8C52FF)
                    )
                }
            },
            actions = {
                // Edit IconButton
                IconButton(
                    onClick = {
                        // Handle edit button click
                        // You can navigate to the edit screen or perform any other action
                        // For example: navController.navigate("edit_screen_route")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF8C52FF)
                    )
                }
            },
        )

        // Center only the image, playlist name, location, and date
        playlistDetails?.let {

            // Playlist name
            if (playlistName != null) {
                Text(
                    text = playlistName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Playlist Location and Date
            val locationAndDate = "${it.location}, ${it.date}"
            Text(
                text = locationAndDate,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // TODO: load playlist image from storage and show on screen



        }

        // Smooth Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF8C52FF))
        )
        // Section: Songs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text "Songs"
            Text(
                text = "Songs",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(0.8f)
            )

            // Add Button
            IconButton(
                onClick = {
                    // Handle add button click
                    // Navigate to the AddSongsScreen or perform any other action
                    navController.navigate(Screens.AddSongsScreen.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF8C52FF),
                )
            }
        }

        // Playlist's List of Songs
        LazyColumn {
            playlistDetails?.songs?.let { songs ->
                items(songs) { song ->
                    SongItemOnEditScreen(song = song)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun SongItemOnEditScreen(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(8.dp))
        Text(text = song.name, fontSize = 16.sp)
    }
}

