package cm.everafter.screens.memories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import cm.everafter.classes.Perfil
import cm.everafter.classes.RelationShip
import cm.everafter.classes.User
import cm.everafter.screens.home.db
import cm.everafter.screens.home.getUserDataFromFirebaseWithImage
import cm.everafter.screens.home.storageRef
import cm.everafter.viewModels.LocationViewModel
import cm.everafter.viewModels.UserViewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class MemoriesView {
    object CalendarPhotoView : MemoriesView()
    object PhotoGridView : MemoriesView()
    object MapView : MemoriesView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    navController: NavController,
    viewModel: UserViewModel,
    locationViewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {
    var selectedView by remember { mutableStateOf<MemoriesView>(MemoriesView.PhotoGridView) }
    MapsInitializer.initialize(LocalContext.current)
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "Memories")
                },
                actions = {
                    /*Button(
                        onClick = {
                            selectedView = MemoriesView.CalendarPhotoView
                        }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                    }*/
                    Button(
                        onClick = {
                            selectedView = MemoriesView.PhotoGridView
                        }
                    ) {
                        Icon(Icons.Outlined.Star, contentDescription = "Photo Grid")
                    }
                    Button(
                        onClick = {
                            selectedView = MemoriesView.MapView
                        }
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Map View")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (selectedView) {
            /*is MemoriesView.CalendarPhotoView -> {
                CalendarPhotoView(paddingValues, viewModel.loggedInUser!!.relationship)
            }*/
            is MemoriesView.PhotoGridView -> {
                PhotoGridView(paddingValues, viewModel.loggedInUser!!.relationship)
            }
            is MemoriesView.MapView -> {
                MapView(paddingValues,viewModel.loggedInUser!!.relationship,locationViewModel.currentLocation!!)
            }

            else -> {}
        }
    }
}

/*@Composable
fun CalendarPhotoView(paddingValues: PaddingValues) {

    var date by remember {
        mutableStateOf("")
    }

    var dateFireBase by remember {
        mutableStateOf("")
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),

    ) {
        AndroidView(factory = { CalendarView(it) },
            update = { it.setOnDateChangeListener { calendarView, year, month, day ->
                date = "$day - ${month + 1} - $year"
                dateFireBase = "${getMonthByNumber(month+1)} $day,$year"
            }
            })
        Text(text = date)
    }
}*/



@Composable
fun CalendarPhotoView(paddingValues: PaddingValues, relationShip: String) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var photosByDate by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val storageRef = cm.everafter.screens.home.storageRef.child("Memories/${relationShip}")
    // Carregar URLs das imagens ao iniciar a composição
    DisposableEffect(storageRef) {
        val imageUrlsWithNames = runBlocking { getImageUrlsWithNames(storageRef) }
        photosByDate = groupImagesByDate(imageUrlsWithNames.keys.toList())
        onDispose { /* Cleanup, if needed */ }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(factory = { context ->
            CalendarView(context).apply {
                setOnDateChangeListener { calendarView, year, month, day ->
                    val selected = Calendar.getInstance().apply {
                        set(year, month, day)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time

                    selectedDate = if (selectedDate == selected) null else selected
                }
            }
        })

        // Exibir círculo nas datas que possuem fotos
        selectedDate?.let { date ->
            val formattedDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            val photoUrls = photosByDate[formattedDateString]
            if (photoUrls != null && photoUrls.isNotEmpty()) {
                // Exibe o círculo indicando a presença de uma foto
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color = Color.Red, shape = CircleShape)
                        .size(16.dp)
                )

                // Aqui você pode abrir uma nova tela ao clicar no círculo
                // com as fotos em tamanho maior
            }
        }
    }
}







