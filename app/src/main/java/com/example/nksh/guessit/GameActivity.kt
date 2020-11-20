package com.example.nksh.guessit

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class GameActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private var categories = arrayListOf<String>("flower")
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

        storage = Firebase.storage
        for(i in 1..5){
            var imageString = "gs://quessit-cdffb.appspot.com/images/" + categories[(categories.indices).random()] + "/" + i + ".png"
            val imageRef = storage.getReferenceFromUrl(imageString)
            val localFile = File.createTempFile("images$i", ".png")

            imageRef.getFile(localFile).addOnSuccessListener { 
            }
            }.addOnFailureListener {
                // Handle any errors
            }
            imageRefs.add(imageRef)
        }





        val img = findViewById<View>(R.id.gameImageView) as ImageView
        img.setBackgroundResource(R.drawable.game_animation)
        val frameAnimation = img.background as AnimationDrawable


    }

    fun onGuessButtonClick(view: View) {
        println("guess button clicked")
    }
}