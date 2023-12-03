package cm.everafter.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.navigation.Screens
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.auth.auth
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult



val db = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
val auth = Firebase.auth

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    if (auth.currentUser == null) {
        navController.navigate(Screens.LogInScreen.route)
    } else {
        Column(
            modifier = modifier,
        ) {
            // Add the Profile button at the top right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { navController.navigate(Screens.ProfileScreen.route) },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                }
            }

            // Rest of HomeScreen content
        }
    }
}

