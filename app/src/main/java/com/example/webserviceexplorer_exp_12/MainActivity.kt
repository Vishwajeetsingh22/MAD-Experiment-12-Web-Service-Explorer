package com.example.webserviceexplorer_exp_12

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnFetch = findViewById<Button>(R.id.btnFetch)
        val txtResult = findViewById<TextView>(R.id.txtResult)
        val etUrl = findViewById<TextInputEditText>(R.id.etUrl)

        btnFetch.setOnClickListener {
            val urlString = etUrl.text.toString()
            if (urlString.isEmpty()) {
                txtResult.text = "Please enter a valid URL."
                return@setOnClickListener
            }
            
            txtResult.text = "Fetching data..."

            thread {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    
                    val responseCode = connection.responseCode
                    val contentType = connection.contentType
                    val contentLength = connection.contentLength
                    
                    val response = connection.inputStream.bufferedReader().use { it.readText() }

                    val resultText = buildString {
                        append("=== HTTP RESPONSE ===\n")
                        append("Status Code: $responseCode\n")
                        append("Content Type: $contentType\n")
                        append("Content Length: $contentLength\n\n")
                        append("=== RESPONSE BODY ===\n")
                        try {
                            val json = JSONObject(response)
                            append(json.toString(4))
                        } catch (e: Exception) {
                            append(response)
                        }
                    }

                    runOnUiThread {
                        txtResult.text = resultText
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        txtResult.text = "Error: ${e.message}"
                    }
                }
            }
        }
    }
}