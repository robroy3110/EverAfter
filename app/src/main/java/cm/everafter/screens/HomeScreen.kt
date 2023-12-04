package cm.everafter.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cm.everafter.Perfil
import cm.everafter.R
import cm.everafter.navigation.Screens
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.auth.auth
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot


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
        lateinit var user : Perfil
        var load = false
        db.reference.child("Users").child(auth.currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                var dataSnapshot: DataSnapshot = it.result;
                if (dataSnapshot.exists()) {
                    Log.e("AUTENTICAÇAO", "${dataSnapshot.value}")
                    user = dataSnapshot.getValue(Perfil::class.java)!!
                    Log.e("AUTENTICALAI","$user")
                    load = true
                }
            } else {
                // Ocorreu um erro ao tentar obter os dados
                var error = it.exception;
                Log.e("Firebase", "Erro: " + error?.message);
            }
        }


      Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {
        // Top Section: 'This Is Us' and Profile Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This Is Us",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )

            Button(
                onClick = { auth.signOut()
                    navController.navigate(Screens.LogInScreen.route)

                    /*navController.navigate(Screens.ProfileScreen.route)*/ },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 8.dp)
                    .background(Color.Transparent)

            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.Black, // Black icon color
                    modifier = Modifier.size(32.dp) // Adjusted size for a bit bigger icon
                )
            }
            var teste = auth.currentUser?.uid

            when (load){
                true ->  Text("Logged In ${user.name} and ${user.username}")
                false -> Text("Not loaded yet")
            }


        }

        // Divider line
        Divider(
            color = Color(0xFF8C52FF), // Changed to the desired color
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Couple pics Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.width(16.dp)) // Adjust spacing between images

            // Heart icon
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "heart",
                tint = Color.Red, // Heart icon color
                modifier = Modifier.size(32.dp) // Adjusted size for a bit bigger icon
            )

            Spacer(modifier = Modifier.width(16.dp)) // Adjust spacing between images

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
        Text(
            text = "Date they met",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp) // Adjusted padding as needed
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = Color(0xFFD9D9D9), // Color D9D9D9
                    shape = RoundedCornerShape(12.dp) // Adjust the corner radius as needed
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "How long they are together",
                modifier = Modifier.wrapContentSize()
            )
        }
        Spacer(modifier = Modifier.height(10.dp))


        Text(
            text = "Daily Quests",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))


        // Daily Quests
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = Color(0xFFD9D9D9), // Color D9D9D9
                    shape = RoundedCornerShape(12.dp) // Adjust the corner radius as needed
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Nested Column for the top row
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quest Name
                    Text(
                        text = "Daily Quest 1",
                        modifier = Modifier.weight(1f)
                    )

                    // Points and Heart Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "100",
                            modifier = Modifier.padding(end = 4.dp) // Adjusted padding as needed
                        )
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "heart",
                            tint = Color.Red, // Heart icon color
                            modifier = Modifier.size(16.dp) // Adjusted size for the heart icon
                        )
                    }
                }
// Bottom Row
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, // Align items to the start and end
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Progress Bar (Replace with actual progress bar)
                        // ...

                        Spacer(modifier = Modifier.width(8.dp)) // Adjusted spacing between progress bar and points achieved

                        // Points Achieved
                        Text(
                            text = "50/100",
                            modifier = Modifier.wrapContentSize() // No need for weight when using SpaceBetween
                        )
                    }
                }


                Spacer(modifier = Modifier.height(8.dp)) // Adjusted spacing between rows
            }


        }







        // ... Rest of your content
    }

    }
}

