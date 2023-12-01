package com.example.mindboost

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.adapters.BecksTestHistoryDetailsAdapter
import com.example.mindboost.dataclasses.BecksTestHistoryDetail
import com.example.mindboost.dataclasses.Odpowiedz
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BecksTestHistoryDetails : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BecksTestHistoryDetailsAdapter
    private val detailsList = mutableListOf<BecksTestHistoryDetail>()
    private lateinit var testDateTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_becks_test_history_details)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = BecksTestHistoryDetailsAdapter(detailsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        testDateTextView = findViewById(R.id.testDate)

        val testDate = intent.getStringExtra("TEST_DATE") // Pobierz datę testu z Intentu
        testDate?.let {
            testDateTextView.text = "Oto udzielone przez Ciebie odpowiedzi na poszczególne pytania w dniu: $it."
        }
        loadTestDetails(testDate)
    }

    private fun loadTestDetails(testDate: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && testDate != null) {
            val userId = currentUser.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/BeckTests/$testDate")
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                for (i in 1..20) { // Zakładając, że mamy 20 pytań
                    val questionRef = FirebaseDatabase.getInstance().getReference("testy/test1/pytania/pytanie$i")
                    questionRef.get().addOnSuccessListener { questionSnapshot ->
                        val question = questionSnapshot.child("tresc").getValue(String::class.java)
                        val score = dataSnapshot.child("question${i}score").getValue(Int::class.java) ?: 0

                        // Teraz pobieramy odpowiedź na podstawie punktów.
                        val answerRef = questionSnapshot.child("odpowiedzi/odpowiedz${score + 1}")
                        val answerObject = answerRef.getValue(Odpowiedz::class.java) // Tworzymy obiekt odpowiedzi
                        val answer = answerObject?.tresc

                        if (question != null && answer != null) {
                            detailsList.add(BecksTestHistoryDetail(question, answer))
                            adapter.notifyDataSetChanged()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Nie udało się załadować pytań.", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Nie udało się załadować detali testu.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
