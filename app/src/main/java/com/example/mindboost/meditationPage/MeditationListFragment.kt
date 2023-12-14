package com.example.mindboost.meditationPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.adapters.MeditationListAdapter
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
        text2View.text = "Przed rozpoczęciem medytacji znajdź miejsce, w którym nic, ani nikt nie będzie Ci przeszkadzał, aby czuć się w pełni komfortowo. \n\n Usiądź w wygodnej dla Ciebie pozycji, np.: w siadzie skrzyżnym, wyprostuj kręgosłup, a ręce oprzyj na kolanach. Włącz jeden z dostępnych poniżej plików i spróbuj się rozluźnić."

        when (meditationType) {
            "mindfulness_meditation" -> {
                text1View.text = "Medytacja uważności"
            }
            "breath_meditation" -> {
                text1View.text = "Medytacja skupiona na oddechu"
            }
            "transdental_meditation" -> {
                text1View.text = "Medytacja transcendentalna"
            }
            "metta_meditation" -> {
                text1View.text = "Medytacja metta"
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