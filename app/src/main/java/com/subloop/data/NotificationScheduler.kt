package com.subloop.data

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.concurrent.TimeUnit

class NotificationScheduler(context: Context) {
    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun schedule(subscription: SubscriptionEntity) {
        if (!subscription.alertEnabled) {
            cancel(subscription.id)
            return
        }

        val dueDate = runCatching { LocalDate.parse(subscription.nextDueDate, DATE_FORMATTER) }
            .getOrElse {
                cancel(subscription.id)
                return
            }
        val now = LocalDateTime.now()
        if (dueDate.isBefore(now.toLocalDate())) {
            cancel(subscription.id)
            return
        }

        val preferredTime = LocalDateTime.of(
            dueDate.minusDays(subscription.alertDaysBefore.toLong()),
            LocalTime.of(9, 0),
        )
        val notifyAt = if (preferredTime.isAfter(now)) preferredTime else now.plusSeconds(5)
        val delayMillis = Duration.between(now, notifyAt).toMillis().coerceAtLeast(0)

        val request = OneTimeWorkRequestBuilder<SubscriptionReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    SubscriptionReminderWorker.KEY_SUBSCRIPTION_ID to subscription.id,
                    SubscriptionReminderWorker.KEY_NAME to subscription.name,
                    SubscriptionReminderWorker.KEY_DUE_DATE to subscription.nextDueDate,
                    SubscriptionReminderWorker.KEY_DAYS_BEFORE to subscription.alertDaysBefore,
                ),
            )
            .addTag(workName(subscription.id))
            .build()

        workManager.enqueueUniqueWork(
            workName(subscription.id),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancel(subscriptionId: Long) {
        if (subscriptionId > 0) workManager.cancelUniqueWork(workName(subscriptionId))
    }

    private fun workName(subscriptionId: Long) = "subloop-reminder-$subscriptionId"

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT)
    }
}
