package com.example.mindboost

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class PhoneListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_phone_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callButton1 = view.findViewById<FrameLayout>(R.id.phoneNumber1)
        callButton1.setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            phoneIntent.data = Uri.parse("tel:800121212") // Tu wpisz numer telefonu
            startActivity(phoneIntent)
        }

        val callButton2 = view.findViewById<FrameLayout>(R.id.phoneNumber2)
        callButton2.setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            phoneIntent.data = Uri.parse("tel:116111") // Tu wpisz numer telefonu
            startActivity(phoneIntent)
        }

        val callButton3 = view.findViewById<FrameLayout>(R.id.phoneNumber3)
        callButton3.setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            phoneIntent.data = Uri.parse("tel:224848804") // Tu wpisz numer telefonu
            startActivity(phoneIntent)
        }

        val callButton4 = view.findViewById<FrameLayout>(R.id.phoneNumber4)
        callButton4.setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL)
            phoneIntent.data = Uri.parse("tel:225949100") // Tu wpisz numer telefonu
            startActivity(phoneIntent)
        }
    }
}
