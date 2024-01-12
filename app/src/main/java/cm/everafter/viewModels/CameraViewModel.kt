package cm.everafter.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.everafter.screens.SavePhotoToGallery
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CameraViewModel( private val savePhotoToGallery: SavePhotoToGallery) : ViewModel() {

    fun storePhoto(bitmap: Bitmap){
        viewModelScope.launch {
            savePhotoToGallery.call(bitmap)
        }
    }
}