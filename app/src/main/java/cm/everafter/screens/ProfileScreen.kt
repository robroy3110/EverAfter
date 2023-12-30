package cm.everafter.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cm.everafter.Perfil
import cm.everafter.R // Replace with the actual resource file for your image placeholder
import cm.everafter.navigation.Screens
import cm.everafter.ui.theme.EverAfterTheme
import cm.everafter.viewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel // Inject the UserViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(text = "Profile", fontWeight = FontWeight.Bold)
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    auth.signOut()
                    navController.navigate(Screens.HomeScreen.route) }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Settings")
                }
            }
        )

        // User Profile Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Profile Picture (Replace with actual user picture)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your image resource
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Details Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {


            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically  // Center vertically
            ) {


                Spacer(modifier = Modifier.width(10.dp))

                // Edit Profile Button
                Button(
                    onClick = { /* Handle edit profile click */ },
                    modifier = Modifier
                        .wrapContentSize()  // Make the button wrap its content
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Edit")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder for additional user details
            // Add more details as needed (e.g., email, bio, etc.)

            Spacer(modifier = Modifier.height(8.dp))
            viewModel.loggedInUser?.let {
                Text(
                    text = viewModel.loggedInUser!!.username, // Replace with actual user name
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,  // Center the text
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}