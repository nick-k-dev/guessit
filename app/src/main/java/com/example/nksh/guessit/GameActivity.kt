package com.example.nksh.guessit

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.media.session.MediaController
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.*
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
        INPROGRESS, OVER
    }
    private lateinit var animationCategories: ArrayList<Int>
    private var answers = ArrayList<ArrayList<String>>()
    private lateinit var aiGuesses: ArrayList<String>
    private var index: Int = 0
    private var status = GAME_STATUS.OVER
    private var toastYOffset = -10

    private lateinit var inProgressContainer: LinearLayout
    private lateinit var gameOverContainer: LinearLayout
    private lateinit var gameOverMessage: TextView
    private lateinit var imageView: ImageView
    private lateinit var userInput: EditText
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
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
        inProgressContainer = findViewById(R.id.inProgressContainer)
        inProgressContainer.visibility = View.VISIBLE
        gameOverContainer = findViewById(R.id.gameOverContainer)
        gameOverContainer.visibility = View.INVISIBLE
        gameOverMessage = findViewById(R.id.gameOverMessage)
        animationCategories = GetCategories()
        answers = GetAnswers()
        handler = Handler(Looper.getMainLooper())
        runnable = object: Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
                updateTimer()
            }
        }
        resetGame()
    }

    override fun onPause() {
        setSharedPrefs(this::getSharedPreferences,this::getText)
        status = GAME_STATUS.OVER
        if(MainActivity.musicPlayer?.isPlaying!!) {
            MainActivity.musicPlayer?.pause()
        }
        super.onPause()
    }
    override fun onResume() {
        loadSharedPrefs(this::getSharedPreferences,this::getText)
        if(MainActivity.music && !MainActivity.musicPlayer?.isPlaying!!) {
            MainActivity.musicPlayer?.start()
        }
        super.onResume()
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
        inProgressContainer.visibility = View.VISIBLE
        gameOverContainer.visibility = View.INVISIBLE
        aiGuesses = ArrayList()
        status = GAME_STATUS.INPROGRESS
        currentImageFrame = 0
        endTime = Calendar.getInstance()
        endTime[Calendar.SECOND] += displayLength
        handler.removeCallbacks(runnable)
        handler.post(runnable)
        userInput = findViewById(R.id.guessInput)
        imageView = findViewById<View>(R.id.gameImageView) as ImageView
        index = (0 until animationCategories.count()).random()
        imageView.setImageDrawable(null)
        imageView.setBackgroundResource(animationCategories[index])
        frameAnimation = imageView.background as AnimationDrawable
        frameAnimation.start()
    }

    fun quitGame() {
        status = GAME_STATUS.OVER
        handler.removeCallbacks(runnable)
        finish()
    }

    fun onButtonClick(view: View) {
        when(view.id) {
            R.id.buttonContinue -> {
                if (MainActivity.currentAmountOfRounds <= 0) {
                    MainActivity.currentAmountOfRounds = MainActivity.amountOfRounds
                }
                resetGame()
            }
            R.id.buttonQuit -> {
                quitGame()
            }
        }
    }

    fun endRound(winStatus: WIN_STATUS) {
        status = GAME_STATUS.OVER
        handler.removeCallbacks(runnable)
        frameAnimation.stop()
        imageView.setImageDrawable(frameAnimation.getFrame(frameAnimation.numberOfFrames - 1))
        gameOverContainer.visibility = View.VISIBLE
        inProgressContainer.visibility = View.INVISIBLE
        var message = ""
        when (winStatus) {
            WIN_STATUS.WON -> {
                message = getString(R.string.game_win)
                MainActivity.wins++
            }
            WIN_STATUS.LOST -> {
                message = getString(R.string.game_loss)
                MainActivity.losses++
            }
            WIN_STATUS.TIED -> {
                message = getString(R.string.game_tied)
                MainActivity.ties++
            }
        }
        message += " The correct options were "
        for (answer in answers[index]) {
            message += answer + ", "
        }
        message = message.substring(0, message.length - 2) + "."
        MainActivity.currentAmountOfRounds--
        if(MainActivity.currentAmountOfRounds > 0) {
            message += " You have ${MainActivity.currentAmountOfRounds} games left. Your score is ${MainActivity.wins} Wins to ${MainActivity.losses} Losses"
        }
        else{
            message += " " + getString(R.string.game_finished_all_rounds) + MainActivity.wins.toString() + " Wins to " + MainActivity.losses.toString() + " Losses"
        }
        gameOverMessage.text = message
    }

    fun checkAnswer(answer: String): Boolean {
        for (word in answers[index]) {
            if(word == answer)
                return true
        }
        return false
    }

    fun onGuessButtonClick(view: View) {
        if (status != GAME_STATUS.OVER) {
            val input = userInput.text.toString().toLowerCase()
            if(checkAnswer(input.toLowerCase())) {
                endRound(WIN_STATUS.WON)
            }
            else {
                userInput.setTextColor(Color.parseColor("#FF0000"))
            }
        }
    }
    fun onSelectInput(view:View){
        userInput.text.clear()
        userInput.setTextColor(Color.parseColor("#000000"))
    }

    private fun aiGuess() {
        val image = InputImage.fromBitmap(imageView.drawToBitmap(), 0)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                aiGuesses = ArrayList()
                for (label in labels) {
                    aiGuesses.add(label.text)
                    if(checkAnswer(label.text.toLowerCase())) {
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
                println(e)
            }
    }

    fun updateTimer() {
        if (status == GAME_STATUS.INPROGRESS) {
            val currentTime = Calendar.getInstance()
            val diff = endTime.timeInMillis - currentTime.timeInMillis
            val seconds = (diff / 1000) % 60
            val timerText = findViewById<TextView>(R.id.timerTextView)
            timerText.text = seconds.toString()
            //Have the ai guess 5 seconds before every frame switch. Can tweak based on desired
            // difficulty of the game.
            if (seconds == 5L) {
                aiGuess()
            }
            //AI quip or saying what it's guess is. Can be tweaked for frequency of response
            if(seconds == 3L || seconds == 11L) {
                if (aiGuesses.count() > 0) {
                    val unsure = resources.getStringArray(R.array.ai_unsure).random()
                    val message = "AI: $unsure ${aiGuesses[(0 until aiGuesses.count()).random()]}"
                    val myToast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                    myToast.setGravity(Gravity.CENTER, 0, toastYOffset)
                    myToast.show()
                }
                else {
                    val quip = "AI: ${resources.getStringArray(R.array.ai_quips).random()}"
                    val myToast = Toast.makeText(applicationContext, quip, Toast.LENGTH_LONG)
                    myToast.setGravity(Gravity.CENTER, 0, toastYOffset)
                    myToast.show()
                }
            }

            endEvent(currentTime, endTime)
        }
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