package com.example.nksh.guessit

import android.content.DialogInterface
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.*


class GameActivity : AppCompatActivity() {
    var itemChangeSoundPlayer: MediaPlayer? = null
    companion object{
        val displayLength = 15
        val numberOfFrames = 5
    }
    enum class WIN_STATUS {
        WON, LOST, TIED
    }
    enum class GAME_STATUS {
        INPROGRESS, OVER, PAUSED
    }
    private var animationCategories = arrayListOf<Int>(
        R.drawable.flower_animation,
        R.drawable.house_animation
    )
    private var answers = ArrayList<ArrayList<String>>()
    private lateinit var aiGuesses: ArrayList<String>
    private var aiGuessesIndex: Int = 0
    private var index: Int = 0
    private var status = GAME_STATUS.OVER
    private var toastYOffset = -100

    private lateinit var imageView: ImageView
    private lateinit var userInput: EditText
    private lateinit var handler: Handler
    private lateinit var frameAnimation: AnimationDrawable
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    var currentImageFrame = 0
    var endTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        itemChangeSoundPlayer = MediaPlayer.create(this, R.raw.popgun)
    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.game_title)
        if(MainActivity.music && !MainActivity.musicPlayer?.isPlaying!!) {
            MainActivity.musicPlayer?.start()
        }
        //TODO put into external function where we call to fill the data into the lists
        answers.add(arrayListOf("flower"))
        answers.add(arrayListOf("house", "building", "cabin"))
        resetGame()
    }

    override fun onPause() {
        // TODO write stats data to MainActivity Shared Prefs
        status = GAME_STATUS.OVER
        if(MainActivity.musicPlayer?.isPlaying!!) {
            MainActivity.musicPlayer?.pause()
        }
        super.onPause()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.quit_game_title)
        builder.setMessage(R.string.quit_game_message)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            //can place any go back home cancelling the event logic here
            quitGame()
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
        builder.show()
    }

    fun resetGame() {
        aiGuesses = ArrayList()
        handler = Handler(Looper.getMainLooper())
        status = GAME_STATUS.INPROGRESS
        currentImageFrame = 0
        endTime = Calendar.getInstance()
        endTime[Calendar.SECOND] += displayLength
        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
                updateTimer()
            }
        })
        userInput = findViewById(R.id.guessInput)
        imageView = findViewById<View>(R.id.gameImageView) as ImageView
        index = (0 until animationCategories.count()).random()
        println("random num is $index")
        imageView.setBackgroundResource(animationCategories[index])
        frameAnimation = imageView.background as AnimationDrawable
        frameAnimation.start()
    }

    fun quitGame() {
        status = GAME_STATUS.OVER
        finish()
    }

    fun endRound(winStatus: WIN_STATUS) {
        status = GAME_STATUS.OVER
        frameAnimation.stop()
        var titleMessage = ""
        when (winStatus) {
            WIN_STATUS.WON -> {
                titleMessage = getString(R.string.game_win)
                MainActivity.wins++
            }
            WIN_STATUS.LOST -> {
                titleMessage = getString(R.string.game_loss)
                MainActivity.losses++
            }
            WIN_STATUS.TIED -> {
                titleMessage = getString(R.string.game_tied)
                MainActivity.ties++
            }
        }
        MainActivity.amountOfRounds--
        createAlertForUserInput(titleMessage)
    }

    fun checkAnswer(answer: String): Boolean {
        for (word in answers[index]) {
            if(word == answer)
                return true
        }
        return false
    }

    fun onGuessButtonClick(view: View) {
        println("guess button clicked")
        if (status != GAME_STATUS.OVER) {
            val input = userInput.text.toString().toLowerCase()
            userInput.text.clear()
            println(input)
            if(checkAnswer(input.toLowerCase())) {
                println("You guessed correctly with $input")
                endRound(WIN_STATUS.WON)
            }
        }
    }

    private fun aiGuess() {
        val image = InputImage.fromBitmap(imageView.drawToBitmap(), 0)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                println("identified image")
                aiGuessesIndex = 0
                aiGuesses = ArrayList()
                for (label in labels) {
                    println(label.text)
                    aiGuesses.add(label.text)
                    if(checkAnswer(label.text.toLowerCase())) {
                        println("Guessed ${label.text} ai wins")
                        val message = resources.getString(R.string.ai_win) + " " + label.text
                        val myToast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                        myToast.setGravity(Gravity.CENTER, 0, toastYOffset)
                        myToast.show()
                        endRound(WIN_STATUS.LOST)
                        break;
                    }
                }
            }
            .addOnFailureListener{ e->
                println("failed to identify image")
                println(e)
            }
    }

    fun updateTimer() {
        if (status == GAME_STATUS.INPROGRESS && status != GAME_STATUS.PAUSED) {
            val currentTime = Calendar.getInstance()
            val diff = endTime.timeInMillis - currentTime.timeInMillis
            val seconds = (diff / 1000) % 60
            val timerText = findViewById<TextView>(R.id.timerTextView)
            timerText.text = seconds.toString()
            //Have the ai guess 5 seconds before every frame switch. Can tweak based on desired
            // difficulty of the game.
            if (seconds == 5L) {
                println("5 seconds left")
                aiGuess()
            }
            //AI quip or saying what it's guess is. Can be tweaked for frequency of response
            if(seconds == 3L || seconds == 11L) {
                if (aiGuesses.count() > 0 && aiGuessesIndex < aiGuesses.count()) {
                    val unsure = resources.getStringArray(R.array.ai_unsure).random()
                    val message = "$unsure ${aiGuesses[aiGuessesIndex]}"
                    val myToast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                    myToast.setGravity(Gravity.CENTER, 0, toastYOffset)
                    myToast.show()
                    aiGuessesIndex++
                }
                else {
                    val quip = resources.getStringArray(R.array.ai_quips).random()
                    val myToast = Toast.makeText(applicationContext, quip, Toast.LENGTH_LONG)
                    myToast.setGravity(Gravity.CENTER, 0, toastYOffset)
                    myToast.show()
                }
            }

            endEvent(currentTime, endTime)
        }
    }
    private fun createAlertForUserInput(titleMessage: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        //TODO replace alert functionality. Feels out of place and slows the game feel
        //TODO replace with toasts and button to proceed to next round and on screen messages
        //TODO along with final animation showing and put button in to go back and quit
        if(MainActivity.amountOfRounds > 0) {
            // set message of alert dialog
            dialogBuilder.setMessage("You have ${MainActivity.amountOfRounds} games left. \nYour score is ${MainActivity.wins} Wins to ${MainActivity.losses} Losses")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(
                    getString(R.string.game_next),
                    DialogInterface.OnClickListener { _, _ ->
                        resetGame()
                    })
                .setNegativeButton(
                    getString(R.string.game_main),
                    DialogInterface.OnClickListener { _, _ ->
                        quitGame()
                    })
        }
        else{
            // set message of alert dialog
            dialogBuilder.setMessage(getString(R.string.game_finished_all_rounds) + MainActivity.wins.toString() + " Wins to " + MainActivity.losses.toString() + " Losses")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setNegativeButton(
                    getString(R.string.game_main),
                    DialogInterface.OnClickListener { _, _ ->
                        quitGame()
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
        if (status != GAME_STATUS.OVER && currentdate.time >= eventdate.time) {
            if (itemChangeSoundPlayer?.isPlaying!!) {
                itemChangeSoundPlayer?.stop()
            }
            if(MainActivity.sound) {
                itemChangeSoundPlayer?.start()
            }
            endTime[Calendar.SECOND] += displayLength
            currentImageFrame++
            if(currentImageFrame == numberOfFrames) {
                handler.removeMessages(0)
                endRound(WIN_STATUS.TIED)
            }
        }
    }
}