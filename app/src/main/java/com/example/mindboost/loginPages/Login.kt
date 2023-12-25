package com.example.mindboost.loginPages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mindboost.R
import com.example.mindboost.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.example.mindboost.homePage.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import org.json.JSONException
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        binding.facebookSignInButton.setOnClickListener {
            signInWithFacebook()
        }

        binding.button2.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (email.contains('@')) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful) {
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Niewłaściwy e-mail!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Uzupełnij wszystkie pola!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign-in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists() || isUserDataIncomplete(dataSnapshot)) {
                                    val intent = Intent(this@Login, CompleteGoogleSignInActivity::class.java)
                                    intent.putExtra("name", acct?.givenName ?: "")
                                    intent.putExtra("lastName", acct?.familyName ?: "")
                                    intent.putExtra("email", acct?.email ?: "")
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val intent = Intent(this@Login, Home::class.java)  // Navigate them to Home or another appropriate activity
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w(TAG, "Failed to read user data", error.toException())
                            }
                        })
                    }
                } else {
                    Log.w(TAG, "Google sign-in failed")
                }
            }
    }

    private fun isUserDataIncomplete(dataSnapshot: DataSnapshot): Boolean {
        val phone = dataSnapshot.child("phone").getValue(String::class.java)
        val birthDate = dataSnapshot.child("birthDate").getValue(String::class.java)
        val gender = dataSnapshot.child("gender").getValue(String::class.java)

        return phone.isNullOrEmpty() || birthDate.isNullOrEmpty() || gender.isNullOrEmpty()
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                // Handle cancel event
            }

            override fun onError(exception: FacebookException) {
                Log.w(TAG, "Facebook sign-in failed", exception)
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Handle successful sign-in
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists() || isUserDataIncomplete(dataSnapshot)) {
                                    // Fetch additional data from Facebook
                                    val request = GraphRequest.newMeRequest(
                                        token
                                    ) { jsonObject, response ->
                                        try {
                                            val firstName = jsonObject?.getString("first_name") ?: "" // user's first name
                                            val lastName = jsonObject?.getString("last_name") ?: "" // user's last name
                                            val email = jsonObject?.getString("email") ?: "" // user's email

                                            val intent = Intent(this@Login, CompleteGoogleSignInActivity::class.java)
                                            intent.putExtra("name", firstName)
                                            intent.putExtra("lastName", lastName)
                                            intent.putExtra("email", email)
                                            startActivity(intent)
                                            finish()

                                        } catch (e: JSONException) {
                                            Log.e(TAG, "JSON error when retrieving Facebook user data", e)
                                        }
                                    }


                                    val parameters = Bundle()
                                    parameters.putString("fields", "first_name,last_name,email")
                                    request.parameters = parameters
                                    request.executeAsync()
                                } else {
                                    val intent = Intent(this@Login, Home::class.java)  // Navigate them to Home or another appropriate activity
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w(TAG, "Failed to read user data", error.toException())
                            }
                        })
                    }
                } else {
                    Log.w(TAG, "Facebook sign-in failed", task.exception)
                }
            }
    }

    fun registerFromLogin(view : View) {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}