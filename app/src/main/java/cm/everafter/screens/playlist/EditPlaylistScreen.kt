
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.classes.Playlist
import cm.everafter.classes.Song
import cm.everafter.screens.playlist.SongImage
import cm.everafter.viewModels.PlaylistViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

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
    // Initialize state for handling image selection
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }


    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            playlistName?.let { name ->
                playlistViewModel.uploadImageToStorage(name, it)
            }
        }
    }
    // Launch the gallery for image selection
    fun chooseImage() {
        getContent.launch("image/*")
    }

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
        // AppBar with back button
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
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = "Go Back",
                        tint = Color(0xFF8C52FF)
                    )
                }
            },

        )

        // ---------------------- image, playlist name, and date------------------------------
        playlistDetails?.let {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Add horizontal padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display the playlist image
                ClickablePlaylistImage(
                    playlist = playlistDetails,
                    storageRef = storageRef,
                    selectedImageUri = selectedImageUri,
                    onImageClick = {
                        chooseImage()
                    }
                )

                // Playlist name
                EditablePlaylistName(
                    playlistName = playlistName,
                    onNameClick = {
                        // Handle playlist name click (you can add the logic to edit the name)
                    },
                    onSaveClick = { newPlaylistName ->
                        // Handle saving the new playlist name to the database
                        if (playlistName != null) {
                            playlistViewModel.updatePlaylistName(playlistName, newPlaylistName)
                        }
                    }
                )

                // Playlist Location and Date
                val date = " ${it.date}"
                Text(
                    text = date,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // Smooth Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF8C52FF))
        )
        //---------------------------- Songs ----------------------------------
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

                    DeletableSongItem(
                        storageRef = storageRef,
                        song = song,
                        onItemClick = {
                            // Handle item click and update the selected item index
                            selectedItemIndex = index
                        },
                        onDeleteClick = {
                            // Call the delete function in the ViewModel
                            playlistViewModel.deleteSongFromPlaylist(playlistName ?: "", song)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}

@Composable
fun DeletableSongItem(
    storageRef: StorageReference,
    song: Song,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isDeleteDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: Edit Playlist's image by getting it from the Phone's gallery
        // TODO: Default playlist image as the first added song only works for a few songs
        // TODO: Remove Blank Space On top of Screens
        // Display song image
        SongImage(song = song, storageRef = storageRef)

        // Column for song name and artist
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f)
        ) {
            // TODO: Being able to edit Playlist's name
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

        // Spacer to push the delete icon to the right side
        Spacer(modifier = Modifier.weight(1f))

        // Delete Icon wrapped in a Box to ensure proper alignment
        Box(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            IconButton(
                onClick = {
                    // Show the delete confirmation dialog or directly delete the song
                    isDeleteDialogVisible = true
                },
                modifier = Modifier
                    .padding(end = 8.dp)
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

    // Delete confirmation dialog
    if (isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                isDeleteDialogVisible = false
            },
            title = {
                Text(text = "Delete Song")
            },
            text = {
                Text(text = "Are you sure you want to delete this song from the playlist?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Call the delete function in the ViewModel
                        onDeleteClick.invoke()
                        isDeleteDialogVisible = false
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isDeleteDialogVisible = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun ClickablePlaylistImage(
    playlist: Playlist,
    storageRef: StorageReference,
    selectedImageUri: Uri?,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onImageClick()
            }
            .wrapContentSize(Alignment.Center)
    ) {
        // Display the playlist image
        PlaylistImage(playlist = playlist, storageRef = storageRef)

        // Show the "Add Image" button if no image is present
        if (selectedImageUri == null && playlist.imageUri.isEmpty()) {
            IconButton(
                onClick = {
                    onImageClick()
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Add Image",
                    tint = Color(0xFF8C52FF)
                )
            }
        }
    }
}

@Composable
fun EditablePlaylistName(
    playlistName: String?,
    onNameClick: () -> Unit,
    onSaveClick: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf(playlistName ?: "") }

    if (isEditing) {
        // Editable text field when in editing mode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = newPlaylistName,
                onValueChange = { newPlaylistName = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isEditing = false
                        onSaveClick(newPlaylistName)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Confirm button to save the changes
            Button(
                onClick = {
                    isEditing = false
                    onSaveClick(newPlaylistName)
                }
            ) {
                Text(text = "Save")
            }
        }
    } else {
        // Clickable text when not in editing mode
        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(newPlaylistName)
                }
            },
            onClick = {
                onNameClick()
                isEditing = true
            },
            modifier = Modifier.padding(vertical = 8.dp),
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
    }
}





