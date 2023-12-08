package com.example.mindboost

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.meditationPage.MeditationActivity
import com.google.firebase.storage.StorageReference

class MeditationListAdapter(
    private val files: List<StorageReference>,
    private val meditationType: String,
    private val context: Context
) : RecyclerView.Adapter<MeditationListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileNameTextView: TextView = view.findViewById(R.id.fileName)
        // Dodaj inne widoki, jeśli są potrzebne
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meditation_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileRef = files[position]
        holder.fileNameTextView.text = fileRef.name // Pobieranie nazwy pliku z obiektu StorageReference

        holder.itemView.setOnClickListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val intent = Intent(context, MeditationActivity::class.java).apply {
                    putExtra("AUDIO_URL", uri.toString())
                    putExtra("MEDITATION_TYPE", meditationType)
                    putExtra("SONG_NAME", fileRef.name)
                }
                context.startActivity(intent)
            }.addOnFailureListener {
                // Obsługa błędu pobierania URL
            }
        }
    }

    override fun getItemCount() = files.size
}
