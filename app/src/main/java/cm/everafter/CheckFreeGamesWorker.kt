package cm.everafter

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cm.everafter.classes.Game
import cm.everafter.screens.games.db
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.tasks.await

class CheckFreeGamesWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // Obtenha a lista de jogos
            val gamesRef = db.getReference("Games")
            val gamesSnapshot = gamesRef.get().await()

            // Transforme o snapshot em uma lista de jogos
            val games = gamesSnapshot.children.mapNotNull { it.getValue(Game::class.java) }
            // Verifique e notifique os novos jogos gratuitos
            val gameNotificationService = NotificationService(applicationContext)

            for (game in games) {
                if (isGameFreeToday(game)) {
                    gameNotificationService.showNewFreeGameNotification(game)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            // Trate qualquer exceção que possa ocorrer durante o trabalho
            return Result.failure()
        }
    }

    private fun getCurrentDate(): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        val dateString = dateFormat.format(currentDate)
        return dateFormat.parse(dateString) ?: Date()
    }

    private fun isGameFreeToday(game: Game): Boolean {
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(game.free_start_date) ?: return false
        val currentDate = getCurrentDate()

        // Verifica se a data de hoje é igual a free_start_date e se a hora é 18:00 ou posterior
        return currentDate.compareTo(startDate) == 0
    }

}
