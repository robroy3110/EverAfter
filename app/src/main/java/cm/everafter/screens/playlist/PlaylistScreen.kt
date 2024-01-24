
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cm.everafter.R
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Our Library")
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                Spacer(modifier = Modifier.height(100.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    // Check if the user has a relationship before showing the add playlist button
                    if (!userViewModel.loggedInUser?.relationship.isNullOrEmpty()) {
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
                    // Show the message only if the user's relationship is null or empty
                    if (userViewModel.loggedInUser?.relationship.isNullOrEmpty()) {
                        // Display the "couple.png" image along with the message
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.couple), // Replace R.drawable.couple1 with your actual resource ID
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(150.dp)
                                        .padding(end = 8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Join a relationship to create a Shared Playlist!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Check if there are playlists associated with the user's relationship otherwise show special msg
                if (!userViewModel.loggedInUser?.relationship.isNullOrEmpty() && playlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Create your first Shared Playlist!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.couple1),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
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

                var context = LocalContext.current
                // Display playlists
                LazyColumn {
                    items(playlists) { playlist ->
                        // Display regular playlist item
                        PlaylistItem(
                            playlist = playlist,
                            storageRef = storageRef,
                            onPlaylistClick = { playlist ->
                                navController.navigate("${Screens.PlaylistDetailsScreen.route}/${playlist.name}")
                            },
                            onPlayButtonClick = { playlist ->
                                // Implement play functionality here
                                playlistViewModel.playPlaylist(playlist)
                                Toast.makeText(context, "You are currently listening to " + playlist.name + " playlist!", Toast.LENGTH_SHORT).show()
                            },
                            onStopButtonClick = {
                                // Implement stop functionality here
                                playlistViewModel.stopPlayback()
                            }
                        )
                        // Add spacing between playlists
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    )
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
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    storageRef: StorageReference,
    onPlaylistClick: (Playlist) -> Unit,
    onPlayButtonClick: (Playlist) -> Unit,
    onStopButtonClick: () -> Unit
) {
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

        // Display playlist name
        Text(
            text = playlist.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        // Play and stop buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play button
            IconButton(
                onClick = { onPlayButtonClick.invoke(playlist) }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color(0xFF8C52FF)
                )
            }

            // Stop button
            IconButton(
                onClick = { onStopButtonClick.invoke() }
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color(0xFF8C52FF)
                )
            }
        }
    }
}


@Composable
fun Column(modifier: Modifier, content: () -> Unit) {}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun showAddPlaylistDialog(userViewModel: UserViewModel, onDismiss: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDate()) }

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
            }
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

// Function to get the current date in the required format
private fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}

private fun savePlaylistToFirebase(playlist: Playlist) {
    val database =Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val playlistsRef = database.getReference("Playlists")

    // Generate a unique key for the playlist
    val playlistKey = playlistsRef.push().key ?: return

    // Save the playlist to the Firebase Realtime Database
    playlistsRef.child(playlistKey).setValue(playlist)
}


