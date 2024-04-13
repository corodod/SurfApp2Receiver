package com.example.surfapp2receiver

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val messageReceiver = Receiver {
        lastReceivedMessage = it
    }
    private var secretKey: String? = null
    private var lastReceivedMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerReceiver()
        resolveProviderData()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun resolveProviderData() {
        val contentResolver = contentResolver
        val providerUri = Uri.parse(SecretProvider.CONTENT_URI_STRING)
        val cursor = contentResolver.query(providerUri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                @SuppressLint("Range")
                val text = it.getString(it.getColumnIndex("text"))
                secretKey = text
                Toast.makeText(
                    this,
                    "Received secret key from provider: $text",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter(SecretProvider.ACTION_SURF)
        ContextCompat.registerReceiver(this, messageReceiver, filter, ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("secretKey", secretKey)
        outState.putString("lastReceivedMessage", lastReceivedMessage)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val key = savedInstanceState.getString("secretKey")
        val message = savedInstanceState.getString("lastReceivedMessage")
        secretKey = key
        lastReceivedMessage = message
        Log.d("MainActivity", "Secret Key: '$secretKey', Last Message: '$lastReceivedMessage'")
    }

    override fun onDestroy() {
        unregisterReceiver(messageReceiver)
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}

object SecretProvider {
    const val ACTION_SURF = "ru.shalkoff.vsu_lesson2_2024.SURF_ACTION"
    const val CONTENT_URI_STRING = "content://dev.surf.android.provider/text"
}
