package cm.everafter.screens.camera

import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import cm.everafter.viewModels.LocationViewModel
import cm.everafter.viewModels.UserViewModel
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi








//FUI AO MANIFEST E ADICIONEI AQUELA CENA, QUALQUER IMPORT Q N DE É DO IR AS DEPENDENCIES, POR FIM HOMESCREEN BUTTON TEM AQUELA CENA PRA VIR PRAKI, NAO ESQUECER O LAYOUT NO RES,
//POR FIM NAO ESQUECER O BOTAO DO MAIN
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FotoScreen(userViewModel: UserViewModel,locationViewModel : LocationViewModel){
    val cameraPermissionState: PermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    val fineLocationPermissionState: PermissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    CheckPermission(hasCameraPermission = cameraPermissionState.status.isGranted,cameraPermissionState::launchPermissionRequest,hasFineLocationPermission = fineLocationPermissionState.status.isGranted,fineLocationPermissionState::launchPermissionRequest,userViewModel,locationViewModel )
}

@Composable
fun CheckPermission(hasCameraPermission: Boolean,onRequestCameraPermission: () -> Unit,hasFineLocationPermission: Boolean,onRequestFineLocationPermission: () -> Unit,userViewModel: UserViewModel,locationViewModel : LocationViewModel ){
    if(hasCameraPermission && hasFineLocationPermission){
        CameraScreen(userViewModel = userViewModel,locationViewModel = locationViewModel )
    }else{
        NoPermissionScreen(onRequestCameraPermission,onRequestFineLocationPermission)
    }

}