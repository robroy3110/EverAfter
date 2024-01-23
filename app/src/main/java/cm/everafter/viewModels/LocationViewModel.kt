package cm.everafter.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    var currentLocation: LatLng? = null
        set(value) {
            field = value
            Log.i("LocationViewModel", "User location set: $value")
        }
}
