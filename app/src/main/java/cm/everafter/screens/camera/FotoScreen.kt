package cm.everafter.screens.camera

import androidx.navigation.NavController
import androidx.compose.runtime.Composable
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
    val storagePermissionState: PermissionState = rememberPermissionState(permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    CheckPermission(navController,hasCameraPermission = cameraPermissionState.status.isGranted,storagePermissionState.status.isGranted,cameraPermissionState::launchPermissionRequest,storagePermissionState::launchPermissionRequest)


}

@Composable
fun CheckPermission(navController:NavController,hasCameraPermission: Boolean,hasStoragePermission:Boolean,onRequestCameraPermission: () -> Unit,onRequestStoragePermission: () -> Unit){
    if(hasCameraPermission && hasStoragePermission){
        CameraScreen(navController)
    }else{
        NoPermissionScreen(navController,onRequestCameraPermission,onRequestStoragePermission)
    }

}