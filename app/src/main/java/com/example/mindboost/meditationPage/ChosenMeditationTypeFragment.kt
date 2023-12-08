package com.example.mindboost.meditationPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.mindboost.R

class ChosenMeditationTypeFragment : Fragment() {

    private var meditationType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            meditationType = it.getString("meditationType")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chosen_meditation_type, container, false)

        val text1View = view.findViewById<TextView>(R.id.text1)
        val text2View = view.findViewById<TextView>(R.id.text2)
        val imageView = view.findViewById<ImageView>(R.id.meditationPicture)

        when (meditationType) {
            "mindfulness_meditation" -> {
                imageView.setImageResource(R.drawable.mindfulness_meditation)
                text1View.text = "Medytacja uważności"
                text2View.text = "Ta forma medytacji polega na świadomym skupieniu uwagi na teraźniejszym momencie. Praktykujący uczą się obserwować swoje myśli, uczucia i doznania z ciała bez oceniania ich. \n\nSłuży ona do redukcji stresu, poprawy koncentracji, zwiększenia świadomości emocjonalnej i rozwijania większej akceptacji wobec doświadczeń życiowych."
            }
            "breath_meditation" -> {
                imageView.setImageResource(R.drawable.breath_meditation)
                text1View.text = "Medytacja skupiona na oddechu"
                text2View.text = "Ten typ medytacji polega na skoncentrowaniu się na oddechu i obserwacji sposobu, w jaki powietrze wchodzi i wychodzi z ciała. \n\nPomaga ona uspokoić umysł, poprawić koncentrację i zwiększyć świadomość ciała."
            }
            "transdental_meditation" -> {
                imageView.setImageResource(R.drawable.transdental_meditation)
                text1View.text = "Medytacja transcendentalna"
                text2View.text = "Ta technika polega na powtarzaniu mantry (dźwięku, słowa lub frazy) w myślach, co pozwala osiągnąć stan głębokiego relaksu i spokoju. \n\nUżywana jest do osiągnięcia stanu głębokiego relaksu, redukcji stresu i uzyskania wewnętrznego spokoju."
            }
            "metta_meditation" -> {
                imageView.setImageResource(R.drawable.metta_meditation)
                text1View.text = "Medytacja metta"
                text2View.text = "Ten typ medytacji skupia się na kultywowaniu uczucia miłości i życzliwości wobec siebie i innych. \n\nłuży do budowania empatii, wyrozumiałości, pozytywnych emocji i zmniejszenia negatywnych uczuć wobec siebie i innych."
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.meditationStartBtn).setOnClickListener {
            val bundle = Bundle().apply {
                putString("meditationType", meditationType)
            }
            findNavController().navigate(R.id.meditationListFragment, bundle)
        }
    }
}
