package com.example.mindboost.profilePage

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.R
import com.example.mindboost.homePage.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ChooseAvatarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_avatar)

        val avatar1 = findViewById<ImageView>(R.id.avatar1)
        val avatar2 = findViewById<ImageView>(R.id.avatar2)
        val avatar3 = findViewById<ImageView>(R.id.avatar3)
        val avatar4 = findViewById<ImageView>(R.id.avatar4)
        val avatar5 = findViewById<ImageView>(R.id.avatar5)
        val avatar6 = findViewById<ImageView>(R.id.avatar6)
        val avatar7 = findViewById<ImageView>(R.id.avatar7)
        val avatar8 = findViewById<ImageView>(R.id.avatar8)
        val avatar9 = findViewById<ImageView>(R.id.avatar9)
        val avatar10 = findViewById<ImageView>(R.id.avatar10)
        val avatar11 = findViewById<ImageView>(R.id.avatar11)
        val avatar12 = findViewById<ImageView>(R.id.avatar12)



        avatar1.setOnClickListener { uploadAvatarToFirebase("avatar1") }
        avatar2.setOnClickListener { uploadAvatarToFirebase("avatar2") }
        avatar3.setOnClickListener { uploadAvatarToFirebase("avatar3") }
        avatar4.setOnClickListener { uploadAvatarToFirebase("avatar4") }
        avatar5.setOnClickListener { uploadAvatarToFirebase("avatar5") }
        avatar6.setOnClickListener { uploadAvatarToFirebase("avatar6") }
        avatar7.setOnClickListener { uploadAvatarToFirebase("avatar7") }
        avatar8.setOnClickListener { uploadAvatarToFirebase("avatar8") }
        avatar9.setOnClickListener { uploadAvatarToFirebase("avatar9") }
        avatar10.setOnClickListener { uploadAvatarToFirebase("avatar10") }
        avatar11.setOnClickListener { uploadAvatarToFirebase("avatar11") }
        avatar12.setOnClickListener { uploadAvatarToFirebase("avatar12") }
    }

    private fun uploadAvatarToFirebase(avatarName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val currentUserUid = currentUser.uid
            val fileName = "profileImage_$currentUserUid.jpg"
            val storageRef = FirebaseStorage.getInstance().getReference("profileImages/$fileName")

            val avatarRef = resources.getIdentifier(avatarName, "drawable", packageName)
            val avatarUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(avatarRef))
                .appendPath(resources.getResourceTypeName(avatarRef))
                .appendPath(resources.getResourceEntryName(avatarRef))
                .build()

            storageRef.putFile(avatarUri)
                .addOnSuccessListener {
                    Toast.makeText(this, "Zapisano avatar!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Wystąpił błąd przy zapisie avatara!", Toast.LENGTH_SHORT).show()
                }
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else {
            // Użytkownik nie jest zalogowany
        }
    }
}
