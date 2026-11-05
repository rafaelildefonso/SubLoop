package com.subloop.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.subloop.R

class SubscriptionReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val subscriptionId = inputData.getLong(KEY_SUBSCRIPTION_ID, -1L)
        val name = inputData.getString(KEY_NAME) ?: return Result.failure()
        val dueDate = inputData.getString(KEY_DUE_DATE) ?: return Result.failure()
        val daysBefore = inputData.getInt(KEY_DAYS_BEFORE, 3)

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val launchIntent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                applicationContext,
                subscriptionId.toInt(),
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Renovação em $daysBefore dias")
            .setContentText("$name será cobrada em $dueDate.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("A assinatura $name está programada para $dueDate. Revise antes da cobrança."),
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .apply { pendingIntent?.let(::setContentIntent) }
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(subscriptionId.hashCode(), notification)
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Renovações de assinaturas",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Lembretes antes das cobranças cadastradas no SubLoop"
            }
            applicationContext.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_SUBSCRIPTION_ID = "subscription_id"
        const val KEY_NAME = "subscription_name"
        const val KEY_DUE_DATE = "due_date"
        const val KEY_DAYS_BEFORE = "days_before"
        private const val CHANNEL_ID = "subscription_renewals"
    }
}
