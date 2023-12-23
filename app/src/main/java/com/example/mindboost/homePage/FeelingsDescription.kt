package com.example.mindboost.homePage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class FeelingsDescription : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feelings_description)

        firebaseAuth = FirebaseAuth.getInstance()

        val emotionImageView = findViewById<ImageView>(R.id.emotionPicture)
        val saveButton = findViewById<Button>(R.id.saveBtn)

        val answer1EditText = findViewById<EditText>(R.id.editTextEmotion1)
        val answer2EditText = findViewById<EditText>(R.id.editTextEmotion2)
        val answer3EditText = findViewById<EditText>(R.id.editTextEmotion3)
        val answer4EditText = findViewById<EditText>(R.id.editTextEmotion4)

        val emotionImageId = intent.getIntExtra("EMOTION_IMAGE_ID", -1)
        val emotionState = when (emotionImageId) {
            R.drawable.ic_joy_emoji -> "joy"
            R.drawable.ic_horror_emoji -> "horror"
            R.drawable.ic_anger_emoji -> "anger"
            R.drawable.ic_confidence_emoji -> "confidence"
            R.drawable.ic_cry_emoji -> "cry"
            R.drawable.ic_creativity_emoji -> "creativity"
            R.drawable.ic_disappointment_emoji -> "disappointment"
            R.drawable.ic_happiness_emoji -> "happiness"
            R.drawable.ic_indifference_emoji -> "indifference"
            R.drawable.ic_sadness_emoji -> "sadness"
            else -> ""
        }

        if (emotionImageId != -1) {
            emotionImageView.setImageResource(emotionImageId)
        }

        saveButton.setOnClickListener {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users/${currentUser.uid}/EmotionDiary")
                val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                val dataMap = hashMapOf<String, Any>(
                    "date" to currentDate,
                    "emotionState" to emotionState,
                    "answer1" to answer1EditText.text.toString(),
                    "answer2" to answer2EditText.text.toString(),
                    "answer3" to answer3EditText.text.toString(),
                    "answer4" to answer4EditText.text.toString()
                )

                databaseReference.child(currentDate).setValue(dataMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Zapisano w dzienniku emocji.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Nie udało się zapisać. Spróbuj ponownie.", Toast.LENGTH_SHORT).show()
                    }
                }
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}
