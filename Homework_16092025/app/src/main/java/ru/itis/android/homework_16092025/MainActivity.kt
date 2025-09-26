package ru.itis.android.homework_16092025

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var text by remember { mutableStateOf("") }

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Введите текст") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                        intent.putExtra("text", text)
                    startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Открыть экран 2")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(this@MainActivity, ThirdActivity::class.java)
                    if (text.isNotEmpty()) {
                        intent.putExtra("text", text)
                    }
                    startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Открыть экран 3")
                }
            }
        }
    }
}