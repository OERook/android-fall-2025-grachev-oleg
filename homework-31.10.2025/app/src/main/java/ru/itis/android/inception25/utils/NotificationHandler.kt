package ru.itis.android.inception25.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import ru.itis.android.inception25.Keys
import ru.itis.android.inception25.MainActivity
import ru.itis.android.inception25.R
import ru.itis.android.inception25.model.NotificationImportance
import ru.itis.android.inception25.model.NotificationSettingsModel
import ru.itis.android.inception25.model.SampleReceiver

class NotificationHandler(
    private val ctx: Context,
    private val resManager: ResManager,
) {
    private val notificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelIfNeeded()
        }
    }

    @SuppressLint("MissingPermission")
    fun showNotification(notificationId: Int, settings: NotificationSettingsModel) {
        val channelId = getChannelIdForImportance(settings.importance)

        val pendingIntent = if (settings.shouldOpenActivity) {
            val intent = Intent(ctx, MainActivity::class.java).apply {
                putExtra(Keys.INTENT_KEY, settings.title)
                putExtra(Keys.EXTRA_PAYLOAD_KEY, settings.content)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            PendingIntent.getActivity(
                ctx,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            null
        }

        val dismissIntent = Intent(Keys.Notifications.ACTION_NOTIFICATION_DISMISSED).apply {
            `package` = ctx.packageName
            putExtra(Keys.Notifications.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            ctx,
            notificationId + 1000,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(settings.title)
            .setContentText(settings.content)
            .setPriority(getNotificationPriority(settings.importance))
            .setAutoCancel(true)
            .setDeleteIntent(dismissPendingIntent)

        if (settings.isExpandable && settings.content.isNotEmpty()) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(settings.content)
            )
        }

        if (settings.shouldOpenActivity && pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        if (settings.hasReplyAction && settings.content.isNotEmpty()) {
            addReplyAction(builder, notificationId)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun updateNotification(notificationId: Int, newText: String) {
        val dismissIntent = Intent(Keys.Notifications.ACTION_NOTIFICATION_DISMISSED).apply {
            `package` = ctx.packageName
            putExtra(Keys.Notifications.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            ctx,
            notificationId + 1000,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(ctx, DEFAULT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(resManager.getString(R.string.notification_updated_title))
            .setContentText(newText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDeleteIntent(dismissPendingIntent) // Важно: устанавливаем deleteIntent и при обновлении
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(newText)
            )
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private fun addReplyAction(builder: NotificationCompat.Builder, notificationId: Int) {
        val remoteInput = RemoteInput.Builder(Keys.Notifications.KEY_TEXT_REPLY)
            .setLabel(resManager.getString(R.string.notification_reply_button))
            .build()

        val replyIntent = Intent(ctx, SampleReceiver::class.java).apply {
            action = Keys.Notifications.ACTION_REPLY
            putExtra(Keys.Notifications.NOTIFICATION_ID, notificationId)
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            ctx,
            notificationId,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_send,
            resManager.getString(R.string.notification_reply_button),
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()

        builder.addAction(replyAction)
    }

    private fun getChannelIdForImportance(importance: NotificationImportance): String {
        return when (importance) {
            NotificationImportance.LOW -> LOW_CHANNEL_ID
            NotificationImportance.MEDIUM -> DEFAULT_CHANNEL_ID
            NotificationImportance.HIGH -> HIGH_CHANNEL_ID
            NotificationImportance.MAX -> MAX_CHANNEL_ID
        }
    }

    private fun getNotificationPriority(importance: NotificationImportance): Int {
        return when (importance) {
            NotificationImportance.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationImportance.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            NotificationImportance.HIGH -> NotificationCompat.PRIORITY_HIGH
            NotificationImportance.MAX -> NotificationCompat.PRIORITY_MAX
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelIfNeeded() {
        val lowChannel = NotificationChannel(
            LOW_CHANNEL_ID,
            resManager.getString(R.string.notification_channel_low),
            NotificationManager.IMPORTANCE_LOW
        )

        val defaultChannel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            resManager.getString(R.string.notification_channel_default),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val highChannel = NotificationChannel(
            HIGH_CHANNEL_ID,
            resManager.getString(R.string.notification_channel_high),
            NotificationManager.IMPORTANCE_HIGH
        )

        val maxChannel = NotificationChannel(
            MAX_CHANNEL_ID,
            resManager.getString(R.string.notification_channel_max),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannels(
            listOf(lowChannel, defaultChannel, highChannel, maxChannel)
        )
    }

    private companion object {
        const val DEFAULT_CHANNEL_ID = "itis_default_channel_id"
        const val LOW_CHANNEL_ID = "itis_low_channel_id"
        const val HIGH_CHANNEL_ID = "itis_high_channel_id"
        const val MAX_CHANNEL_ID = "itis_max_channel_id"
    }
}