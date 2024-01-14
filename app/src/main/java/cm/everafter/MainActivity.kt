package cm.everafter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import cm.everafter.screens.playlist.initializeSongs
import cm.everafter.ui.theme.EverAfterTheme
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger

class MainActivity : ComponentActivity(), ImageLoaderFactory {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Initialize Coil
        Coil.setImageLoader(
            ImageLoader.Builder(this)
            .logger(DebugLogger()) // Remove this line in release builds
            .build())
        setContent {
            EverAfterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    initializeSongs()
                    EverAfter()
                }
            }
        }
    }
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .logger(DebugLogger()) // Remove this line in release builds
            .build()
    }
}
