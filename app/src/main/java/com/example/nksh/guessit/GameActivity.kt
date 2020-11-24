package com.example.nksh.guessit

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.random.Random.Default.nextInt

class GameActivity : AppCompatActivity() {
    companion object{
        var wins = 0
        var losses = 0
    }
    private lateinit var storage: FirebaseStorage
    private var categories = arrayListOf<Int>(R.drawable.flower_animation)
    private var imageRefs  = arrayListOf<StorageReference>()

    private val handler = Handler()
    var timercount = 0
    var endTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        FirebaseApp.initializeApp(this);
        endTime[Calendar.SECOND] += 15
        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
                updateTimer()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.game_title)


        val img = findViewById<View>(R.id.gameImageView) as ImageView
        img.setBackgroundResource(categories.random())
        val frameAnimation = img.background as AnimationDrawable
        frameAnimation.start()

    }

    fun onGuessButtonClick(view: View) {
        println("guess button clicked")
    }
    fun updateTimer() {
        val currentTime = Calendar.getInstance()

        val diff = endTime.timeInMillis - currentTime.timeInMillis
        val seconds = (diff / 1000) % 60
        val timerText = findViewById<TextView>(R.id.timerTextView)
        timerText.text = seconds.toString()
        endEvent(currentTime, endTime)
    }
    private fun createAlert(didWin : Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)

        var titleMessage = if(didWin){
            getString(R.string.game_win)
            wins++
        }else{
            getString(R.string.game_loss)
            losses++
        }

        if(MainActivity.amountOfRounds > 0) {
            MainActivity.amountOfRounds--
            // set message of alert dialog
            dialogBuilder.setMessage("You have ${MainActivity.amountOfRounds} games left. \nYour score is $wins Wins to $losses Losses")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(
                    getString(R.string.game_next),
                    DialogInterface.OnClickListener { _, _ ->
                        val intent = Intent(this, GameActivity::class.java).apply {}
                        startActivity(intent)
                    })
                .setNegativeButton(getString(R.string.game_main), DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(this, MainActivity::class.java).apply {}
                    startActivity(intent)
                })
        }
        else{
            // set message of alert dialog
            dialogBuilder.setMessage(getString(R.string.game_finished_all_rounds) + wins.toString() + " Wins to " + losses.toString() + " Losses" )
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setNegativeButton(getString(R.string.game_main), DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(this, MainActivity::class.java).apply {}
                    startActivity(intent)
                })
        }
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(titleMessage)
        // show alert dialog
        alert.show()
    }
    private fun endEvent(currentdate: Calendar, eventdate: Calendar) {
        if (currentdate.time >= eventdate.time) {
            endTime[Calendar.SECOND] += 15
            timercount++
            if(timercount == 5) {
                handler.removeMessages(0)
                createAlert(false)
            }
        }
    }
}