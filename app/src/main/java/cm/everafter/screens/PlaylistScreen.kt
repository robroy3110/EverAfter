package cm.everafter.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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

// Search bar with reduced height, more rounded corners, and changed outline color
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp), // Adjusted padding for reduced height
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar with search icon on the right and the same color as the background
            OutlinedTextField(
                value = "",
                onValueChange = { },
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
                    .weight(0.7f)
                    .padding(start = 11.dp)
                    .height(36.dp) // Adjusted height for reduced height
                    .clip(RoundedCornerShape(12.dp)) // Adjust the corner radius as needed
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, color = Color(0xFF8C52FF), shape = RoundedCornerShape(12.dp)) // Changed outline color
            )
        }


        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "Playlists",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

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

data class Playlist(val name: String, val imageResId: Int)

// Placeholder playlists
val playlists = listOf(
    Playlist(name = "Playlist 1", imageResId = R.drawable.ic_launcher_foreground),
    Playlist(name = "Playlist 2", imageResId = R.drawable.ic_launcher_foreground),
    Playlist(name = "Playlist 3", imageResId = R.drawable.ic_launcher_foreground),
    // ... Add more playlists as needed
)