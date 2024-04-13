package com.example.surfapp2receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Receiver(private val onMessageReceived: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SecretProvider.ACTION_SURF) {
            val message = intent.getStringExtra("message")
            onMessageReceived(message ?: "")
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}