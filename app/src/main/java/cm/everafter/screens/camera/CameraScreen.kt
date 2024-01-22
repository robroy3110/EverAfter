package cm.everafter.screens.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cm.everafter.classes.CameraState
import cm.everafter.classes.rotateBitmap
import cm.everafter.viewModels.CameraViewModel
import cm.everafter.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import java.text.DateFormat.getDateInstance
import java.util.Date
import android.location.Location
import android.location.LocationManager
import java.util.Locale
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import cm.everafter.screens.games.db
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraScreen(cameraViewModel: CameraViewModel = koinViewModel(), userViewModel: UserViewModel){

    val cameraState: CameraState by cameraViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember{ LifecycleCameraController(context)}
    val lastCapturedPhoto : Bitmap? = cameraState.capturedImage
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var addresses by remember {mutableStateOf<List<Address>?>(mutableListOf())}
    var country by remember { mutableStateOf<String?>("Not Found") }
    var city by remember {mutableStateOf<String?>("Not Found")}

    val fineLocationPermissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val geocoder: Geocoder = Geocoder(context,Locale.getDefault())
    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {

            var pointsPictures by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                var picturesPointsSnapshot  =  db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsPictures").get().await()

                // Verifique se o snapshot contém algum valor antes de tentar obter as crianças
                if (picturesPointsSnapshot.exists()) {
                    // Obtém a pontuação dos jogos como uma string
                    val picturePointsString = picturesPointsSnapshot.value.toString()
                    // Converte a string para um inteiro (assumindo que a string representa um número)
                    pointsPictures = picturePointsString.toIntOrNull() ?: 0
                } else {
                    // Se não houver dados, defina a pontuação como 0 ou outro valor padrão
                    pointsPictures = 0
                }
            }


            ExtendedFloatingActionButton(
                text = { Text("Take Photo") },
                icon = {Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera capture icon")},
                onClick = {

                    val mainExecutor = ContextCompat.getMainExecutor(context)

                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    if((ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || fineLocationPermissionState.status.isGranted){
                        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if(location != null){
                            latitude = location.latitude
                            longitude = location.longitude
                            addresses = geocoder.getFromLocation(latitude,longitude,5)
                            city = addresses?.get(0)?.locality
                            country = addresses?.get(0)?.countryName
                        }
                    }

                    cameraController.takePicture(mainExecutor, object: ImageCapture.OnImageCapturedCallback(){

                        override fun onCaptureSuccess(image: ImageProxy){

                            val bitmapImage = image.toBitmap().rotateBitmap(image.imageInfo.rotationDegrees)

                            val baos = ByteArrayOutputStream()
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, baos)
                            val imageBytes = baos.toByteArray()

                            val sdf = getDateInstance()
                            val currentDateAndTime = sdf.format(Date())

                            if(city == null){
                                city = "Not Found"
                            }
                            if(country == null){
                                country = "Not Found"
                            }

                            val riversRef = storageRef.child("Memories/${userViewModel.loggedInUser!!.relationship}/${System.currentTimeMillis()}")

                            riversRef.putBytes(imageBytes,
                                StorageMetadata.Builder().setCustomMetadata("Coordinates","$latitude, $longitude").setCustomMetadata("Location","$city, $country").build())

                            cameraViewModel.storePhoto(bitmapImage)

                            pointsPictures += 7
                            // Atualizar a pontuação no banco de dados, se necessário
                            db.reference.child("Relationships").child(userViewModel.loggedInUser!!.relationship).child("pointsPictures").setValue(pointsPictures)

                            // Mostrar um Toast informando sobre a ação
                            Toast.makeText(context, "You both won 7 for taking a picture", Toast.LENGTH_SHORT).show()

                            image.close()
                        }
                    })

                    })
        }

    ) {  paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
            PreviewView(context).apply{
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                setBackgroundColor(Color.BLACK)
                scaleType = PreviewView.ScaleType.FILL_START
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }.also{previewView ->
                previewView.controller = cameraController
                cameraController.bindToLifecycle(lifeCycleOwner)
            }

        })
        if (lastCapturedPhoto != null) {
            Box(modifier = Modifier){
                LastPhotoPreview(
                modifier = Modifier.align(BottomStart),
                lastCapturedPhoto = lastCapturedPhoto
            )}

        }

    }.also{
        DisposableEffect(cameraController) {
            onDispose {
                cameraController.unbind()
            }
        }
    }
}
@SuppressLint("RememberReturnType")
@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap
) {

    val capturedPhoto: ImageBitmap = remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }

    Card(
        modifier = modifier
            .size(128.dp)
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    }
}

