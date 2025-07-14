package com.myapp.clearwrite

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View

class MainActivity : AppCompatActivity() {

    private val geminiApiKey = "" // 
    var result=""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.inputText)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val outputText = findViewById<TextView>(R.id.outputText)

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = geminiApiKey
        )

        sendButton.setOnClickListener {
            val userText = inputText.text.toString().trim()
            if (userText.isNotEmpty()) {
                val prompt = "only give a 200 word essay on the Topic:\n\n$userText"
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = generativeModel.generateContent(prompt)
                        val result = response.text ?: "No response from AI."
                        withContext(Dispatchers.Main) {
                            outputText.text = result.trim()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            outputText.text = "Error: ${e.localizedMessage}"
                        }
                    }
                }
            }
        }
        val copyButton = findViewById<Button>(R.id.copyButton)

        sendButton.setOnClickListener {
            val userText = inputText.text.toString().trim()
            if (userText.isNotEmpty()) {
                val prompt = "Improve the clarity and grammar of the following text:\n\n$userText"

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = generativeModel.generateContent(prompt)
                        val result = response.text ?: "No response from AI."

                        withContext(Dispatchers.Main) {
                            outputText.text = result.trim()
                            copyButton.visibility = View.VISIBLE

                            copyButton.setOnClickListener {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AI Output", outputText.text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(this@MainActivity, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            outputText.text = "Error: ${e.localizedMessage}"
                        }
                    }
                }
            }
        }


    }
}
