package com.example.mindboost.meditationPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mindboost.R

class MeditationTypesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meditation_types, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FrameLayout>(R.id.meditation1).setOnClickListener {
            navigateWithMeditationType("mindfulness_meditation")
        }

        view.findViewById<FrameLayout>(R.id.meditation2).setOnClickListener {
            navigateWithMeditationType("breath_meditation")
        }

        view.findViewById<FrameLayout>(R.id.meditation3).setOnClickListener {
            navigateWithMeditationType("transdental_meditation")
        }

        view.findViewById<FrameLayout>(R.id.meditation4).setOnClickListener {
            navigateWithMeditationType("metta_meditation")
        }
    }

    private fun navigateWithMeditationType(meditationType: String) {
        val bundle = Bundle().apply {
            putString("meditationType", meditationType)
        }
        val navController = findNavController()
        navController.navigate(R.id.chosenMeditationTypeFragment, bundle)
    }
}
