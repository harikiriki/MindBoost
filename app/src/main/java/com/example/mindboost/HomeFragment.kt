package com.example.mindboost

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            loadUserNickname(userId, view)
        }

        val navController = findNavController()
        val becksTest = view.findViewById<FrameLayout>(R.id.becksTest)
        becksTest.setOnClickListener {
            navController.navigate(R.id.becksTestHomeFragment)
        }

        val phoneList = view.findViewById<FrameLayout>(R.id.phoneList)
        phoneList.setOnClickListener {
            navController.navigate(R.id.phoneListFragment)
        }

    }

    private fun loadUserNickname(userId: String, view: View) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/nickname")
        databaseReference.get().addOnSuccessListener {
            val nickname = it.value as String?
            val nicknameTextView = view.findViewById<TextView>(R.id.nickname)
            nickname?.let { name ->
                nicknameTextView.text = name
            }
        }.addOnFailureListener {
            // Obsługa błędów, np. logowanie lub wyświetlanie Toast
        }
    }
}
