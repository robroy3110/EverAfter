package cm.everafter.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(text = "Map View")
        // Conteúdo específico do mapa aqui
    }
}
