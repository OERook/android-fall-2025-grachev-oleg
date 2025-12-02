package ru.itis.android.inception25

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import ru.itis.android.inception25.model.SampleReceiver
import ru.itis.android.inception25.model.UserMessage
import ru.itis.android.inception25.navScreens.notifications.NotificationEditScreen
import ru.itis.android.inception25.navScreens.notifications.NotificationSettingsScreen
import ru.itis.android.inception25.navScreens.notifications.UserMessagesScreen
import ru.itis.android.inception25.utils.NotificationHandler
import ru.itis.android.inception25.utils.PermissionHandler
import ru.itis.android.inception25.utils.ResManager

class MainActivity : ComponentActivity() {

    private var permissionsHandler: PermissionHandler? = null
    private var notificationHandler: NotificationHandler? = null
    private var receiver: SampleReceiver? = null

    private var messagesList: MutableList<UserMessage> = mutableListOf()
    private var activeNotificationIds: MutableSet<Int> = mutableSetOf()
    private var notificationCounter: Int = 0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.extras?.getString(Keys.INTENT_KEY)?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        permissionsHandler = PermissionHandler(
            onPermissionGranted = {},
            onPermissionDenied = {},
            activity = this
        )

        val resManager = ResManager(ctx = applicationContext)

        if (notificationHandler == null) {
            notificationHandler = NotificationHandler(ctx = applicationContext, resManager = resManager)
            notificationHandler?.initNotificationChannels()
        }

        if (receiver == null) {
            receiver = SampleReceiver()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    receiver,
                    IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                    Context.RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(receiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsHandler?.requestMultiplePermissions(
                    permission = listOf(Manifest.permission.POST_NOTIFICATIONS)
                )
            }
        }

        setContent {
            NotificationApp(
                messagesList = messagesList,
                activeNotificationIds = activeNotificationIds,
                notificationCounter = notificationCounter,
                onMessagesUpdated = { newMessages ->
                    messagesList.clear()
                    messagesList.addAll(newMessages)
                },
                onActiveNotificationsUpdated = { newIds ->
                    activeNotificationIds.clear()
                    activeNotificationIds.addAll(newIds)
                },
                onNotificationCounterUpdated = { newCounter ->
                    notificationCounter = newCounter
                },
                notificationHandler = notificationHandler
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private companion object {
        const val LOG_TAG = "MainActivity"
    }
}

@Composable
fun NotificationApp(
    messagesList: MutableList<UserMessage>,
    activeNotificationIds: MutableSet<Int>,
    notificationCounter: Int,
    onMessagesUpdated: (List<UserMessage>) -> Unit,
    onActiveNotificationsUpdated: (Set<Int>) -> Unit,
    onNotificationCounterUpdated: (Int) -> Unit,
    notificationHandler: NotificationHandler?
) {
    val context = LocalContext.current
    val selectedTab = remember { mutableIntStateOf(0) }
    val messagesState = remember { mutableStateOf(messagesList.toList()) }
    val activeNotificationIdsState = remember { mutableStateOf(activeNotificationIds.toSet()) }
    val notificationCounterState = remember { mutableIntStateOf(notificationCounter) }

    LaunchedEffect(Unit) {
        val replyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Keys.Notifications.ACTION_REPLY) {
                    val replyText = intent.getStringExtra(Keys.Notifications.EXTRA_REPLY_TEXT)
                    val notificationId = intent.getIntExtra(Keys.Notifications.NOTIFICATION_ID, -1)
                    if (replyText != null) {
                        val newMessage = UserMessage(text = replyText)
                        val updatedMessages = messagesList.toMutableList().apply { add(newMessage) }
                        messagesList.clear()
                        messagesList.addAll(updatedMessages)
                        onMessagesUpdated(updatedMessages)
                        messagesState.value = updatedMessages

                        if (notificationId != -1) {
                            notificationHandler?.cancelNotification(notificationId)
                            val updatedNotifications = activeNotificationIds.toMutableSet().apply { remove(notificationId) }
                            activeNotificationIds.clear()
                            activeNotificationIds.addAll(updatedNotifications)
                            onActiveNotificationsUpdated(updatedNotifications)
                            activeNotificationIdsState.value = updatedNotifications
                        }
                    }
                }
            }
        }