@Composable
fun PhotoGridView(paddingValues: PaddingValues, relationShip: String) {
    var photosByDate by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var photosDetails by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    var selectedImage by remember { mutableStateOf<String>("") }
    var showSelectedImageDialog by remember { mutableStateOf<Boolean>(false) }



    val storageRef = storageRef.child("Memories/${relationShip}")

    LaunchedEffect(storageRef) {
        // Obter mapa de nomes e URLs das imagens do Firebase Storage
        val photosWithNames = getImageUrlsWithNames(storageRef)

        // Organizar fotos pelo timestamp (data)
        val photosGroupedByDate = groupImagesByDate(photosWithNames.keys.toList())

        photosDetails = getImageUrlsWithDetails(storageRef)

        // Preencher o mapa final com URLs organizados por data
        photosByDate = photosGroupedByDate.mapValues { entry ->
            val (_, imageNames) = entry
            imageNames.mapNotNull { imageName ->
                photosWithNames[imageName]
            }
        }
    }

    if(showSelectedImageDialog){

        Dialog(
            onDismissRequest = { showSelectedImageDialog = false },
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(530.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(selectedImage),
                            contentDescription = null,
                            contentScale= ContentScale.FillBounds,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.extraLarge)  // Adiciona bordas arredondadas
                                .padding(4.dp)
                        )
                        val instant = Instant.ofEpochMilli(photosDetails[selectedImage]!![0].toLong())
                        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val formatterHour = DateTimeFormatter.ofPattern("HH:mm:ss")

                        val formattedDate = localDateTime.format(formatterDate)
                        val formattedTime = localDateTime.format(formatterHour)
                        Text(formattedDate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp))
                        Text(formattedTime,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(2.dp))
                        Text(photosDetails[selectedImage]!![2],
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(6.dp))
                        Text(photosDetails[selectedImage]!![1],fontWeight = FontWeight.Light,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp))

                    }
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 16.dp, end = 16.dp)
    ) {
        photosByDate.entries
            .sortedByDescending { it.key }
            .forEachIndexed { index, entry ->
            val (date, photoList) = entry

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = formatDate(date),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                        color = Color.Black,  // Cor do texto
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Create rows with three images each
            val rows = photoList.chunked(3)
            rows.forEach { rowPhotos ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        rowPhotos.forEach { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = null,
                                contentScale= ContentScale.FillBounds,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.extraLarge)  // Adiciona bordas arredondadas
                                    .padding(4.dp)
                                    .clickable {
                                        selectedImage = imageUrl
                                        showSelectedImageDialog = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun MapView(paddingValues: PaddingValues, relationShip: String,currentLocation: LatLng) {
    var mapView: MapView? = null
    var fotosLocations by remember { mutableStateOf<Map<Pair<LatLng,String>,Pair<String,BitmapDescriptor>>?>(null)}
    var photosDetails by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val storageRef = storageRef.child("Memories/${relationShip}")
    var cameraLocation by remember {mutableStateOf<LatLng>(currentLocation)}

    var selectedImage by remember { mutableStateOf<String>("") }
    var showSelectedImageDialog by remember { mutableStateOf<Boolean>(false) }

    LaunchedEffect(Unit) {
        fotosLocations = getFotosLocationsAndBitmapDescriptor(relationShip)
        photosDetails = getImageUrlsWithDetails(storageRef)

    }
    if(showSelectedImageDialog){
        Dialog(
            onDismissRequest = { showSelectedImageDialog = false },
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(530.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(selectedImage),
                            contentDescription = null,
                            contentScale= ContentScale.FillBounds,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.extraLarge)  // Adiciona bordas arredondadas
                                .padding(4.dp)
                        )
                        val instant = Instant.ofEpochMilli(photosDetails[selectedImage]!![0].toLong())
                        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val formatterHour = DateTimeFormatter.ofPattern("HH:mm:ss")

                        val formattedDate = localDateTime.format(formatterDate)
                        val formattedTime = localDateTime.format(formatterHour)
                        Text(formattedDate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp))
                        Text(formattedTime,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(2.dp))
                        Text(photosDetails[selectedImage]!![2],
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(6.dp))
                        Text(photosDetails[selectedImage]!![1],fontWeight = FontWeight.Light,
                            fontSize = 20.sp,  // Ajuste o tamanho da fonte conforme desejado
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp))

                    }
                }
            }
        )
    }
    fotosLocations?.let {
        if (fotosLocations != null) {
            if (fotosLocations!!.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        mapView = MapView(context)
                        mapView!!
                    },
                    update = { mapView ->
                        // Configurações adicionais podem ser feitas aqui
                        mapView?.onCreate(Bundle())
                        mapView?.getMapAsync { googleMap ->
                            // Configurações adicionais do mapa

                            for (foto in fotosLocations!!) {
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(foto.key.first)
                                        .title(foto.key.second)         //Isto esta ganda confusao dont blame me but ask me wtf going on here if u dont know
                                        .icon(foto.value.second)
                                        .snippet(foto.value.first)
                                )

                            }

                            googleMap.setOnInfoWindowClickListener {
                                selectedImage = it.snippet!!
                                cameraLocation = it.position
                                showSelectedImageDialog = true
                            }

                            // Adicione um marcador em Lisboa


                            // Configuração da posição e zoom
                            val cameraPosition = CameraPosition.Builder()
                                .target(cameraLocation) // Define o centro do mapa na posição de Lisboa
                                .zoom(15f) // Define o nível de zoom desejado (ajuste conforme necessário)
                                .build()

                            // Mova a câmera para a posição e zoom desejados
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        }
                    }
                )
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        mapView = MapView(context)
                        mapView!!
                    },
                    update = { mapView ->
                        // Configurações adicionais podem ser feitas aqui
                        mapView?.onCreate(Bundle())
                        mapView?.getMapAsync { googleMap ->
                            // Configurações adicionais do mapa
                            onMapReadyNoPhotos(googleMap)
                        }
                    }
                )
            }
        }
    }
}


fun onMapReadyNoPhotos(googleMap: GoogleMap) {
    // Configurações adicionais do mapa após a inicialização


    // Configuração da posição e zoom
    val cameraPosition = CameraPosition.Builder()
        .target(LatLng(38.697055, -9.4222933)) // Define o centro do mapa na posição de Lisboa
        .zoom(15f) // Define o nível de zoom desejado (ajuste conforme necessário)
        .build()

    // Mova a câmera para a posição e zoom desejados
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
}

