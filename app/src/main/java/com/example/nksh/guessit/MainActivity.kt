package com.example.nksh.guessit

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object {
        var amountOfRounds = 0
        var musicPlayer: MediaPlayer? = null
        //TODO these values are the source of truth and need to be saved and read from shared prefs for usage
        //************************
        var sound: Boolean = true
        var music: Boolean = true
        var wins = 0
        var losses = 0
        var ties = 0
        var gamesPlayed = 0
        var time = 0
        //************************
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(musicPlayer == null) {
            musicPlayer = MediaPlayer.create(this, R.raw.nk_digital_leisure_minute_loop1)
            musicPlayer?.isLooping = true
        }
    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.app_name)
        if(music && !musicPlayer?.isPlaying!!) {
            musicPlayer?.start()
        }
    }

    override fun onPause() {
        if(musicPlayer?.isPlaying!!) {
            musicPlayer?.pause()
        }
        super.onPause()
    }

    fun onButtonClick(view: View) {
        when(view.id) {
            R.id.startButton -> {
                amountOfRounds =  Integer.parseInt(findViewById<EditText>(R.id.roundsInput).text.toString())
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