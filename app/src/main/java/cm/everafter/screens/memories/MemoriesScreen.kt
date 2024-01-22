package cm.everafter.screens.memories

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import cm.everafter.classes.Perfil
import cm.everafter.classes.RelationShip
import cm.everafter.classes.User
import cm.everafter.screens.home.db
import cm.everafter.screens.home.getUserDataFromFirebaseWithImage
import cm.everafter.screens.home.storageRef
import cm.everafter.viewModels.UserViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.tasks.await

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
    modifier: Modifier = Modifier
) {
    var selectedView by remember { mutableStateOf<MemoriesView>(MemoriesView.CalendarPhotoView) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Memories")
                },
                actions = {
                    Button(
                        onClick = {
                            selectedView = MemoriesView.CalendarPhotoView
                        }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                    }
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
            is MemoriesView.CalendarPhotoView -> {
                CalendarPhotoView(paddingValues)
            }
            is MemoriesView.PhotoGridView -> {
                PhotoGridView(paddingValues)
            }
            is MemoriesView.MapView -> {
                MapView(paddingValues,viewModel.loggedInUser!!.relationship)
            }
        }
    }
}

@Composable
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
        modifier = Modifier.fillMaxSize()

    ) {
        AndroidView(factory = { CalendarView(it) },
            update = { it.setOnDateChangeListener { calendarView, year, month, day ->
                date = "$day - ${month + 1} - $year"
                dateFireBase = "${getMonthByNumber(month+1)} $day,$year"
            }
            })
        Text(text = date)
    }
}


/*
@Composable
fun CalendarPhotoView(paddingValues: PaddingValues) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    val photosByDate = // Mapa que associa Date a URL da foto, ou algo similar

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
            val photoUrl = photosByDate[date]
            if (photoUrl != null) {
                // Exibe o círculo indicando a presença de uma foto
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color = Color.Blue, shape = CircleShape)
                        .size(16.dp)
                )

                // Aqui você pode abrir uma nova tela ao clicar no círculo
                // com a foto em tamanho maior
            }
        }
    }
}
*/

@Composable
fun PhotoGridView(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(text = "Photo Grid View")
        // Conteúdo específico da grade de fotos aqui
    }
}

@Composable
fun MapView(paddingValues: PaddingValues,relationShip: String) {
    var mapView: MapView? = null
    var fotosLocations by remember { mutableStateOf<List<Pair<LatLng,String>>?>(mutableListOf())}
    LaunchedEffect(Unit) {
        fotosLocations = getFotosLocations(relationShip)

    }
    fotosLocations?.let {
        if(fotosLocations!!.isNotEmpty()) {
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
                        onMapReady(googleMap, fotosLocations!!)
                    }
                }
            )
        }
    }
}

fun onMapReady(googleMap: GoogleMap, fotosLocations: List<Pair<LatLng,String>>) {
    // Configurações adicionais do mapa após a inicialização

    // Posição de Lisboa, Portugal
        for (location in fotosLocations) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(location.first)
                    .title(location.second)
            )

        }

        // Adicione um marcador em Lisboa


        // Configuração da posição e zoom
        val cameraPosition = CameraPosition.Builder()
            .target(fotosLocations[0].first) // Define o centro do mapa na posição de Lisboa
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

suspend fun getFotosLocations(relationShip:String): List<Pair<LatLng,String>> {
    val fotosLocations = mutableListOf<Pair<LatLng,String>>()

    val ref = storageRef.child("Memories/${relationShip}")


    val fotosRef = ref.listAll().await()

    for(foto in fotosRef.items){
        val fotoMetadata = foto.metadata.await()
        val coordinatesString = fotoMetadata.getCustomMetadata("Coordinates")
        val coordinates = coordinatesString!!.split(", ")
        val location = fotoMetadata.getCustomMetadata("Location")
        if(location != null){
            Log.i("TSES","${Pair(LatLng(coordinates[0].toDouble(),coordinates[1].toDouble()),location)}")
            fotosLocations.add(Pair(LatLng(coordinates[0].toDouble(),coordinates[1].toDouble()),location))
        }else{
            fotosLocations.add(Pair(LatLng(coordinates[0].toDouble(),coordinates[1].toDouble()),"Location Not Found"))
        }

    }


    return fotosLocations
}

