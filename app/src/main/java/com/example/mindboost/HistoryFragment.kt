package com.example.mindboost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the FrameLayouts by their IDs
        val historyBtn1 = view.findViewById<FrameLayout>(R.id.historyBtn1)
        val historyBtn2 = view.findViewById<FrameLayout>(R.id.historyBtn2)
        val historyBtn3 = view.findViewById<FrameLayout>(R.id.historyBtn3)


        // Set click listeners for FrameLayouts
        historyBtn1.setOnClickListener {
            // Navigate to BecksTestHistoryFragment
            findNavController().navigate(R.id.becksTestHistoryFragment)
        }

        historyBtn2.setOnClickListener {
            // Navigate to EmotionsHistoryFragment
            findNavController().navigate(R.id.emotionsHistoryFragment)
        }

        historyBtn3.setOnClickListener {
            // Navigate to EmotionsHistoryFragment
            findNavController().navigate(R.id.moodChartFragment)
        }
    }
}
