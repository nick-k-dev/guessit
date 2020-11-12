package com.example.nksh.guessit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.stats_title)
    }

    fun onHomeButtonClick(view: View) {
        println("home button pressed")
        val intent = Intent(this, MainActivity::class.java).apply {}
        startActivity(intent)
    }
}