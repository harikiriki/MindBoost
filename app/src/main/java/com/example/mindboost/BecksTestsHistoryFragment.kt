package com.example.mindboost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.adapters.BecksTestsAdapter
import com.example.mindboost.dataclasses.BecksTestDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BecksTestsHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BecksTestsAdapter
    private val becksTestsList = mutableListOf<BecksTestDetail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_becks_tests_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = BecksTestsAdapter(becksTestsList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        loadBecksTests()
    }

    private fun loadBecksTests() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/BeckTests")
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                becksTestsList.clear() // Czyść listę przed dodaniem nowych elementów
                dataSnapshot.children.mapNotNull { snapshot ->
                    val testDate = snapshot.child("date").getValue(String::class.java)
                    val testScore = snapshot.child("totalScore").getValue(Int::class.java)
                    if (testDate != null && testScore != null) {
                        BecksTestDetail(testDate, testScore)
                    } else {
                        null
                    }
                }.sortedByDescending { it.date }.also { sortedList ->
                    becksTestsList.addAll(sortedList)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
}

