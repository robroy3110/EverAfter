package cm.everafter.classes

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cm.everafter.CheckFreeGamesWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import java.util.Calendar
import java.util.concurrent.TimeUnit


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


        initializeWorkManager()
        initKoin()

    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(defaultModule)
        }
    }

    private fun initializeWorkManager() {
        // Obtém o horário atual
        val currentTimeMillis = System.currentTimeMillis()
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTimeMillis

        // Configura a hora para 18:00:00
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Se o horário atual for após as 18h, agende para o próximo dia
        if (currentCalendar.after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Calcula o tempo de espera até as 18h
        val delayInMillis = calendar.timeInMillis - currentTimeMillis

        // Cria o WorkRequest para ser executado uma vez por dia às 18h
        val checkFreeGamesWorkRequest = OneTimeWorkRequestBuilder<CheckFreeGamesWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        // Agenda o trabalho
        WorkManager.getInstance(this).enqueue(checkFreeGamesWorkRequest)
    }

}