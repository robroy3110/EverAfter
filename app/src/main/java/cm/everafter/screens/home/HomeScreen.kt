package cm.everafter.screens.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import cm.everafter.classes.Perfil
import cm.everafter.R
import cm.everafter.classes.RelationShip
import cm.everafter.navigation.Screens
import cm.everafter.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import cm.everafter.classes.User
import java.util.stream.IntStream.range


val db = Firebase.database("https://everafter-382e1-default-rtdb.europe-west1.firebasedatabase.app/")
val auth = Firebase.auth
val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
val storageRef = storage.reference


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    if (auth.currentUser == null) {
        navController.navigate(Screens.LogInScreen.route)
    } else {

        ResultScreen(modifier = modifier.fillMaxWidth(), navController,viewModel)
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen( modifier: Modifier, navController: NavController,viewModel: UserViewModel) {

    var user by remember { mutableStateOf<Perfil?>(null) }
    var userImageBitMap by remember { mutableStateOf<Bitmap?>(null) }
    var notificationsUsers by remember { mutableStateOf<List<User?>?>(mutableListOf())}
    var otherUser by remember { mutableStateOf<Perfil?>(null) }
    var otherUserImageBitMap by remember { mutableStateOf<Bitmap?>(null)}

    var relationShip by remember {mutableStateOf<RelationShip?>(null)}


    LaunchedEffect(Unit) {
        val data = getUserDataFromFirebase(auth.currentUser!!.uid)
        user = data

    }

    DisposableEffect(user?.relationship) {
        // Observe changes in the relationship property of the user
        val userRef = db.reference.child("Users").child(auth.currentUser!!.uid)
        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedUser = snapshot.getValue(Perfil::class.java)
                user = updatedUser
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error observing user data: ${error.message}")
            }
        }

        userRef.addValueEventListener(listener)
        // Remove the listener when the composable is disposed
        onDispose {
            userRef.removeEventListener(listener)
        }
    }


    user?.let { thisUser ->

        viewModel.loggedInUser = thisUser

        if(thisUser.image != ""){
            val ref = storageRef.child("ProfilePics/${thisUser.image}")
            val megabytes: Long = 1024 * 1024
            LaunchedEffect(Unit) {
                try {
                    val byteArray = ref.getBytes(megabytes).await()
                    userImageBitMap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
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
                    userImageBitMap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                } catch (e: Exception) {
                    // Handle failure
                    e.printStackTrace()
                }
            }
        }


        if(thisUser.relationship != "") {
            LaunchedEffect(Unit) {
                try {
                    val relationshipRef =
                        db.reference.child("Relationships").child(thisUser.relationship).get()
                            .await()
                    if (relationshipRef.exists()) {
                        relationShip = relationshipRef.getValue(RelationShip::class.java)
                        var other = ""
                        other = if (relationShip?.user1!! != auth.currentUser!!.uid) {
                            relationShip?.user1!!
                        } else {
                            relationShip?.user2!!
                        }
                        val otherUserRef = db.reference.child("Users").child(other).get().await()
                        if (otherUserRef.exists()) {
                            otherUser = otherUserRef.getValue(Perfil::class.java)
                            otherUserImageBitMap = if(otherUser?.image != ""){
                                val ref = storageRef.child("ProfilePics/${otherUser?.image}")
                                val megabytes: Long = 1024 * 1024
                                val byteArray = ref.getBytes(megabytes).await()
                                BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            }else{
                                val ref = storageRef.child("ProfilePics/default_profile_pic.jpg")
                                val megabytes: Long = 1024 * 1024
                                val byteArray = ref.getBytes(megabytes).await()
                                BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle failure
                    e.printStackTrace()
                }
            }
            otherUserImageBitMap?.let {
                userImageBitMap?.let {
                    HomeScreenRelation(
                        modifier = modifier,
                        navController = navController,
                        thisUser = thisUser,
                        userBitMap = userImageBitMap!!,
                        userBitMap2 = otherUserImageBitMap!!,
                        relationShip = relationShip,
                    )
                } ?: run {
                    // If userImageBitMap is null, show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Replace this with an Image that loads your loading XML
                        Image(
                            painter = painterResource(id = R.drawable.loading_img),
                            contentDescription = null
                        )
                    }
                }
            }



        }else{
            if (thisUser.notifications.isNotEmpty()) {
                LaunchedEffect(Unit) {
                    notificationsUsers = getUserDataFromFirebaseWithImage(thisUser.notifications)
                }
                userImageBitMap?.let {
                    notificationsUsers?.let {
                        HomeScreenNoRelation(
                            modifier = modifier,
                            navController = navController,
                            thisUser = thisUser,
                            userBitMap = userImageBitMap!!,
                            notificationsUsers = notificationsUsers!!.toList()
                        )
                    }

                } ?: run {
                    // Mostra o indicador de carregamento enquanto espera
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.loading_img),
                            contentDescription = null
                        )
                    }
                }
            }else{
                userImageBitMap?.let {
                    HomeScreenNoRelation(
                        modifier = modifier,
                        navController = navController,
                        thisUser = thisUser,
                        userBitMap = userImageBitMap!!,
                        notificationsUsers = notificationsUsers!!.toList()
                    )
                } ?: run {
                    // Mostra o indicador de carregamento enquanto espera
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.loading_img),
                            contentDescription = null
                        )
                    }
                }
            }
        }



    }
    // Adjusted spacing between rows
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenRelation(
    modifier: Modifier,
    navController: NavController,
    thisUser: Perfil,
    userBitMap: Bitmap,
    userBitMap2: Bitmap,
    relationShip: RelationShip?
) {
    var showDialogNotifications by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            // Aqui você pode adicionar uma TopAppBar ou outras coisas no topo do seu layout
            // Se precisar de informações específicas, você pode personalizar a TopAppBar conforme necessário
            TopAppBar(
                title = { Text("This is us") },
                actions = {
                    Button(
                        onClick = {
                            navController.navigate(Screens.ProfileScreen.route)
                        },
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

                    Button(
                        onClick = {
                            showDialogNotifications = true
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(start = 8.dp)
                            .background(Color.Transparent)

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.Magenta, // Black icon color
                            modifier = Modifier.size(32.dp) // Adjusted size for a bit bigger icon
                        )
                    }

                }
            )

        }

    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .padding(horizontal = 20.dp)
        ) {
            // Top Section: 'This Is Us' and Profile Button
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Picture (Replace with actual user picture)
                    Image(
                        bitmap = userBitMap.asImageBitmap(), // Replace with your image resource
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
                        bitmap = userBitMap2.asImageBitmap(),  // Replace with your image resource
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentScale = ContentScale.Crop,
                    )
                }
                if(showDialogNotifications){
                    Dialog(
                        onDismissRequest = { showDialogNotifications = false },
                        content = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(375.dp)
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                            ){
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text("Nothing to see here!")
                                }
                            }
                        }
                    )
                }
            }

            item {
                Text(
                    text = relationShip!!.date,
                    modifier = Modifier
                        .padding(top = 8.dp) // Adjusted padding as needed
                )
            }

            item {
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
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Daily Quests",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            item {
                Text("Dates")
                RelationshipProgressBar(relationShip!!.pointsDate)

                Text("Music")
                RelationshipProgressBar(25)

                Text("Gaming")
                RelationshipProgressBar(relationShip!!.pointsGames)

                Text("Pictures")
                RelationshipProgressBar(relationShip!!.pointsPictures)
            }
        }
    }
}

