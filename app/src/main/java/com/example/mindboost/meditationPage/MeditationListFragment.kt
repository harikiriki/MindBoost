package com.example.mindboost.meditationPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.MeditationListAdapter
import com.example.mindboost.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MeditationListFragment : Fragment() {

    private var meditationType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            meditationType = it.getString("meditationType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meditation_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text1View = view.findViewById<TextView>(R.id.text1)
        val text2View = view.findViewById<TextView>(R.id.text2)

        when (meditationType) {
            "mindfulness_meditation" -> {
                text1View.text = "Medytacja uważności"
                text2View.text = "tekst1"
            }
            "breath_meditation" -> {
                text1View.text = "Medytacja skupiona na oddechu"
                text2View.text = "tekst2"
            }
            "transdental_meditation" -> {
                text1View.text = "Medytacja transcendentalna"
                text2View.text = "tekst3"
            }
            "metta_meditation" -> {
                text1View.text = "Medytacja metta"
                text2View.text = "tekst4"
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            // Ustawienie LayoutManagera tutaj
            layoutManager = LinearLayoutManager(context)
        }

        val files = mutableListOf<StorageReference>()

        meditationType?.let { type ->
            val storageRef = FirebaseStorage.getInstance().reference.child(type)
            storageRef.listAll().addOnSuccessListener { result ->
                result.items.forEach { fileRef ->
                    files.add(fileRef)
                }
                if (context != null) {
                    recyclerView.adapter = MeditationListAdapter(files, type, requireContext()) // Przekazujemy type do adaptera
                }
            }
        }

        meditationType?.let { type ->
            recyclerView.adapter = MeditationListAdapter(files, type, requireContext())
        }
    }
}