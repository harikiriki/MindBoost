package com.example.mindboost.loginPages

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.R
import com.example.mindboost.databinding.ActivityLoginOrRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

class LoginOrRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOrRegisterBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOrRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicjalizacja Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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

        binding.googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        // Listener dla przycisków logowania przez Facebook i Google można dodać tutaj
        // Na razie pozostawiamy je bez akcji
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Tutaj możesz przekazać dane do aktywności Login i obsłużyć logowanie
                // Możesz użyć Intent, aby przekazać dane konta Google do aktywności Login
                val intent = Intent(this, Login::class.java)
                intent.putExtra("googleAccount", account)
                startActivity(intent)
            } catch (e: ApiException) {
                // Obsługa błędów logowania
            }
        }
    }
}
