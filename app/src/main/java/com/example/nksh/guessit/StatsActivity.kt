package com.example.nksh.guessit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView

class StatsActivity : AppCompatActivity() {

    private lateinit var switchMusic: Switch
    private lateinit var switchSound: Switch
    private lateinit var lossesValue: TextView
    private lateinit var gamesPlayedValue: TextView
    private lateinit var averageTimeValue: TextView
    private lateinit var winsValue: TextView
    private lateinit var tiesValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        switchMusic = findViewById(R.id.switchMusic)
        switchSound = findViewById(R.id.switchSound)
        lossesValue = findViewById(R.id.lossesValue)
        gamesPlayedValue = findViewById(R.id.gamesPlayedValue)
        averageTimeValue = findViewById(R.id.avgTimeValue)
        winsValue = findViewById(R.id.winsValue)
        tiesValue = findViewById(R.id.tiesValue)
    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.stats_title)
        switchMusic.isChecked = MainActivity.music
        switchSound.isChecked = MainActivity.sound
        lossesValue.text = MainActivity.losses.toString()
        gamesPlayedValue.text = MainActivity.gamesPlayed.toString()
        averageTimeValue.text = MainActivity.time.toString()
        winsValue.text = MainActivity.wins.toString()
        tiesValue.text = MainActivity.ties.toString()
    }
    override fun onResume() {
        loadSharedPrefs(this::getSharedPreferences,this::getText)
        if(MainActivity.music && !MainActivity.musicPlayer?.isPlaying!!) {
            MainActivity.musicPlayer?.start()
        }
        super.onResume()
    }
    override fun onPause() {
        setSharedPrefs(this::getSharedPreferences,this::getText)
        if(MainActivity.musicPlayer?.isPlaying!!) {
            MainActivity.musicPlayer?.pause()
        }
        super.onPause()
    }

    fun onHomeButtonClick(view: View) {
        val intent = Intent(this, MainActivity::class.java).apply {}
        startActivity(intent)
    }

    fun onSwitchToggle(view: View) {
        when(view.id) {
            R.id.switchMusic -> {
                MainActivity.music = switchMusic.isChecked
                if(MainActivity.music) {
                    if(!MainActivity.musicPlayer?.isPlaying!!) {
                        MainActivity.musicPlayer?.start()
                    }
                }
                else {
                    if(MainActivity.musicPlayer?.isPlaying!!) {
                        MainActivity.musicPlayer?.pause()
                    }
                }
            }
            R.id.switchSound -> {
                MainActivity.sound = switchSound.isChecked
            }
        }
    }
}