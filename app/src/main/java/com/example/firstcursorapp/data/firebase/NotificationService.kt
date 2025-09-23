package com.example.firstcursorapp.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.firstcursorapp.MainActivity
import com.example.firstcursorapp.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID_BREAKING_NEWS = "breaking_news"
        const val CHANNEL_ID_DAILY_DIGEST = "daily_digest"
        const val CHANNEL_ID_CATEGORY = "category_news"
        const val NOTIFICATION_ID_BREAKING = 1001
        const val NOTIFICATION_ID_DIGEST = 1002
        const val NOTIFICATION_ID_CATEGORY = 1003
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val breakingNewsChannel = NotificationChannel(
                CHANNEL_ID_BREAKING_NEWS,
                "Breaking News",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent breaking news alerts"
                enableVibration(true)
                enableLights(true)
            }

            val dailyDigestChannel = NotificationChannel(
                CHANNEL_ID_DAILY_DIGEST,
                "Daily Digest",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily news summary"
                enableVibration(false)
            }

            val categoryChannel = NotificationChannel(
                CHANNEL_ID_CATEGORY,
                "Category News",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "News from your preferred categories"
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(
                listOf(breakingNewsChannel, dailyDigestChannel, categoryChannel)
            )
        }
    }

    fun showBreakingNewsNotification(title: String, body: String, articleUrl: String?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("article_url", articleUrl)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BREAKING_NEWS)
            .setSmallIcon(R.drawable.ic_newspaper)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(NOTIFICATION_ID_BREAKING, notification)
    }

    fun showDailyDigestNotification(title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DAILY_DIGEST)
            .setSmallIcon(R.drawable.ic_newspaper)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(NOTIFICATION_ID_DIGEST, notification)
    }

    fun showCategoryNotification(title: String, body: String, category: String, articleUrl: String?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("article_url", articleUrl)
            putExtra("category", category)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CATEGORY)
            .setSmallIcon(R.drawable.ic_newspaper)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(NOTIFICATION_ID_CATEGORY, notification)
    }

    suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getToken(): Result<String> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

