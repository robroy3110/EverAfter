package cm.everafter.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.R
import cm.everafter.Playlist
import cm.everafter.Song
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Initialize Firebase Database
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val playlistsRef = database.getReference("Playlists")

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
                showAddPlaylistDialog(onDismiss = { showDialog = false })
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        /* ------------------------------------- PLAYLISTS  ------------------------------------- */
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
        // Playlist from the database
        // Retrieve playlists from the database
        DisposableEffect(Unit) {
            val playlistsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newPlaylists = snapshot.children.mapNotNull { it.getValue(Playlist::class.java) }
                    playlists = newPlaylists
                }
                override fun onCancelled(error: DatabaseError) {}
            }

            playlistsRef.addValueEventListener(playlistsListener)

            onDispose {
                playlistsRef.removeEventListener(playlistsListener)
            }
        }

        // Display playlists in groups of three per row
        // Display playlists one below the other
        LazyColumn {
            items(playlists) { playlist ->
                // Display regular playlist item
                PlaylistItem(
                    playlist = playlist,
                    onEditClick = {
                        // Implement your logic when the edit button is clicked
                        // For example, you can show the edit dialog
                        // or navigate to another screen for editing
                    }
                )

                // Add spacing between playlists
                Spacer(modifier = Modifier.height(8.dp))
            }
        }



    }
}

@Composable
fun PlaylistItem(playlist: Playlist, onEditClick: () -> Unit) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // Display playlist image if available
        if (playlist.imageUri != null) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder image, replace with actual logic
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Display playlist name and edit button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = playlist.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(0.8f)
            )
            IconButton(
                onClick = onEditClick
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Edit Playlist",
                    tint = Color(0xFF8C52FF),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Show pop-up box when showDialog is true
        if (showDialog.value) {
            showPlaylistDetailsDialog(context, playlist, onDismiss = { showDialog.value = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <Context> showPlaylistDetailsDialog(context: Context, playlist: Playlist, onDismiss: () -> Unit) {
    var editedName by remember { mutableStateOf(playlist.name) }
    OutlinedTextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Name") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
    AlertDialog(
        onDismissRequest = {
            onDismiss.invoke()
        },
        title = {
            Text(text = "Playlist Details")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Display playlist details
                Text("Name: ${playlist.name}")
                Text("Description: ${playlist.description}")
                Text("Date: ${playlist.date}")
                Text("Location: ${playlist.location}")
                // Add more details as needed
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Save the edited name to Firebase or perform any other necessary action
                    val updatedPlaylist = playlist.copy(name = editedName)
                    saveEditedPlaylistToFirebase(updatedPlaylist)

                    onDismiss.invoke()
                }
            ) {
                Text(text = "Save")
            }
        },
    )
}

fun saveEditedPlaylistToFirebase(updatedPlaylist: Any) {

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun showAddPlaylistDialog(onDismiss: () -> Unit) {
    // TODO: Implement logic to handle user input and database interactions
    var expanded by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2023-12-01") }
    var location by remember { mutableStateOf("Current Location") }

    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = {
            onDismiss.invoke()
            // TODO: Handle dismissal
        },
        title = {
            Text(text = "Add New Playlist")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Adjusted padding as needed
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
            // ... (similar UI components for description, date, and location)

        },
        confirmButton = {
            Button(
                onClick = {
                    // Save the playlist to Firebase
                    val playlist = Playlist(
                        name = playlistName,
                        description = description,
                        date = date,
                        location = location
                    )
                    println("SAVED PLAYLIST SUPOSTAMENTE")
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
                    // TODO: Handle cancel button click
                    keyboardController?.hide()
                    onDismiss.invoke()
                }
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                Text(text = "Close")
            }
        },
        // TODO: Add content for the dialog
    )
}

private fun savePlaylistToFirebase(playlist: Playlist) {
    val database =Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val playlistsRef = database.getReference("Playlists")
    println("PLAYLISts TREF" + playlistsRef)
    // Generate a unique key for the playlist
    val playlistKey = playlistsRef.push().key ?: return

    // Save the playlist to the Firebase Realtime Database
    playlistsRef.child(playlistKey).setValue(playlist)
}

private fun saveEditedPlaylistToFirebase(playlist: Playlist) {
    val database = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
    val playlistsRef = database.getReference("Playlists")

    val query = playlistsRef.orderByChild("name").equalTo(playlist.name)

    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (childSnapshot in snapshot.children) {
                val playlistKey = childSnapshot.key
                if (playlistKey != null) {
                    // Update the playlist details
                    playlistsRef.child(playlistKey).setValue(playlist)
                    return
                }
            }

            // If no matching playlist is found, you can handle it accordingly
            // For example, you can log an error or show a message to the user.
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle the error if the query is canceled
        }
    })
}