        val dismissReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Keys.Notifications.ACTION_NOTIFICATION_DISMISSED) {
                    val notificationId = intent.getIntExtra(Keys.Notifications.EXTRA_NOTIFICATION_ID, -1)
                    if (notificationId != -1) {
                        val updatedNotifications = activeNotificationIds.toMutableSet().apply { remove(notificationId) }
                        activeNotificationIds.clear()
                        activeNotificationIds.addAll(updatedNotifications)
                        onActiveNotificationsUpdated(updatedNotifications)
                        activeNotificationIdsState.value = updatedNotifications
                    }
                }
            }
        }

        val replyIntentFilter = IntentFilter(Keys.Notifications.ACTION_REPLY).apply {
            priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        }
        val dismissIntentFilter = IntentFilter(Keys.Notifications.ACTION_NOTIFICATION_DISMISSED).apply {
            priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                replyReceiver,
                replyIntentFilter,
                Context.RECEIVER_NOT_EXPORTED
            )
            context.registerReceiver(
                dismissReceiver,
                dismissIntentFilter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                context,
                replyReceiver,
                replyIntentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            ContextCompat.registerReceiver(
                context,
                dismissReceiver,
                dismissIntentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        onMessagesUpdated(messagesList)
        onActiveNotificationsUpdated(activeNotificationIds)
        onNotificationCounterUpdated(notificationCounterState.intValue)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(context.getString(ru.itis.android.inception25.R.string.tab_creation)) },
                    selected = selectedTab.intValue == 0,
                    onClick = { selectedTab.intValue = 0 }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    label = { Text(context.getString(ru.itis.android.inception25.R.string.tab_editing)) },
                    selected = selectedTab.intValue == 1,
                    onClick = { selectedTab.intValue = 1 }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Message, contentDescription = null) },
                    label = { Text(context.getString(ru.itis.android.inception25.R.string.tab_messages)) },
                    selected = selectedTab.intValue == 2,
                    onClick = { selectedTab.intValue = 2 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab.intValue) {
                0 -> NotificationSettingsScreen(
                    onNotificationCreated = { settings ->
                        val notifId = notificationCounterState.intValue
                        notificationCounterState.intValue++
                        onNotificationCounterUpdated(notificationCounterState.intValue)

                        val updatedNotifications = activeNotificationIds.toMutableSet().apply { add(notifId) }
                        activeNotificationIds.clear()
                        activeNotificationIds.addAll(updatedNotifications)
                        onActiveNotificationsUpdated(updatedNotifications)
                        activeNotificationIdsState.value = updatedNotifications

                        notificationHandler?.showNotification(notifId, settings)
                        Toast.makeText(
                            context,
                            "${context.getString(ru.itis.android.inception25.R.string.notification_created_toast)} ${context.getString(ru.itis.android.inception25.R.string.notification_id_label)}: $notifId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                1 -> NotificationEditScreen(
                    activeNotificationIds = activeNotificationIdsState.value,
                    onUpdateNotification = { id, text ->
                        if (activeNotificationIds.contains(id)) {
                            notificationHandler?.updateNotification(id, text)
                            Toast.makeText(
                                context,
                                context.getString(ru.itis.android.inception25.R.string.notification_updated_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(ru.itis.android.inception25.R.string.notification_not_found_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            false
                        }
                    },
                    onCancelAllNotifications = {
                        if (activeNotificationIds.isNotEmpty()) {
                            notificationHandler?.cancelAllNotifications()
                            activeNotificationIds.clear()
                            onActiveNotificationsUpdated(emptySet())
                            activeNotificationIdsState.value = emptySet()
                            Toast.makeText(
                                context,
                                context.getString(ru.itis.android.inception25.R.string.notification_all_cleared_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(ru.itis.android.inception25.R.string.notification_no_notifications_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            false
                        }
                    }
                )

                2 -> UserMessagesScreen(
                    messages = messagesState.value,
                    onMessageAdded = { message ->
                        val updatedMessages = messagesList.toMutableList().apply { add(message) }
                        messagesList.clear()
                        messagesList.addAll(updatedMessages)
                        onMessagesUpdated(updatedMessages)
                        messagesState.value = updatedMessages
                    }
                )
            }
        }
    }
}