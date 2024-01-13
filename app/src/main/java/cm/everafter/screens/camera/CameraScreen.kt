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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cm.everafter.classes.CameraState
import cm.everafter.classes.rotateBitmap
import cm.everafter.screens.auth
import cm.everafter.viewModels.CameraViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import java.text.DateFormat.getDateInstance
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraScreen(viewModel: CameraViewModel = koinViewModel()){

    val cameraState: CameraState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember{ LifecycleCameraController(context)}
    val lastCapturedPhoto : Bitmap? = cameraState.capturedImage
    val storage = Firebase.storage("gs://everafter-382e1.appspot.com")
    val storageRef = storage.reference
    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Take Photo") },
                icon = {Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera capture icon")},
                onClick = {
                    val mainExecutor = ContextCompat.getMainExecutor(context)

                    cameraController.takePicture(mainExecutor, object: ImageCapture.OnImageCapturedCallback(){
                        override fun onCaptureSuccess(image: ImageProxy){
                           // var uploadTask = riversRef.putFile()
                            val bitmapImage = image.toBitmap().rotateBitmap(image.imageInfo.rotationDegrees)
                            val baos = ByteArrayOutputStream()
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, baos)
                            val imageBytes = baos.toByteArray()
                            val sdf = getDateInstance()

                            val currentDateAndTime = sdf.format(Date())
                            val riversRef = storageRef.child("Memories/${auth.currentUser!!.uid}/${currentDateAndTime}/idk")
                            riversRef.putBytes(imageBytes)
                            viewModel.storePhoto(bitmapImage)
                            image.close()
                        }
                    })})
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

