package com.example.mindboost

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class BecksTestHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_becks_test_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val becksTestStartBtn = view.findViewById<Button>(R.id.becksTestStartBtn)
        becksTestStartBtn.setOnClickListener {
            // Use an Intent to start the Activity
            val intent = Intent(activity, BeckQuestionsTest::class.java)
            startActivity(intent)
        }
    }
}
