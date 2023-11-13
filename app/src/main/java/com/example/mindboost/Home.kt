package com.example.mindboost

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Home : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            loadUserNickname(userId)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationBarHome)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeActivity -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                R.id.meditationActivity -> {
                    val intent = Intent(this, MeditationHome::class.java)
                    startActivity(intent)
                    true
                }
                R.id.historyActivity -> {
                    val intent = Intent(this, HistoryHome::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profileActivity -> {
                    val intent = Intent(this, UserProfileHome::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
//        val knowledgeCentre = findViewById<FrameLayout>(R.id.knowledgeCentre)
//        knowledgeCentre.setOnClickListener {
//            val intent = Intent(this, KnowledgeCentre::class.java) // Załóżmy, że KnowledgeCentreActivity jest Twoją aktywnością docelową
//            startActivity(intent)
//        }

        val becksTest = findViewById<FrameLayout>(R.id.becksTest)
        becksTest.setOnClickListener {
            val intent = Intent(this, BecksTestHome::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserNickname(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/nickname")
        databaseReference.get().addOnSuccessListener {
            val nickname = it.value as String?
            val nicknameTextView = findViewById<TextView>(R.id.nickname)
            nickname?.let { name ->
                nicknameTextView.text = name
            }
        }.addOnFailureListener {
            // Obsługa błędów, np. logowanie lub wyświetlanie Toast
        }
    }
}
