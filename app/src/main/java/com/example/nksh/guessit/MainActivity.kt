package com.example.nksh.guessit

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

/*
* Project: Guess It!
* Authors: Nick Komarnicki and Steven Harrison
* Date: December 11, 2020
* */
class MainActivity : AppCompatActivity() {
    companion object {
        var amountOfRounds = 0
        var currentAmountOfRounds = 0
        var musicPlayer: MediaPlayer? = null
        var sound: Boolean = true
        var music: Boolean = true
        var wins = 0
        var losses = 0
        var ties = 0
        var gamesPlayed = 0
        var time = 0
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
    }
    override fun onResume() {
        loadSharedPrefs(this::getSharedPreferences,this::getText)
        if(music && !musicPlayer?.isPlaying!!) {
            musicPlayer?.start()
        }
        super.onResume()
    }
    override fun onPause() {
        setSharedPrefs(this::getSharedPreferences,this::getText)
        if(musicPlayer?.isPlaying!!) {
            musicPlayer?.pause()
        }
        super.onPause()
    }

    fun onButtonClick(view: View) {
        when(view.id) {
            R.id.startButton -> {
                currentAmountOfRounds =  Integer.parseInt(findViewById<EditText>(R.id.roundsInput).text.toString())
                amountOfRounds = currentAmountOfRounds
                val intent = Intent(this, GameActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.statsButton -> {
                val intent = Intent(this, StatsActivity::class.java).apply {}
                startActivity(intent)
            }
        }
    }
}