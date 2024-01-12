package cm.everafter.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cm.everafter.viewModels.CameraViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraScreen(navController : NavController,viewModel: CameraViewModel = koinViewModel()){

    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember{ LifecycleCameraController(context)}

    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Take Photo") },
                icon = { /*TODO*/ },
                onClick = {
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(mainExecutor, object: ImageCapture.OnImageCapturedCallback(){
                        override fun onCaptureSuccess(image: ImageProxy){
                            val riversRef = storageRef.child("ProfilePics/${auth.currentUser!!.uid}")
                           // var uploadTask = riversRef.putFile()
                            val bitmapImage = image.toBitmap()
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
            }.also{previewView ->
                previewView.controller = cameraController
                cameraController.bindToLifecycle(lifeCycleOwner)
            }

        })

    }
}