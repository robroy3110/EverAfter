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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.R
import cm.everafter.ui.theme.EverAfterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
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
                        Text(text = "Search songs or playlists...")
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

        // Playlist Rows
        LazyColumn {
            items(playlists.chunked(3)) { rowPlaylists ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    rowPlaylists.forEach { playlist ->
                        PlaylistItem(playlist = playlist)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun showAddPlaylistDialog(onDismiss: () -> Unit) {
    // TODO: Implement logic to handle user input and database interactions
    var expanded by remember { mutableStateOf(false) }
    var personalPlaylist by remember { mutableStateOf(true) }
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
                // UI for selecting personal/shared
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Type:")
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                personalPlaylist = true
                                expanded = false
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            text = {
                                Text("Personal")
                            }
                        )

                        DropdownMenuItem(
                            onClick = {
                                personalPlaylist = false
                                expanded = false
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            text = {
                                Text("Shared")
                            }
                        )
                    }
                }

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
                    // TODO: Handle save button click
                    // You can use the entered values (personalPlaylist, playlistName, description, date, location)
                    // for saving the new playlist
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




@Composable
fun PlaylistItem(playlist: Playlist) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { /* Handle click on playlist */ }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = playlist.imageResId),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)

                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = playlist.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

// will be removed
data class Playlist(val name: String, val imageResId: Int)

// Placeholder playlists
val playlists = listOf(
    Playlist(name = "Playlist 1", imageResId = R.drawable.ic_launcher_foreground),
    Playlist(name = "Playlist 2", imageResId = R.drawable.ic_launcher_foreground),
    Playlist(name = "Playlist 3", imageResId = R.drawable.ic_launcher_foreground),
    // ... Add more playlists as needed
)