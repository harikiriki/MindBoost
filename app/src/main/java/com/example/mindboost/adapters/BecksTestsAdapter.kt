package com.example.mindboost.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.historyPage.BecksTestHistoryDetails
import com.example.mindboost.R
import com.example.mindboost.dataclasses.BecksTestDetail

class BecksTestsAdapter(private val becksTestsList: List<BecksTestDetail>) :
    RecyclerView.Adapter<BecksTestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_becks_tests_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val becksTest = becksTestsList[position]
        holder.testDate.text = becksTest.date
        val scoreText = "${becksTest.score} - ${getDepressionCategory(becksTest.score)}"
        holder.testScore.text = scoreText

        holder.detailsBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BecksTestHistoryDetails::class.java)
            intent.putExtra("TEST_DATE", becksTest.date)
            context.startActivity(intent)
        }
    }

    private fun getDepressionCategory(score: Int): String {
        return when (score) {
            in 0..11 -> "brak depresji"
            in 12..19 -> "depresja lekka"
            in 20..25 -> "depresja umiarkowana"
            in 26..63 -> "depresja ciężka"
            else -> "nieokreślony wynik"
        }
    }


    override fun getItemCount(): Int = becksTestsList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testDate: TextView = itemView.findViewById(R.id.testDate)
        val testScore: TextView = itemView.findViewById(R.id.testScore)
        val detailsBtn: Button = itemView.findViewById(R.id.detailsBtn)
    }
}
