package cm.everafter

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import cm.everafter.screens.playlist.initializeSongs
import cm.everafter.ui.theme.EverAfterTheme
import cm.everafter.viewModels.LocationViewModel
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import android.Manifest.permission.POST_NOTIFICATIONS as POST_NOTIFICATIONS

class MainActivity : ComponentActivity(), ImageLoaderFactory {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                buildScreen()
            } else {
                buildScreen()
            }
        }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()

        buildScreen()
    }

    override fun onStart() {
        super.onStart()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()

        buildScreen()
    }


    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .logger(DebugLogger()) // Remove this line in release builds
            .build()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun buildScreen(){

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,CancellationTokenSource().token)
            .addOnSuccessListener { location: Location? ->
                currentLocation = if (location != null) {
                    LatLng(location.latitude, location.longitude)
                } else {
                    LatLng(0.0, 0.0)

                }

                WindowCompat.setDecorFitsSystemWindows(window, false)
                // Initialize Coil
                Coil.setImageLoader(
                    ImageLoader.Builder(this)
                        .logger(DebugLogger()) // Remove this line in release builds
                        .build()
                )
                setContent {
                    EverAfterTheme {
                        val postNotificationPermission =
                            rememberPermissionState(permission = POST_NOTIFICATIONS)
                        val gameNotificationService = NotificationService(this)
                        LaunchedEffect(key1 = true) {
                            if (!postNotificationPermission.status.isGranted) {
                                postNotificationPermission.launchPermissionRequest()
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            initializeSongs()
                            EverAfter(gameNotificationService, currentLocation)
                        }

                        // A surface container using the 'background' color from the theme
                    }
                }
            }
    }

    private fun requestLocationPermission() {
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}


