package cm.everafter


import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import cm.everafter.classes.Game
import cm.everafter.classes.Song
import kotlin.random.Random

class NotificationService(
    private val context:Context
){
    private val notificationManager=context.getSystemService(NotificationManager::class.java)
    fun showBasicNotification(){
        val notification=NotificationCompat.Builder(context,"game_notification")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink a glass of water")
            .setSmallIcon(R.drawable.game_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showNewFreeGameNotification(game: Game) {

        val notification = NotificationCompat.Builder(context, "game_notification")
            .setContentTitle(game.title + " is now FREE!")
            .setContentText("Check this game out and play it together!")
            .setSmallIcon(R.drawable.game_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)


    }

    fun showNewSongPlayedNotification(songName: String) {

        val notification = NotificationCompat.Builder(context, "game_notification")
            .setContentTitle("Your partner is listening to " + songName)
            .setContentText("Go listen to it and gain points!")
            .setSmallIcon(R.drawable.game_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)


    }


    private fun Context.bitmapFromResource(
        @DrawableRes resId:Int
    )= BitmapFactory.decodeResource(
        resources,
        resId
    )
}