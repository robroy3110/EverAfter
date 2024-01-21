import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cm.everafter.classes.Playlist
import cm.everafter.navigation.Screens
import cm.everafter.viewModels.PlaylistViewModel
import cm.everafter.viewModels.UserViewModel
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
fun PlayListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    // Inside your composable function
    val playlistViewModel: PlaylistViewModel = viewModel()

    // Initialize Firebase Database
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val playlistsRef = database.getReference("Playlists")

    // Initialize Firebase Storage
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference

    // State to hold playlists from the database
    var playlists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Top Section: 'Our Library' and Search Bar
        Text(
            text = "Our Library",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        // Divider line with the same color as the search bar
        Divider(
            color = Color(0xFF8C52FF), // Changed to the desired color
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {



            /* --------------------------------------- SEARCH BAR ---------------------------------------------------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Adjusted padding for reduced height
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Bar with search icon on the right and the same color as the background
                var searchText by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    leadingIcon = { },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF8C52FF) // Use the primary color as the icon color
                        )
                    },
                    placeholder = {
                        Text(text = "Search playlists in your library...")
                    },
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(start = 2.dp)
                        .height(53.dp) // Adjusted height for reduced height
                        .clip(RoundedCornerShape(12.dp)) // Adjust the corner radius as needed
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, color = Color(0xFF8C52FF), shape = RoundedCornerShape(12.dp)) // Changed outline color
                )
            }
            // Show the dialog when showDialog is true
            if (showDialog) {
                showAddPlaylistDialog(userViewModel = userViewModel, onDismiss = { showDialog = false })
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        /* ------------------------------------- ADDING PLAYLISTS  ------------------------------------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Playlists",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            // Add Playlist Button
            IconButton(
                onClick = {
                    // Open the dialog when the button is clicked
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Playlist",
                    tint = Color(0xFF8C52FF),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        /* ------------------------------------- PLAYLISTS OF DB ------------------------------------- */
        // Retrieve playlists from the database
        DisposableEffect(Unit) {
            val playlistsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newPlaylists = snapshot.children.mapNotNull { it.getValue(Playlist::class.java) }
                        .filter { it.relationship == userViewModel.loggedInUser?.relationship }
                    playlists = newPlaylists
                }

                override fun onCancelled(error: DatabaseError) {}
            }

            playlistsRef.addValueEventListener(playlistsListener)

            onDispose {
                playlistsRef.removeEventListener(playlistsListener)
            }
        }
        // Display playlists
        LazyColumn {
            items(playlists) { playlist ->
                // Display regular playlist item
                PlaylistItem(
                    playlist = playlist,
                    storageRef = storageRef,  // Pass the storage reference
                    onPlaylistClick = { playlist ->
                        navController.navigate("${Screens.PlaylistDetailsScreen.route}/${playlist.name}")
                    }
                )
                // Add spacing between playlists
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PlaylistImage(playlist: Playlist, storageRef: StorageReference) {
    // Display playlist image if available
    val megabytes: Long = 1024 * 1024
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(playlist) {
        try {
            val byteArray = storageRef.child("PlaylistsPics/${playlist.imageUri}")
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
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    }
}

@Composable
fun PlaylistItem(playlist: Playlist, storageRef: StorageReference, onPlaylistClick: (Playlist) -> Unit) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Gray.copy(alpha = 0.1f)) // Temporary background color for debugging
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPlaylistClick.invoke(playlist) }, // Handle click on the whole playlist item
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display playlist image if available
        PlaylistImage(playlist = playlist, storageRef = storageRef)

        // Display playlist name and edit button
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = playlist.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}


@Composable
fun Column(modifier: Modifier, content: () -> Unit) {

}

/*
fun saveEditedPlaylistToFirebase(updatedPlaylist: Any) {
    // TODO: saveEditedPlaylistToFirebase
}
*/


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun showAddPlaylistDialog(userViewModel: UserViewModel, onDismiss: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2023-12-01") }
    var location by remember { mutableStateOf("Current Location") }

    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = {
            onDismiss.invoke()
        },
        title = {
            Text(
                text = "Add New Playlist",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp) // Center text
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // UI for playlistName, description, date, and location
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("location") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
            // TODO: showAddPlaylistDialog add a better location and date picking approach
        },
        confirmButton = {
            Button(
                onClick = {
                    // Save the playlist to Firebase with the relationship from UserViewModel
                    val playlist = Playlist(
                        relationship = userViewModel.loggedInUser?.relationship ?: "",
                        name = playlistName,
                        description = description,
                        date = date,
                        location = location,
                        imageUri = "",
                        songs = emptyList()
                    )

                    savePlaylistToFirebase(playlist)

                    // Hide the keyboard and dismiss the dialog
                    keyboardController?.hide()
                    onDismiss.invoke()
                }
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                Text(text = "Save")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    keyboardController?.hide()
                    onDismiss.invoke()
                },
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                Text(text = "Close")
            }
        },
    )
}


private fun savePlaylistToFirebase(playlist: Playlist) {
    val database =Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val playlistsRef = database.getReference("Playlists")

    // Generate a unique key for the playlist
    val playlistKey = playlistsRef.push().key ?: return

    // Save the playlist to the Firebase Realtime Database
    playlistsRef.child(playlistKey).setValue(playlist)
}


