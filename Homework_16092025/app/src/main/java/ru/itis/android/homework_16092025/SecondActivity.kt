package ru.itis.android.homework_16092025

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val originalText = intent.getStringExtra("text")
        val displayText = if (originalText.isNullOrEmpty()) "Экран 2" else originalText

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = displayText, style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(this@SecondActivity, ThirdActivity::class.java)
                    if (!originalText.isNullOrEmpty()) {
                        intent.putExtra("text", originalText)
                    }
                    startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Открыть экран 3")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(this@SecondActivity, MainActivity::class.java)
                    startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Открыть экран 1")
                }
            }
        }
    }
}