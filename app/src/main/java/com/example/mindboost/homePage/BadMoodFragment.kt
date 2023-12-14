package com.example.mindboost.homePage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mindboost.R


class BadMoodFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bad_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val navController = findNavController()

        view.findViewById<FrameLayout>(R.id.callAfriend).setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            startActivity(phoneIntent)
        }


        view.findViewById<FrameLayout>(R.id.becksTest).setOnClickListener {
            navController.navigate(R.id.becksTestHomeFragment)
        }

        view.findViewById<FrameLayout>(R.id.phoneList).setOnClickListener {
            navController.navigate(R.id.phoneListFragment)
        }

        view.findViewById<FrameLayout>(R.id.meditation).setOnClickListener {
            navController.navigate(R.id.meditationFragment)
        }
    }
}
