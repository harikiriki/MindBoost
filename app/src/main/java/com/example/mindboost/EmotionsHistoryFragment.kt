package com.example.mindboost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.adapters.EmotionsHistoryAdapter
import com.example.mindboost.dataclasses.EmotionEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EmotionsHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmotionsHistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emotions_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EmotionsHistoryAdapter(mutableListOf()) // Inicjalizacja z pustą mutowalną listą
        recyclerView.adapter = adapter

        loadEmotionsData()
    }

    private fun loadEmotionsData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users/${user.uid}/EmotionDiary")
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                val emotionsList = dataSnapshot.children.mapNotNull { snapshot ->
                    val date = snapshot.child("date").getValue(String::class.java)
                    val emotionState = snapshot.child("emotionState").getValue(String::class.java)
                    val answer1 = snapshot.child("answer1").getValue(String::class.java) ?: ""
                    val answer2 = snapshot.child("answer2").getValue(String::class.java) ?: ""
                    val answer3 = snapshot.child("answer3").getValue(String::class.java) ?: ""
                    val answer4 = snapshot.child("answer4").getValue(String::class.java) ?: ""

                    if (date != null && emotionState != null) {
                        EmotionEntry(date, emotionState, answer1, answer2, answer3, answer4)
                    } else {
                        null
                    }
                }.toMutableList()
                emotionsList.reverse()
                adapter.updateData(emotionsList)
                recyclerView.adapter = adapter
            }
        }
    }
}
