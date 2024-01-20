package cm.everafter.classes

import android.graphics.Bitmap

data class User(val name: String = "", val username: String = "", val id: String = "", val image: Bitmap? = Bitmap.createBitmap(0,0,Bitmap.Config.ARGB_8888), val relationship :String = "")