package com.example.nksh.guessit

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.random.Random.Default.nextInt

class GameActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private var categories = arrayListOf<Int>(R.drawable.flower_animation)
    private var imageRefs  = arrayListOf<StorageReference>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        FirebaseApp.initializeApp(this);


    }

    override fun onStart() {
        super.onStart()
        var toolbarText = findViewById<TextView>(R.id.toolbar_text)
        toolbarText.text = getString(R.string.game_title)


        val img = findViewById<View>(R.id.gameImageView) as ImageView
        img.setBackgroundResource(categories[Random.nextInt(0,categories.size-1)])
        val frameAnimation = img.background as AnimationDrawable


    }

    fun onGuessButtonClick(view: View) {
        println("guess button clicked")
    }
}