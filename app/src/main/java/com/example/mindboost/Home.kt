package com.example.mindboost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homeContainer) as NavHostFragment
        navController = navHostFragment.navController
        val navigationBarHome = findViewById<BottomNavigationView>(R.id.navigationBarHome)
        setupWithNavController(navigationBarHome, navController)

        handleIntentNavigation()
    }

    private fun handleIntentNavigation() {
        val navigateTo = intent.getStringExtra("NAVIGATE_TO")
        if (navigateTo != null) {
            when (navigateTo) {
                "PhoneListFragment" -> navController.navigate(R.id.phoneListFragment)
                // Dodaj inne przypadki, jeśli to konieczne
            }
        }
    }
}
