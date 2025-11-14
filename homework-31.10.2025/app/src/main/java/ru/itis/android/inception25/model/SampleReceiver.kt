package ru.itis.android.inception25.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import ru.itis.android.inception25.Keys

class SampleReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context?, intent: Intent?) {
        when (intent?.action) {
            Keys.Notifications.ACTION_REPLY -> {
                val replyText = RemoteInput.getResultsFromIntent(intent)
                    ?.getCharSequence(Keys.Notifications.KEY_TEXT_REPLY)
                    ?.toString()
                    ?: return

                val notificationId = intent.getIntExtra(Keys.Notifications.NOTIFICATION_ID, -1)

                val broadcastIntent = Intent(Keys.Notifications.ACTION_REPLY).apply {
                    `package` = ctx?.packageName
                    putExtra(Keys.Notifications.EXTRA_REPLY_TEXT, replyText)
                    putExtra(Keys.Notifications.NOTIFICATION_ID, notificationId)
                }

                ctx?.sendBroadcast(broadcastIntent)
            }
        }
    }
}