@Composable
fun RelationshipProgressBar(points: Int) {
    val likersFill = points in 1..10
    val loversFill = points in 11..20
    val addictedFill = points > 20

    var likersColor = Color(0xFFD9D9D9)
    var loversColor = Color(0xFFD9D9D9)
    var addictedColor = Color(0xFFD9D9D9)

    if(likersFill) {
        likersColor = Color(0xFFD896FF)
    } else if (loversFill) {
        likersColor = Color(0xFFD896FF)
        loversColor = Color(0xFFBE29EC)
    } else if (addictedFill) {
        likersColor = Color(0xFFD896FF)
        loversColor = Color(0xFFBE29EC)
        addictedColor = Color(0xFF800080)
    }



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            val totalSegments = 3
            val segmentWidth = size.width / totalSegments
            val segmentHeight = 60.dp.toPx() // Ajuste a altura dos quadrados aqui
            val outlineStroke = 2.dp.toPx()

            for (i in 0 until totalSegments) {
                val fillColor = when (i) {
                    0 -> likersColor
                    1 -> loversColor
                    2 -> addictedColor
                    else -> Color(0xFFD9D9D9)
                }

                // Mude a cor da borda para roxo (purple)
                val borderColor = Color(0xFF660066) // Código de cor para roxo (purple)

                drawRect(
                    color = fillColor,
                    size = Size(segmentWidth, segmentHeight),
                    topLeft = Offset(segmentWidth * i, 0f)
                )

                drawRect(
                    color = borderColor,
                    size = Size(segmentWidth, segmentHeight),
                    style = Stroke(width = outlineStroke),
                    topLeft = Offset(segmentWidth * i, 0f)
                )

                // Adiciona o texto correspondente ao quadrado com tamanho de fonte menor
                val text = when (i) {
                    0 -> "Likers"
                    1 -> "Lovers"
                    2 -> "Addicted"
                    else -> ""
                }

                val textColor = if (fillColor != Color(0xFFD9D9D9)) Color.White else Color.Black

                val paint = Paint().apply {
                    color = textColor.toArgb() // Cor do texto ajustada
                    textSize = 16.dp.toPx() // Tamanho da fonte ajustado para 16dp
                }

                // Posiciona o texto no centro do quadrado
                val textX = (segmentWidth - paint.measureText(text)) / 2 + segmentWidth * i
                val textY = segmentHeight / 2 + paint.textSize / 2
                drawContext.canvas.nativeCanvas.drawText(text, textX, textY, paint)

                // Adiciona o texto da faixa
                val rangeText = when (i) {
                    0 -> "1-10"
                    1 -> "11-20"
                    2 -> "21+"
                    else -> ""
                }

                val rangeTextPaint = Paint().apply {
                    color = textColor.toArgb() // Cor do texto ajustada
                    textSize = 12.dp.toPx() // Tamanho da fonte ajustado para 12dp
                }

                // Posiciona o texto da faixa abaixo do texto principal
                val rangeTextX = (segmentWidth - rangeTextPaint.measureText(rangeText)) / 2 + segmentWidth * i
                val rangeTextY = segmentHeight / 2 + rangeTextPaint.textSize / 2 + 20.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(rangeText, rangeTextX, rangeTextY, rangeTextPaint)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenNoRelation(modifier: Modifier, navController: NavController, thisUser: Perfil, userBitMap: Bitmap,notificationsUsers: List<User?>) {
    var searchUser by remember { mutableStateOf<User?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogUser by remember { mutableStateOf(false) }
    var showDialogError by remember { mutableStateOf(false) }
    var showDialogUserInRelationship by remember { mutableStateOf(false) }
    var showDialogNotificationSent by remember { mutableStateOf(false)}
    var showDialogNotifications by remember { mutableStateOf(false)}
    val username = remember { mutableStateOf(TextFieldValue()) }
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
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "This Is Us",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )

            Button(
                onClick = {
                    navController.navigate(Screens.ProfileScreen.route)
                },
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

            //-------------------Still not beautiful mas mostra quantas notifs ele tem ------------------------
            // Honestly i give up tryna make it beautiful i miss flutter
            Button(
                onClick = {
                    showDialogNotifications = true
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 8.dp)
                    .background(Color.Transparent)
                    .clip(CircleShape) // Garante que o botão seja circular
            ) {
                Box{
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                    if (notificationsUsers.isNotEmpty()) {
                        // Adiciona um círculo vermelho com o número
                        Box(
                            modifier = Modifier
                                .background(Color.Red)
                                .padding(2.dp) // Ajuste para controlar a distância do círculo até o ícone
                                .clip(CircleShape)
                                .align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = notificationsUsers.size.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
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
                bitmap = userBitMap.asImageBitmap(), // Replace with your image resource
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
                painter = painterResource(id = R.drawable.add), // Replace with your image resource
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { showDialog = showDialog.not() },
                contentScale = ContentScale.Crop,
            )
        }

        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = "This is a dialog with buttons and an image.",
                                modifier = Modifier.padding(16.dp),
                            )
                            OutlinedTextField(
                                label = { Text("Username") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                value = username.value,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                onValueChange = { username.value = it }
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    db.reference.child("Usernames")
                                        .child(username.value.text.trim()).get().addOnCompleteListener {
                                            if (it.result.exists()){
                                                showDialog = showDialog.not()
                                                showDialogUser = showDialogUser.not()
                                            }else{
                                                showDialog = showDialog.not()
                                                showDialogError = showDialogError.not()
                                            }
                                        }
                                }
                            ) {
                                Text(text = "Search")
                            }
                        }
                    }
                }
            )
        }
        if (showDialogUser) {
            LaunchedEffect(Unit) {
                val data = searchUserDataFromFirebase(username.value.text.trim())
                searchUser = data
            }
            searchUser?.let {
                Dialog(
                    onDismissRequest = { showDialogUser = false },
                    content = {
                        Card{
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                    ) {
                                        // Imagem do perfil
                                        Image(
                                            it.image!!.asImageBitmap(),
                                            contentDescription = null,
                                            contentScale= ContentScale.FillBounds,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .fillMaxSize()
                                        )

                                        // Espaçamento entre a imagem e o texto
                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Informações do usuário
                                        Column {
                                            Text(
                                                text = it.name,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = it.username,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Espaçamento entre o texto e o botão
                                        Spacer(modifier = Modifier.weight(1f))

                                        // Botão de checkmark


                                    }
                                    Button(
                                        modifier = Modifier.fillMaxWidth().padding(start= 24.dp,top= 0.dp,end = 24.dp,bottom = 24.dp),
                                        onClick = {
                                            Log.i("TESTET","${it.relationship} && ${thisUser.relationship}")
                                            if(it.relationship == "" && thisUser.relationship == "") {

                                                db.reference.child("Users").child(it.id.trim()).child("notifications").child(thisUser.username).setValue(
                                                    auth.currentUser!!.uid)

                                                showDialogNotificationSent = true
                                                showDialogUser = false

                                            }else{
                                                showDialogUserInRelationship = true
                                                showDialogUser = false
                                            }

                                        }
                                    ) {
                                        Text(text = "Add")
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
        if(showDialogError){
            Dialog(
                onDismissRequest = { showDialogError = false },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = "Error user not found",
                                modifier = Modifier.padding(16.dp),
                            )
                            Text(
                                text = "Try again?",
                                modifier = Modifier.padding(16.dp),
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    showDialogError = showDialogError.not()
                                    showDialog = showDialog.not()
                                }
                            ) {
                                Text(text = "Try Again")
                            }
                        }
                    }
                }
            )
        }
        if(showDialogUserInRelationship){
            Dialog(
                onDismissRequest = { showDialogUserInRelationship = false
                    showDialogUser = true},
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = "Error already in a relationship",
                                modifier = Modifier.padding(16.dp),
                            )
                            Text(
                                text = "Try again?",
                                modifier = Modifier.padding(16.dp),
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    showDialogUserInRelationship = showDialogUserInRelationship.not()
                                    showDialog = showDialog.not()
                                }
                            ) {
                                Text(text = "Try Again")
                            }
                        }
                    }
                }
            )
        }
        if(showDialogNotificationSent){
            Dialog(
                onDismissRequest = { showDialogNotificationSent = false },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = "A request has been sent to the user.",
                                modifier = Modifier.padding(16.dp),
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    showDialogNotificationSent = showDialogNotificationSent.not()
                                }
                            ) {
                                Text(text = "Ok")
                            }
                        }
                    }
                }
            )
        }
        if(showDialogNotifications){

            Dialog(
                onDismissRequest = { showDialogNotifications = false },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            for (i in notificationsUsers){
                                Card{
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(16.dp)
                                        ) {
                                            // Imagem do perfil
                                            Image(
                                                i!!.image!!.asImageBitmap(),
                                                contentDescription = null,
                                                contentScale= ContentScale.FillBounds,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )

                                            // Espaçamento entre a imagem e o texto
                                            Spacer(modifier = Modifier.width(16.dp))

                                            // Informações do usuário
                                            Column {
                                                Text(
                                                    text = i.name,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Text(
                                                    text = i.username,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            // Espaçamento entre o texto e o botão
                                            Spacer(modifier = Modifier.weight(1f))

                                            // Botão de checkmark
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Check",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clickable {

                                                        if (thisUser.relationship == "" && i.relationship == "") {

                                                            val newRelationShip = db.reference
                                                                .child("Relationships")
                                                                .push()

                                                            val relationShipKey =
                                                                newRelationShip.key

                                                            newRelationShip.setValue(
                                                                RelationShip(
                                                                    0,
                                                                    0,
                                                                    0,
                                                                    0,
                                                                    0,
                                                                    "Today idk",
                                                                    auth.currentUser!!.uid,
                                                                    i.id
                                                                )
                                                            )

                                                            db.reference
                                                                .child("Users")
                                                                .child(auth.currentUser!!.uid)
                                                                .child("relationship")
                                                                .setValue(relationShipKey)
                                                            db.reference
                                                                .child("Users")
                                                                .child(i.id)
                                                                .child("relationship")
                                                                .setValue(relationShipKey)
                                                        }else{
                                                            showDialogUserInRelationship = true
                                                            showDialogNotifications = false
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
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
                text = "You still don't have a partner, try pulling some first!",
                modifier = Modifier.wrapContentSize()
            )
        }
        Spacer(modifier = Modifier.height(10.dp))




        // ... Rest of your content
    }
}


suspend fun getUserDataFromFirebase(userId: String): Perfil? {
    return try {
        val snapshot = db.reference.child("Users").child(userId).get().await()
        if (snapshot.exists()) {
            snapshot.getValue(Perfil::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("Firebase", "Error: ${e.message}")
        null
    }
}

suspend fun getUserDataFromFirebaseWithImage(users: Map<String,String>): List<User?> {
    val returnUsers = mutableListOf<User?>()
    try {
        for(i in users){
            val snapshot = db.reference.child("Users").child(i.value).get().await()
            if (snapshot.exists()) {
                val perfil = snapshot.getValue(Perfil::class.java)
                val bitmap = if (perfil!!.image != "") {
                    val ref = storageRef.child("ProfilePics/${perfil.image}")
                    val megabytes: Long = 1024 * 1024
                    val byteArray = ref.getBytes(megabytes).await()
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else {
                    val ref = storageRef.child("ProfilePics/default_profile_pic.jpg")
                    val megabytes: Long = 1024 * 1024
                    val byteArray = ref.getBytes(megabytes).await()
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                }
                returnUsers.add(User(perfil.name, perfil.username, i.value, bitmap,perfil.relationship))
            }
        }
    } catch (e: Exception) {
        Log.e("Firebase", "Error: ${e.message}")
        return returnUsers
    }
    return returnUsers
}

suspend fun searchUserDataFromFirebase(username:String): User? {
    try {
        val snapshot = db.reference.child("Usernames").child(username).get().await()
        if (snapshot.exists()) {
            val userToken = snapshot.getValue(String::class.java)
            val user = db.reference.child("Users").child(userToken!!).get().await()
            if(user.exists()){
                val perfil = user.getValue(Perfil::class.java)
                val bitmap = if (perfil!!.image != "") {
                    val ref = storageRef.child("ProfilePics/${perfil.image}")
                    val megabytes: Long = 1024 * 1024
                    val byteArray = ref.getBytes(megabytes).await()
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else {
                    val ref = storageRef.child("ProfilePics/default_profile_pic.jpg")
                    val megabytes: Long = 1024 * 1024
                    val byteArray = ref.getBytes(megabytes).await()
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                }
                return User(perfil.name, perfil.username, userToken, bitmap,perfil.relationship)
            }
        } else {
            return null
        }
    } catch (e: Exception) {
        Log.e("Firebase", "Error: ${e.message}")
        return null
    }
    return null
}




@Composable
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    painter: Painter,
    imageDescription: String,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painter,
                    contentDescription = imageDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(160.dp)
                )
                Text(
                    text = "This is a dialog with buttons and an image.",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
