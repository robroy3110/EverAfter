package cm.everafter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import cm.everafter.screens.playlist.initializeSongs
import cm.everafter.ui.theme.EverAfterTheme
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest.permission.POST_NOTIFICATIONS as POST_NOTIFICATIONS

class MainActivity : ComponentActivity(), ImageLoaderFactory {
    @OptIn(ExperimentalPermissionsApi::class)
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
                val postNotificationPermission=
                    rememberPermissionState(permission = POST_NOTIFICATIONS)
                val gameNotificationService=NotificationService(this)
                LaunchedEffect(key1 = true ){
                    if(!postNotificationPermission.status.isGranted){
                        postNotificationPermission.launchPermissionRequest()
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    initializeSongs()
                    EverAfter(gameNotificationService)
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


