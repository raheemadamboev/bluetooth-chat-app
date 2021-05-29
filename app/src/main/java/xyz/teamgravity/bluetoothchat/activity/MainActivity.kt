package xyz.teamgravity.bluetoothchat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.teamgravity.bluetoothchat.R
import xyz.teamgravity.bluetoothchat.helper.util.ChatServer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Run the chat server as long as the app is on screen
    override fun onStart() {
        super.onStart()
        ChatServer.startServer(application)
    }

    override fun onStop() {
        super.onStop()
        ChatServer.stopServer()
    }
}