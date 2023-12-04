package cm.everafter.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cm.everafter.Perfil
import cm.everafter.R
import cm.everafter.navigation.Screens
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val auth = Firebase.auth
    var context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Substitua pelo ID da sua imagem
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        val email = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }
        val name = remember { mutableStateOf(TextFieldValue()) }
        val username = remember { mutableStateOf(TextFieldValue()) }



        OutlinedTextField(
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = email.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { email.value = it }
        )

        OutlinedTextField(
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            value = password.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { password.value = it }
        )
        OutlinedTextField(
            label = { Text("Your Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            value = name.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { name.value = it }
        )

        OutlinedTextField(
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            value = username.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { username.value = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                auth.createUserWithEmailAndPassword(
                    email.value.text.trim(),
                    password.value.text.trim()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Auth", "Success!")
                        db.reference.child("Users").child(auth.currentUser!!.uid).setValue(
                            Perfil(name.value.text, "", 0, "", "",username.value.text)
                        )
                        navController.navigate(Screens.HomeScreen.route)
                    } else {
                        Log.d("Auth", "Failed: ${task.exception}")
                        task.exception?.printStackTrace()
                    }
                }
            }
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString("Login Here."),
            onClick = {
                navController.navigate(Screens.LogInScreen.route)
            }
        )
    }
}