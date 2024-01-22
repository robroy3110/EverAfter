import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.classes.Playlist
import cm.everafter.classes.Song
import cm.everafter.navigation.Screens
import cm.everafter.screens.playlist.SongDetailsItem
import cm.everafter.screens.playlist.SongImage
import cm.everafter.screens.playlist.SongItem
import cm.everafter.viewModels.PlaylistViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaylistScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel,
    playlistName: String?,
    modifier: Modifier = Modifier
) {
    // Initialize Firebase Storage
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference

    // State to keep track of selected item index
    var selectedItemIndex by remember { mutableStateOf(-1) }

    // Trigger the effect when playlistName changes
    LaunchedEffect(playlistName) {
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
    val playlistDetails = remember(playlistState) { playlistState }

    // Content of the screen
    androidx.compose.foundation.layout.Column(
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

        )

        // Center only the image, playlist name, location, and date
        playlistDetails?.let {
            // Display the playlist image
            PlaylistImage(playlist = playlistDetails, storageRef = storageRef)
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
        }

        // Playlist's List of Songs
        LazyColumn {
            playlistDetails?.songs?.let { songs ->
                itemsIndexed(songs) { index, song ->
                    // Assuming you have a SongDetailsItem composable for displaying song details
                    DeletableSongItem(
                        storageRef = storageRef,
                        song = song,
                        onItemClick = {
                            // Handle item click and update the selected item index
                            selectedItemIndex = index
                            //TODO: call Delete Song From playlist function
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Additional content related to each song, if needed
                }
            }
        }

    }
}

@Composable
fun DeletableSongItem(
    storageRef: StorageReference,
    song: Song,
    onItemClick: () -> Unit
) {
    var isDeleteDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display song image
        SongImage(song = song, storageRef = storageRef)

        Spacer(modifier = Modifier.width(8.dp))

        // Column for song name and artist
        Box(
            modifier = Modifier.weight(1f)
        ) {
            // Column for song name and artist
            androidx.compose.foundation.layout.Column {
                // Song name
                Text(text = song.name, fontSize = 16.sp)

                // Song artist
                Text(
                    text = song.artist,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Delete Icon
        IconButton(
            onClick = {  },
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFF8C52FF)
            )
        }

    }
}



