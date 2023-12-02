package cm.everafter.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cm.everafter.navigation.Screens
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIn(
    navController : NavController,
    modifier: Modifier = Modifier
) {
    val auth = Firebase.auth
    var context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
    ){
        val email = remember{ mutableStateOf(TextFieldValue())}
        val password = remember{ mutableStateOf(TextFieldValue())}

        OutlinedTextField(
            label = {
                Text("Email")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = email.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                email.value = it
            }
        )
        OutlinedTextField(
            label = {
                Text("Password")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            value = password.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                password.value = it
            }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(modifier = Modifier.fillMaxWidth(),
            onClick = {
                auth.signInWithEmailAndPassword(
                    email.value.text.trim(),
                    password.value.text.trim()
                ).addOnCompleteListener{ task->
                    if(task.isSuccessful){
                        Log.d("Auth","Success!")
                    }else{
                        Log.d("Auth","Failed: ${task.exception}")
                    }
                }
            }){
            Text(text= "Log In")
        }
        ClickableText(
            text = AnnotatedString("Log Iñ Here.") ,
            onClick = {
                navController.navigate(Screens.RegisterScreen.route)
            })
    }




}


