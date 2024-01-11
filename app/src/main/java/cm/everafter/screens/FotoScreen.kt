package cm.everafter.screens

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import cm.everafter.MainActivity
import cm.everafter.databinding.ScreenFotoBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi








//FUI AO MANIFEST E ADICIONEI AQUELA CENA, QUALQUER IMPORT Q N DE É DO IR AS DEPENDENCIES, POR FIM HOMESCREEN BUTTON TEM AQUELA CENA PRA VIR PRAKI, NAO ESQUECER O LAYOUT NO RES,
//POR FIM NAO ESQUECER O BOTAO DO MAIN
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FotoScreen(navController:NavController){

    val cameraPermissionState: PermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    CheckPermission(navController,hasPermission = cameraPermissionState.status.isGranted,cameraPermissionState::launchPermissionRequest)


}

@Composable
fun CheckPermission(navController:NavController,hasPermission: Boolean,onRequestPermission: () -> Unit){
    if(hasPermission){
        CameraScreen(navController)
    }else{
        NoPermissionScreen(navController,onRequestPermission)
    }

}