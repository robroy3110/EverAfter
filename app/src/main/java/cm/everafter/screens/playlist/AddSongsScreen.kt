package cm.everafter.screens.playlist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.R
import cm.everafter.classes.Song
import cm.everafter.viewModels.PlaylistViewModel
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongsScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel,
    modifier: Modifier = Modifier
) {
    // Initialize Firebase Storage
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference

    // Fetch the list of songs from the repository
    val availableSongs = remember { mutableStateOf<List<Song>>(emptyList()) }

    getAvailableSongs { songs ->
        availableSongs.value = songs
    }

    // Currently playing song
    var currentlyPlayingSong by remember { mutableStateOf<Song?>(null) }

    // State to keep track of selected item index
    var selectedItemIndex by remember { mutableStateOf(-1) }

    // Content of the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TopBar with back and edit buttons
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
        LazyColumn(state = rememberLazyListState()) {
            itemsIndexed(availableSongs.value) { index, song ->
                SongItem(
                    storageRef = storageRef,
                    song = song,
                    onItemClick = {
                        // Handle item click and update the selected item index
                        selectedItemIndex = index
                    },
                    isPlaying = currentlyPlayingSong == song,
                    onPlayClick = {
                        // Start playing the song
                        playlistViewModel.playSong(song)
                        currentlyPlayingSong = song
                    },
                    onStopClick = {
                        // Stop playing the song
                        playlistViewModel.stopPlayback()
                        currentlyPlayingSong = null
                    },
                    isClicked = index == selectedItemIndex
                )
/*                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )*/
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
fun SongImage(song: Song, storageRef: StorageReference) {
    // Display song image if available
    val megabytes: Long = 1024 * 1024
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(song) {
        try {
            val byteArray = storageRef.child("SongsPics/${song.imageFileName}")
                .getBytes(megabytes)
                .await()
            imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            // Handle failure
            e.printStackTrace()
        }
    }

    // Display the loaded image
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    } else {
        // Display a default image if imageUrl is null or blank
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground), // Replace with your default image resource
            contentDescription = "Default Image",
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    }
}

@Composable
fun SongItem(
    storageRef: StorageReference,
    song: Song,
    onItemClick: (Song) -> Unit,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onStopClick: () -> Unit,
    isClicked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick.invoke(song) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Display song image
        SongImage(song = song, storageRef = storageRef)

        Spacer(modifier = Modifier.width(8.dp))

        // Column for song name and artist
        Column {
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

        // Play Icon (conditionally displayed)
        if (isClicked) {
            IconButton(
                onClick = { onPlayClick() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = if (isPlaying) Color(0xFF8C52FF) else Color.Gray
                )
            }
        }
        // Stop Icon (conditionally displayed)
        if (isClicked) {
            IconButton(
                onClick = { onStopClick() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = if (isPlaying) Color(0xFF8C52FF) else Color.Gray
                )
            }
        }
    }
}