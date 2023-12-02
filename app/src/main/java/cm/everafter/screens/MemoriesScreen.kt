package cm.everafter.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MemoriesScreen(
    navController : NavController,
    modifier: Modifier = Modifier
) {
    Column(
    modifier = modifier.fillMaxSize().padding(20.dp),
){

    Text(text= "Register")

}
}