package com.example.mindboost.loginPages

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.databinding.ActivityLoginOrRegisterBinding


class LoginOrRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOrRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOrRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonListeners()
    }


    private fun setupButtonListeners() {
        binding.loginButton.setOnClickListener {
            // Przechodzenie do aktywności logowania
            val loginIntent = Intent(this, Login::class.java)
            startActivity(loginIntent)
        }

        binding.registerButton.setOnClickListener {
            // Przechodzenie do aktywności rejestracji
            val registerIntent = Intent(this, Register::class.java)
            startActivity(registerIntent)
        }
    }
}
