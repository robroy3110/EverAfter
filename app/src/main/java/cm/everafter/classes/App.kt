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
import java.util.Date
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
        // Configura a hora para 18:00:00 todos os dias
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 31)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Calcula o atraso inicial garantindo que seja sempre positivo
        val delayInMillis = if (calendar.timeInMillis > System.currentTimeMillis()) {
            calendar.timeInMillis - System.currentTimeMillis()
        } else {
            // Se já passou das 18h hoje, calcula o atraso para o mesmo horário no próximo dia
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.timeInMillis - System.currentTimeMillis()
        }

        // Cria o WorkRequest para ser executado uma vez por dia às 18h
        val checkFreeGamesWorkRequest = PeriodicWorkRequestBuilder<CheckFreeGamesWorker>(
            1, // repetir a cada 1 dia
            TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .build()

        // Agenda o trabalho
        WorkManager.getInstance(this).enqueue(checkFreeGamesWorkRequest)
    }

}