package cm.everafter.screens

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

sealed class MemoriesView {
    object CalendarView : MemoriesView()
    object PhotoGridView : MemoriesView()
    object MapView : MemoriesView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedView by remember { mutableStateOf<MemoriesView>(MemoriesView.CalendarView) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Memories")
                },
                actions = {
                    Button(
                        onClick = {
                            selectedView = MemoriesView.CalendarView
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
            is MemoriesView.CalendarView -> {
                CalendarView(paddingValues)
            }
            is MemoriesView.PhotoGridView -> {
                PhotoGridView(paddingValues)
            }
            is MemoriesView.MapView -> {
                MapView(paddingValues)
            }
        }
    }
}

@Composable
fun CalendarView(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(text = "Calendar View")
        // Conteúdo específico do calendário aqui
    }
}

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
fun MapView(paddingValues: PaddingValues) {
    var mapView: MapView? = null

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
                onMapReady(googleMap)
            }
        }
    )
}

fun onMapReady(googleMap: GoogleMap) {
    // Configurações adicionais do mapa após a inicialização

    // Posição de Lisboa, Portugal
    val coordenadasLisboa = LatLng(38.7223, -9.1393)

    // Adicione um marcador em Lisboa
    googleMap.addMarker(
        MarkerOptions()
            .position(coordenadasLisboa)
            .title("Lisboa, Portugal")
    )

    // Configuração da posição e zoom
    val cameraPosition = CameraPosition.Builder()
        .target(coordenadasLisboa) // Define o centro do mapa na posição de Lisboa
        .zoom(15f) // Define o nível de zoom desejado (ajuste conforme necessário)
        .build()

    // Mova a câmera para a posição e zoom desejados
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
}

