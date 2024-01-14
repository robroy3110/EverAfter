package cm.everafter.screens.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.classes.Song
import cm.everafter.navigation.Screens
import cm.everafter.viewModels.PlaylistViewModel
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongsScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel,
    modifier: Modifier = Modifier
) {
    // Fetch the list of songs from the repository
    val availableSongs = remember { mutableStateOf<List<Song>>(emptyList()) }

    getAvailableSongs { songs ->
        availableSongs.value = songs
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
        )
        // Display the list of available songs
        LazyColumn {
            items(availableSongs.value) { song ->
                SongItem(song = song, onItemClick = {
                    // Handle song item click
                    playlistViewModel.playSong(song)
                })
                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

fun getAvailableSongs(onSongsLoaded: (List<Song>) -> Unit) {
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val songsRef = database.getReference("Songs")

    // Placeholder list to store fetched songs
    val songsList = mutableListOf<Song>()

    // Fetch songs from Firebase Realtime Database
    songsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            songsList.clear()

            // Iterate through the dataSnapshot to get songs
            for (songSnapshot in snapshot.children) {
                val song = songSnapshot.getValue(Song::class.java)
                song?.let {
                    songsList.add(it)
                }
            }
            // Notify the caller that songs are loaded
            onSongsLoaded(songsList)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle the error
        }
    })
}
@Composable
fun SongItem(song: Song, onItemClick: (Song) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick.invoke(song) }, // Pass the clicked song to onItemClick
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Load and display the image
        Image(
            painter = rememberImagePainter(data = song.imageUrl),
            contentDescription = "Song Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = song.name, fontSize = 16.sp)
    }
}

