package cm.everafter.screens.home

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cm.everafter.navigation.Screens
import cm.everafter.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel // Inject the UserViewModel
) {
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference
    val context = LocalContext.current
    var imageBitMap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if(uri != null){
                Log.i("TESETE","ENTREUAGW${uri}")
                //var file = Uri.fromFile(File(uri.toString()))
                val riversRef = storageRef.child("ProfilePics/${auth.currentUser!!.uid}.jpg")
                var uploadTask = riversRef.putFile(uri)

// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->

                    db.reference.child("Users").child(auth.currentUser!!.uid).child("image").setValue(
                        "${auth.currentUser!!.uid}.jpg"
                    )
                    val contentResolver: ContentResolver =context.contentResolver

                    // Use ContentResolver to open the stream and decode the bitmap
                    val inputStream = contentResolver.openInputStream(uri)
                    imageBitMap = BitmapFactory.decodeStream(inputStream)
                }
            }
        }
    }

    if(viewModel.loggedInUser!!.image != ""){
        val ref = storageRef.child("ProfilePics/${viewModel.loggedInUser!!.image}")
        val megabytes: Long = 1024 * 1024
        LaunchedEffect(Unit) {
            try {
                val byteArray = ref.getBytes(megabytes).await()
                imageBitMap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            } catch (e: Exception) {
                // Handle failure
                e.printStackTrace()
            }
        }
    }else{
        val ref = storageRef.child("ProfilePics/default_profile_pic.jpg")
        val megabytes: Long = 1024 * 1024
        LaunchedEffect(Unit) {
            try {
                val byteArray = ref.getBytes(megabytes).await()
                imageBitMap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            } catch (e: Exception) {
                // Handle failure
                e.printStackTrace()
            }
        }
    }

    imageBitMap?.let {
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
                        Icon(imageVector = Icons.Filled.ArrowBackIos, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        navController.navigate(Screens.HomeScreen.route) }) {
                        Icon(imageVector = Icons.Filled.Logout, contentDescription = "Home")
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
                        bitmap = imageBitMap!!.asImageBitmap(), // Replace with your image resource
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
                        onClick = {
                            launcher.launch("image/*")
                        },
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