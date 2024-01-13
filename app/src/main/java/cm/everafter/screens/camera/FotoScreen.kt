package cm.everafter.screens.camera

import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import cm.everafter.viewModels.UserViewModel
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi








//FUI AO MANIFEST E ADICIONEI AQUELA CENA, QUALQUER IMPORT Q N DE É DO IR AS DEPENDENCIES, POR FIM HOMESCREEN BUTTON TEM AQUELA CENA PRA VIR PRAKI, NAO ESQUECER O LAYOUT NO RES,
//POR FIM NAO ESQUECER O BOTAO DO MAIN
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FotoScreen(userViewModel: UserViewModel){
    val cameraPermissionState: PermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    CheckPermission(hasCameraPermission = cameraPermissionState.status.isGranted,cameraPermissionState::launchPermissionRequest,userViewModel)
}

@Composable
fun CheckPermission(hasCameraPermission: Boolean,onRequestCameraPermission: () -> Unit,userViewModel: UserViewModel){
    if(hasCameraPermission){
        CameraScreen(userViewModel = userViewModel)
    }else{
        NoPermissionScreen(onRequestCameraPermission)
    }

}