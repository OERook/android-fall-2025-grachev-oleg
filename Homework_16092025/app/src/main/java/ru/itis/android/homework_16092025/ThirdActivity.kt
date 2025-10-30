package ru.itis.android.homework_16092025

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ThirdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedText = intent.getStringExtra("text") ?: "Экран 3"

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = receivedText, style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(this@ThirdActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Открыть экран 1")
                }
            }
        }
    }
}