fun getMonthByNumber(month: Int) : String{
    when (month) {
        1 -> {
            return "Jan"
        }
        2 -> {
            return "Feb"
        }
        3 -> {
            return "Mar"
        }
        4 -> {
            return "Apr"
        }
        5 -> {
            return "May"
        }
        6 -> {
            return "Jun"
        }
        7 -> {
            return "Jul"
        }
        8 -> {
            return "Aug"
        }
        9 -> {
            return "Sep"
        }
        10 -> {
            return "Oct"
        }
        11 -> {
            return "Nov"
        }
        else -> {
            return "Dec"
        }
    }
}

suspend fun getFotosLocationsAndBitmapDescriptor(relationShip:String): Map<Pair<LatLng,String>, Pair<String,BitmapDescriptor>> {
    val fotosLocations = mutableMapOf<Pair<LatLng,String>,Pair<String,BitmapDescriptor>>()

    val ref = storageRef.child("Memories/${relationShip}")


    val fotosRef = ref.listAll().await()

    for(foto in fotosRef.items){

        val fotoMetadata = foto.metadata.await()
        var download = foto.downloadUrl.await().toString()

        val coordinatesString = fotoMetadata.getCustomMetadata("Coordinates")
        val coordinates = coordinatesString!!.split(", ")
        val location = fotoMetadata.getCustomMetadata("Location")

        val byteArray = foto.getBytes(4096*4096).await()
        val imageBitMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        val width = 150
        val height = 150

        val resizedBitmap = Bitmap.createScaledBitmap(imageBitMap, width, height, false)

        val circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circularBitmap)

        // Desenha um círculo branco como fundo
        val paintCircle = Paint().apply {
            color = Color.White.hashCode()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width.toFloat() / 2, height.toFloat() / 2, width.toFloat() / 2, paintCircle)

        // Desenha o bitmap redimensionado no centro do círculo
        val paintBitmap = Paint().apply {
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(resizedBitmap, 0f, 0f, paintBitmap)

        // Adiciona uma borda branca ao redor do círculo
        val paintBorder = Paint().apply {
            color = Color.White.hashCode()
            style = Paint.Style.STROKE
            strokeWidth = 5f // Ajuste a largura da borda conforme necessário
        }
        canvas.drawCircle(width.toFloat() / 2, height.toFloat() / 2, width.toFloat() / 2, paintBorder)

        val imageBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(circularBitmap)

        fotosLocations[Pair(LatLng(coordinates[0].toDouble(),coordinates[1].toDouble()),location!!)] = Pair(download,imageBitmapDescriptor)


    }

    return fotosLocations
}

// Obter URLs das imagens do Firebase Storage
suspend fun getImageUrls(storageRef: StorageReference): List<String> {
    return try {
        val result = storageRef.listAll().await()
        result.items.map { it.downloadUrl.await().toString() }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun getImageUrlsWithNames(storageRef: StorageReference): Map<String, String> {
    return try {
        val result = storageRef.listAll().await()
        result.items.associateByTo(HashMap()) { it.name }  // Chave: nome do StorageReference, Valor: URL
            .mapValues { (_, storageReference) ->
                storageReference.downloadUrl.await().toString()
            }
    } catch (e: Exception) {
        emptyMap()
    }
}

suspend fun getImageUrlsWithDetails(storageRef: StorageReference): Map<String, List<String>> {
    try {
        val result = storageRef.listAll().await()
        val photosMap : MutableMap<String,List<String>> = mutableMapOf()
        for(foto in result.items){

            val photoDetails = mutableListOf<String>()
            val fotoMetadata = foto.metadata.await()

            val coordinates = fotoMetadata.getCustomMetadata("Coordinates")
            val location = fotoMetadata.getCustomMetadata("Location")
            val downloadUrl = foto.downloadUrl.await().toString()

            photoDetails.add(foto.name)
            photoDetails.add(coordinates!!)
            photoDetails.add(location!!)

            photosMap[downloadUrl] = photoDetails

        }
        return photosMap

    } catch (e: Exception) {
        return emptyMap()
    }
}
fun groupImagesByDate(imageNames: List<String>): Map<String, List<String>> {
    return imageNames.groupBy { calculateDateFromImageName(it) }
}


// Extrair a data do URL
fun calculateDateFromImageName(imageName: String): String {
    val timestamp = imageName.toLongOrNull()
    return if (timestamp != null) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
        date
    } else {
        "Unknown Date"
    }
}


// Função para formatar a data
fun formatDate(date: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = dateFormat.parse(date)

    parsedDate?.let {
        val cal = Calendar.getInstance()
        cal.time = parsedDate

        val currentCal = Calendar.getInstance()

        // Verifica se a data é hoje
        if (cal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == currentCal.get(Calendar.DAY_OF_MONTH)
        ) {
            return "Today"
        }

        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val month = getMonthByNumber(cal.get(Calendar.MONTH) + 1) // Adiciona 1 porque o mês no Calendar começa do zero
        val year = cal.get(Calendar.YEAR)

        return "$dayOfMonth $month, $year"
    }

    return "Unknown Date"
}



