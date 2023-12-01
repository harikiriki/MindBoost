//package com.example.mindboost
//
//import android.os.Bundle
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//
//class EmotionsHistoryDescription : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_emotions_history_description)
//
//        val emotionDateTextView: TextView = findViewById(R.id.emotionDate)
//        val emotionImageView: ImageView = findViewById(R.id.emotionPicture)
//        val editTextEmotion1: EditText = findViewById(R.id.editTextEmotion1)
//        val editTextEmotion2: EditText = findViewById(R.id.editTextEmotion2)
//        val editTextEmotion3: EditText = findViewById(R.id.editTextEmotion3)
//        val editTextEmotion4: EditText = findViewById(R.id.editTextEmotion4)
//
//        val date = intent.getStringExtra("DATE") ?: ""
//        val emotionState = intent.getStringExtra("EMOTION_STATE") ?: ""
//        val answer1 = intent.getStringExtra("ANSWER1") ?: ""
//        val answer2 = intent.getStringExtra("ANSWER2") ?: ""
//        val answer3 = intent.getStringExtra("ANSWER3") ?: ""
//        val answer4 = intent.getStringExtra("ANSWER4") ?: ""
//
//        emotionDateTextView.text = getString(R.string.emotion_date_description, date, getEmotionDescription(emotionState))
//        emotionImageView.setImageResource(getEmotionDrawableId(emotionState))
//
//        editTextEmotion1.setText(answer1)
//        editTextEmotion2.setText(answer2)
//        editTextEmotion3.setText(answer3)
//        editTextEmotion4.setText(answer4)
//    }
//
//    private fun getEmotionDrawableId(emotionState: String): Int {
//        return resources.getIdentifier("ic_${emotionState}_emoji", "drawable", packageName)
//    }
//
//    private fun getEmotionDescription(emotionState: String): String {
//        return when (emotionState) {
//            "indifference" -> "Czułeś się wtedy obojętny"
//            "anger" -> "Odczuwałeś wtedy gniew"
//            "joy" -> "Byłeś wtedy radosny"
//            "cry" -> "Chciało Ci się wtedy płakać"
//            "horror" -> "Byłeś wtedy przerażony"
//            "sadness" -> "Czułeś się wtedy smutny"
//            "disappointment" -> "Czułeś wtedy rozczarowanie"
//            "confidence" -> "Czułeś się wtedy pewny siebie"
//            "creativity" -> "Odczuwałeś wtedy kreatywność"
//            "happiness" -> "Byłeś wtedy szczęśliwy"
//            else -> emotionState // lub zwróć domyślny opis
//        }
//    }
//}

package com.example.mindboost

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EmotionsHistoryDescription : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotions_history_description)

        val emotionDateTextView = findViewById<TextView>(R.id.emotionDate)
        val emotionImageView = findViewById<ImageView>(R.id.emotionPicture)

        val editTextEmotion1 = findViewById<EditText>(R.id.editTextEmotion1)
        val editTextEmotion2 = findViewById<EditText>(R.id.editTextEmotion2)
        val editTextEmotion3 = findViewById<EditText>(R.id.editTextEmotion3)
        val editTextEmotion4 = findViewById<EditText>(R.id.editTextEmotion4)
        val editTextEmotion5 = findViewById<EditText>(R.id.editTextEmotion5)

        val saveButton = findViewById<Button>(R.id.saveBtn)

        val date = intent.getStringExtra("DATE") ?: ""
        val emotionState = intent.getStringExtra("EMOTION_STATE") ?: ""
        val answer1 = intent.getStringExtra("ANSWER1") ?: ""
        val answer2 = intent.getStringExtra("ANSWER2") ?: ""
        val answer3 = intent.getStringExtra("ANSWER3") ?: ""
        val answer4 = intent.getStringExtra("ANSWER4") ?: ""

        emotionDateTextView.text = "Oto zapis Twojego stanu z dnia: $date"
        val emotionId = resources.getIdentifier(
            "ic_${emotionState}_emoji", "drawable", packageName
        )
        emotionImageView.setImageResource(emotionId)

        editTextEmotion1.setText(answer1)
        editTextEmotion2.setText(answer2)
        editTextEmotion3.setText(answer3)
        editTextEmotion4.setText(answer4)

        saveButton.setOnClickListener {
            val updatedAnswer1 = editTextEmotion1.text.toString()
            val updatedAnswer2 = editTextEmotion2.text.toString()
            val updatedAnswer3 = editTextEmotion3.text.toString()
            val updatedAnswer4 = editTextEmotion4.text.toString()
            val answer5 = editTextEmotion5.text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users/${user.uid}/EmotionDiary/$date")
                val updateMap = mapOf(
                    "answer1" to updatedAnswer1,
                    "answer2" to updatedAnswer2,
                    "answer3" to updatedAnswer3,
                    "answer4" to updatedAnswer4,
                    "answer5" to answer5
                )
                databaseReference.updateChildren(updateMap)
            }
            finish()
        }
    }
}
