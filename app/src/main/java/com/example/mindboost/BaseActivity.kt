package com.example.mindboost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

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
    }
}
