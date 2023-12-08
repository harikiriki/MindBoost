package com.example.mindboost.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.historyPage.EmotionsHistoryDescription
import com.example.mindboost.dataclasses.EmotionEntry
import com.example.mindboost.R

class EmotionsHistoryAdapter(private val emotionsList: MutableList<EmotionEntry>) :
    RecyclerView.Adapter<EmotionsHistoryAdapter.ViewHolder>() {

    private val emotionStateMap = mapOf(
        "indifference" to "byłeś obojętny.",
        "anger" to "czułeś gniew.",
        "joy" to "odczuwałeś radość.",
        "cry" to "chciało Ci się płakać.",
        "horror" to "byłeś przerażony.",
        "sadness" to "odczuwałeś smutek.",
        "disappointment" to "czułeś rozczarowanie.",
        "confidence" to "byłeś pewny siebie.",
        "creativity" to "odczuwałeś kreatywność.",
        "happiness" to "byłeś szczęśliwy."
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emotionDate: TextView = view.findViewById(R.id.emotionDate)
        val emoji: ImageView = view.findViewById(R.id.emoji)
        val emotionDescription: TextView = view.findViewById(R.id.emotionDescription)
        val detailsButton: Button = view.findViewById(R.id.detailsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emotions_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emotionEntry = emotionsList[position]
        holder.emotionDate.text = emotionEntry.date

        val emotionTranslation = emotionStateMap[emotionEntry.emotionState] ?: emotionEntry.emotionState
        holder.emotionDescription.text = holder.itemView.context.getString(R.string.emotion_description, emotionTranslation)

        val context = holder.emoji.context
        val emotionId = context.resources.getIdentifier(
            "ic_${emotionEntry.emotionState}_emoji", "drawable", context.packageName
        )
        holder.emoji.setImageResource(emotionId)

        holder.detailsButton.setOnClickListener {
            // Tworzenie Intentu do nowej aktywności
            val intent = Intent(holder.itemView.context, EmotionsHistoryDescription::class.java)

            // Dodawanie danych do Intentu
            intent.putExtra("DATE", emotionEntry.date)
            intent.putExtra("EMOTION_STATE", emotionEntry.emotionState)
            intent.putExtra("ANSWER1", emotionEntry.answer1)
            intent.putExtra("ANSWER2", emotionEntry.answer2)
            intent.putExtra("ANSWER3", emotionEntry.answer3)
            intent.putExtra("ANSWER4", emotionEntry.answer4)

            // Startowanie nowej aktywności
            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateData(newData: List<EmotionEntry>) {
        emotionsList.clear()
        emotionsList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount() = emotionsList.size
}
