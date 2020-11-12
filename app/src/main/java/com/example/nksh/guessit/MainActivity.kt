package com.example.nksh.guessit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.app_name)
    }

    fun onButtonClick(view: View) {
        when(view.id) {
            R.id.startButton -> {
                println("start button pressed")
                val intent = Intent(this, GameActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.statsButton -> {
                println("stats button pressed")
                val intent = Intent(this, StatsActivity::class.java).apply {}
                startActivity(intent)
            }
        }
    }
}