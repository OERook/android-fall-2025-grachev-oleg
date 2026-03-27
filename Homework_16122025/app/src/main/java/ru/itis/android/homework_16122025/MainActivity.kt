package ru.itis.android.homework_16122025

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.itis.android.homework_16122025.di.ServiceLocator
import ru.itis.android.homework_16122025.ui.theme.Homework_16122025Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            enableEdgeToEdge()
            
            Log.d("MainActivity", "Initializing database...")
            ServiceLocator.initDatabase(applicationContext)
            Log.d("MainActivity", "Database initialized")
            
            Log.d("MainActivity", "Initializing session manager...")
            ServiceLocator.initSessionManager(applicationContext)
            Log.d("MainActivity", "Session manager initialized")

            setContent {
                Homework_16122025Theme {
                    MusicBoxApp()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
            e.printStackTrace()
            throw e
        }
    }
}