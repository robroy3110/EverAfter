package cm.everafter.screens.playlist

import PlaylistImage
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.classes.Song
import cm.everafter.navigation.Screens
import cm.everafter.viewModels.PlaylistViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cm.everafter.NotificationService
import cm.everafter.classes.Perfil
import cm.everafter.classes.RelationShip
import cm.everafter.screens.home.auth
import cm.everafter.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

val db = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel,
    playlistName: String?,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    // Initialize Firebase Storage
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference

    // Currently playing song
    var currentlyPlayingSong by remember { mutableStateOf<Song?>(null) }

    // State to keep track of selected item index
    var selectedItemIndex by remember { mutableStateOf(-1) }

    // Observe changes to the playlistState
    val playlistState by playlistViewModel.playlistState.collectAsState()

    var otheruser by remember { mutableStateOf("") }
    var relationShip by remember { mutableStateOf<RelationShip?>(null) }

    val notificationService = NotificationService(LocalContext.current)
    var showToast by remember { mutableStateOf(false) }
    var pointsMusic by remember { mutableStateOf(0) }
    var pointsTotal by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {

        val relationshipRef =
            db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).get()
                .await()

        var musicPointsSnapshot  =  db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsMusic").get().await()
        var totalPointsSnapshot  = db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsTotal").get().await()

        if (relationshipRef.exists()) {
            relationShip = relationshipRef.getValue(RelationShip::class.java)
            otheruser = ""
            otheruser = if (relationShip?.user1!! != auth.currentUser!!.uid) {
                "1" + relationShip?.user1!!
            } else {
                "2" + relationShip?.user2!!
            }
        }

        // Verifique se o snapshot contém algum valor antes de tentar obter as crianças
        if (musicPointsSnapshot.exists()) {
            // Obtém a pontuação dos jogos como uma string
            val musicPointsString = musicPointsSnapshot.value.toString()
            // Converte a string para um inteiro (assumindo que a string representa um número)
            pointsMusic = musicPointsString.toIntOrNull() ?: 0
        } else {
            // Se não houver dados, defina a pontuação como 0 ou outro valor padrão
            pointsMusic = 0
        }

        // Verifique se o snapshot contém algum valor antes de tentar obter as crianças
        if (totalPointsSnapshot.exists()) {
            // Obtém a pontuação dos jogos como uma string
            val totalPointsString = totalPointsSnapshot.value.toString()
            // Converte a string para um inteiro (assumindo que a string representa um número)
            pointsTotal = totalPointsString.toIntOrNull() ?: 0
        } else {
            // Se não houver dados, defina a pontuação como 0 ou outro valor padrão
            pointsTotal = 0
        }
    }

    relationShip?.let {
        if (otheruser[0] == '1') {
            DisposableEffect(relationShip?.lastsongplayed1) {
                val relationRef = db.reference.child("Relationships")
                    .child(userViewModel.loggedInUser!!.relationship)
                val listener = object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val updatedRelation = snapshot.getValue(RelationShip::class.java)
                        relationShip = updatedRelation

                        if(relationShip!!.lastsongplayed1 != "") {
                            notificationService.showNewSongPlayedNotification(relationShip!!.lastsongplayed1)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Error observing user data: ${error.message}")
                    }
                }

                relationRef.addValueEventListener(listener)
                // Remove the listener when the composable is disposed
                onDispose {
                    relationRef.removeEventListener(listener)
                }

            }
        } else {
            DisposableEffect(relationShip?.lastsongplayed2) {
                val relationRef = db.reference.child("Relationships")
                    .child(userViewModel.loggedInUser!!.relationship)
                val listener = object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val updatedRelation = snapshot.getValue(RelationShip::class.java)
                        relationShip = updatedRelation

                        if(relationShip!!.lastsongplayed2 != "") {
                            notificationService.showNewSongPlayedNotification(relationShip!!.lastsongplayed2)
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Error observing user data: ${error.message}")
                    }
                }
                relationRef.addValueEventListener(listener)
                // Remove the listener when the composable is disposed
                onDispose {
                    relationRef.removeEventListener(listener)
                }

            }
        }
    }

    // Observar o estado para exibir o Toast dentro de um bloco @Composable
    if (showToast) {
        Toast.makeText(LocalContext.current, "You both won 28 for listening to the same song!", Toast.LENGTH_SHORT).show()
        // Resetar o estado para evitar a exibição repetida do Toast
        showToast = false
    }

    // Trigger the effect when playlistName changes
    LaunchedEffect(playlistName) {
        println("----------------- Playlist Details Screen -----------------")

        if (playlistName != null) {
            playlistViewModel.getPlaylist(playlistName)
        }
    }

    // Print the playlistState when it changes
    LaunchedEffect(playlistState) {
        println("Playlist State: $playlistState")
    }

    // Use remember to store the result of the effect
    val playlistDetails = remember(playlistState) { playlistState }

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
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = "Go Back",
                        tint = Color(0xFF8C52FF)
                    )
                }
            },
            actions = {
                // Delete IconButton
                IconButton(
                    onClick = {
                        // Handle delete playlist action
                        // You can show a confirmation dialog or directly perform the deletion
                        // For simplicity, I'm calling a function that you can implement
                        if (playlistName != null) {
                            playlistViewModel.deletePlaylist(playlistName)
                        }
                        // Navigate back after deletion or perform any other action
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete, // Replace with the appropriate delete icon
                        contentDescription = "Delete",
                        tint = Color(0xFF8C52FF)
                    )
                }
                // Edit IconButton
                IconButton(
                    onClick = {
                        navController.navigate("${Screens.EditPlaylistScreen.route}/${playlistName}")
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

        // Details of playlist's image, playlist name, and date
        playlistDetails?.let {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Add horizontal padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                // Playlist's Date
                val date = "${it.date}"
                Text(
                    text = date,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // Divider
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
                    // Navigate to the AddSongsScreen or perform any other action
                    navController.navigate("${Screens.AddSongsScreen.route}/${playlistName}")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF8C52FF),
                )
            }
        }

        LazyColumn {
            playlistDetails?.songs?.let { songs ->
                itemsIndexed(songs) { index, song ->
                    // Assuming you have a SongDetailsItem composable for displaying song details
                    SongDetailsItem(
                        storageRef=storageRef,
                        song = song,
                        onItemClick = {
                            // Handle item click and update the selected item index
                            selectedItemIndex = index
                        },
                        isPlaying = currentlyPlayingSong == song,
                        onPlayClick = {

                            if(otheruser[0] == '1') {
                                db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("lastsongplayed2").setValue(song.name)

                                if((relationShip!!.lastsongplayed1 == relationShip!!.lastsongplayed2) && (relationShip!!.lastsongplayed1 != "" && relationShip!!.lastsongplayed2 != "")) {
                                    showToast = true
                                    pointsTotal += 28
                                    pointsMusic += 28
                                    cm.everafter.screens.games.db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsMusic").setValue(pointsMusic)
                                    cm.everafter.screens.games.db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsTotal").setValue(pointsTotal)
                                }

                            } else {
                                db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("lastsongplayed1").setValue(song.name)

                                if((relationShip!!.lastsongplayed1 == relationShip!!.lastsongplayed2) && (relationShip!!.lastsongplayed1 != "" && relationShip!!.lastsongplayed2 != "")) {
                                    showToast = true
                                    pointsTotal += 28
                                    pointsMusic += 28
                                    cm.everafter.screens.games.db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsMusic").setValue(pointsMusic)
                                    cm.everafter.screens.games.db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsTotal").setValue(pointsTotal)
                                }
                            }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Additional content related to each song, if needed
                }
            }
        }
    }
}

@Composable
fun SongDetailsItem(
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