package cm.everafter.classes

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val notificationChannel= NotificationChannel(
            "game_notification",
            "Free Games",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(defaultModule)
        }
    }
}