//package com.example.mindboost
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.Collections.max
//
//class HomeFragment : Fragment() {
//
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var reminderTextView: TextView
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        firebaseAuth = FirebaseAuth.getInstance()
//        val currentUser = firebaseAuth.currentUser
//        reminderTextView = view.findViewById(R.id.reminder)
//
//        if (currentUser != null) {
//            val userId = currentUser.uid
//            loadUserNickname(userId, view)
//            loadLastTestDateAndSetReminder(userId)
//        }
//
//        val navController = findNavController()
//        val becksTest = view.findViewById<FrameLayout>(R.id.becksTest)
//        becksTest.setOnClickListener {
//            navController.navigate(R.id.becksTestHomeFragment)
//        }
//
//        val phoneList = view.findViewById<FrameLayout>(R.id.phoneList)
//        phoneList.setOnClickListener {
//            navController.navigate(R.id.phoneListFragment)
//        }
//
//    }
//
//    private fun loadUserNickname(userId: String, view: View) {
//        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/nickname")
//        databaseReference.get().addOnSuccessListener {
//            val nickname = it.value as String?
//            val nicknameTextView = view.findViewById<TextView>(R.id.nickname)
//            nickname?.let { name ->
//                nicknameTextView.text = name
//            }
//        }.addOnFailureListener {
//            // Obsługa błędów, np. logowanie lub wyświetlanie Toast
//        }
//    }
//
//    private fun loadLastTestDateAndSetReminder(userId: String) {
//        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/lastTestDate")
//        databaseReference.get().addOnSuccessListener { dataSnapshot ->
//            val lastTestDateString = dataSnapshot.value as String?
//            lastTestDateString?.let { dateString ->
//                // Here you parse the date and calculate the days until the next test
//                val lastTestDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateString)
//                lastTestDate?.let { date ->
//                    val daysUntilNextTest = calculateDaysUntilNextTest(date)
//                    reminderTextView.text = getString(R.string.days_until_next_test, daysUntilNextTest)
//                }
//            }
//        }.addOnFailureListener {
//            // Handle failure
//        }
//    }
//
//    private fun calculateDaysUntilNextTest(lastTestDate: Date): Int {
//        val today = Calendar.getInstance()
//        val testCalendar = Calendar.getInstance().apply {
//            time = lastTestDate
//            add(Calendar.DAY_OF_YEAR, 7) // Add 7 days for the next test
//        }
//        val daysUntilNextTest = ((testCalendar.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
//        return daysUntilNextTest.coerceAtLeast(0) // Ensure it doesn't go below 0
//    }
//}

package com.example.mindboost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reminderTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        reminderTextView = view.findViewById(R.id.reminder)

        if (currentUser != null) {
            val userId = currentUser.uid
            loadUserNickname(userId, view)
            checkLastTestDateAndSetReminder(userId)
        }

        val navController = findNavController()
        view.findViewById<FrameLayout>(R.id.becksTest).setOnClickListener {
            navController.navigate(R.id.becksTestHomeFragment)
        }

        view.findViewById<FrameLayout>(R.id.phoneList).setOnClickListener {
            navController.navigate(R.id.phoneListFragment)
        }
    }

    private fun loadUserNickname(userId: String, view: View) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/nickname")
        databaseReference.get().addOnSuccessListener {
            val nickname = it.value as String?
            view.findViewById<TextView>(R.id.nickname).text = nickname ?: "User"
        }.addOnFailureListener {
            // Handle errors, e.g., logging or displaying a Toast
        }
    }

    private fun checkLastTestDateAndSetReminder(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/BeckTests")
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            val lastTestDate = dataSnapshot.children.map { it.key }.filterNotNull().maxOfOrNull {
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it) ?: Date(0)
            }
            setReminder(lastTestDate)
        }.addOnFailureListener {
            // Handle failure
        }
    }

    private fun setReminder(lastTestDate: Date?) {
        val today = Calendar.getInstance()
        lastTestDate?.let {
            val testCalendar = Calendar.getInstance().apply { time = it }
            // Add 7 days to the last test date
            testCalendar.add(Calendar.DAY_OF_MONTH, 8)

            // Calculate difference between today and the date of next expected test
            val daysUntilNextTest = daysBetween(today, testCalendar)

            reminderTextView.text = when {
//                daysUntilNextTest <= 0 -> "Od ponad 7 dni nie wykonałeś \ntestu na depresję! Zrób go już \nteraz, aby sprawdzić swoj stan!"
//                daysUntilNextTest == 1 -> "Test powinien zostać wykonany \nponownie jutro."
//                else -> "Test na depresję powinien\n zostać wykonany ponownie\n za $daysUntilNextTest dni."
                daysUntilNextTest <= 0 -> "Od ponad 7 dni nie wykonałeś testu na depresję! Zrób go już teraz, aby sprawdzić swoj stan."
                daysUntilNextTest == 1 -> "Test na depresję powinien zostać wykonany ponownie jutro, aby na bieżąco kontrolować swój aktualny stan."
                else -> "Za $daysUntilNextTest dni, wykonaj test na depresję ponownie, aby sprawdzić swój aktualny stan!"
            }
        } ?: run {
            reminderTextView.text = "Brak danych o ostatnim teście."
        }
    }

    private fun daysBetween(startCal: Calendar, endCal: Calendar): Int {
        val diff = endCal.timeInMillis - startCal.timeInMillis
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

}
