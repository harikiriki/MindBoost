package com.example.mindboost.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.R
import com.example.mindboost.dataclasses.BecksTestHistoryDetail

class BecksTestHistoryDetailsAdapter(private val detailsList: List<BecksTestHistoryDetail>) :
    RecyclerView.Adapter<BecksTestHistoryDetailsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionTextView: TextView = view.findViewById(R.id.question)
        val answerTextView: TextView = view.findViewById(R.id.answer)
        val questionNumberTextView: TextView = view.findViewById(R.id.questionNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_becks_tests_history_details, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail = detailsList[position]
        holder.questionTextView.text = detail.question
        holder.answerTextView.text = detail.answer
        holder.questionNumberTextView.text = "Pytanie ${position + 1}/20" // Ustawienie numeru pytania
    }

    override fun getItemCount() = detailsList.size
}
