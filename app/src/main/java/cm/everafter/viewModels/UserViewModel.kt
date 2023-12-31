package cm.everafter.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import cm.everafter.Perfil

class UserViewModel : ViewModel() {
    var loggedInUser: Perfil? = null
        set(value) {
            field = value
            Log.i("UserViewModel", "User details set: $value")
        }
}
