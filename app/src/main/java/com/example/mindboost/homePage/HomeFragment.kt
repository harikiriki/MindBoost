package com.example.mindboost.homePage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mindboost.historyPage.FeelingsDescription
import com.example.mindboost.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
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

        setEmotionClickListeners(view)
        checkEmotionStateAndSetEmotions(view)

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

    private fun setEmotionClickListeners(view: View) {
        val emotionIds = listOf(
            R.id.joyRL, R.id.happinessRL, R.id.indifferenceRL, R.id.sadnessRL, R.id.cryRL,
            R.id.confidenceRL, R.id.angerRL, R.id.disappointmentRL, R.id.horrorRL, R.id.creativityRL
        )

        val emotionImageIds = listOf(
            R.drawable.ic_joy_emoji,
            R.drawable.ic_happiness_emoji,
            R.drawable.ic_indifference_emoji,
            R.drawable.ic_sadness_emoji,
            R.drawable.ic_cry_emoji,
            R.drawable.ic_confidence_emoji,
            R.drawable.ic_anger_emoji,
            R.drawable.ic_disappointment_emoji,
            R.drawable.ic_horror_emoji,
            R.drawable.ic_creativity_emoji
        )

        emotionIds.forEachIndexed { index, emotionId ->
            view.findViewById<RelativeLayout>(emotionId).setOnClickListener {
                navigateToFeelingsDescription(emotionImageIds[index])
            }
        }
    }

private fun navigateToFeelingsDescription(emotionImageId: Int) {
    // Sprawdź, czy dzisiejsza data znajduje się już w bazie danych
    val currentUser = firebaseAuth.currentUser
    currentUser?.let { user ->
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users/${user.uid}/EmotionDiary")
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        databaseReference.child(currentDate).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Jeśli istnieje wpis na dzisiejszą datę, wyłącz możliwość kliknięcia na emotki
                view?.let { nonNullView ->
                    setEmotionsDisabled(nonNullView)
                }
            } else {
                // Jeśli nie ma wpisu, pozwól na przechodzenie do aktywności
                val intent = Intent(context, FeelingsDescription::class.java).apply {
                    putExtra("EMOTION_IMAGE_ID", emotionImageId)
                }
                startActivity(intent)
            }
        }
    }
}

    private fun setEmotionsDisabled(view: View) {
        val emotionIds = listOf(
            R.id.joyRL, R.id.happinessRL, R.id.indifferenceRL, R.id.sadnessRL, R.id.cryRL,
            R.id.confidenceRL, R.id.angerRL, R.id.disappointmentRL, R.id.horrorRL, R.id.creativityRL
        )

        emotionIds.forEach { emotionId ->
            val emotionView = view.findViewById<RelativeLayout>(emotionId)
            emotionView.alpha = 0.3f // Przyciemnij wszystkie emotki
            emotionView.setOnClickListener(null) // Usuń możliwość kliknięcia na emotki
        }

        // Teraz zaznacz wybraną emotkę, która jest przypisana do dzisiejszego dnia
        // Zastanów się, jak przechowujesz związane z nią dane, aby móc ją odpowiednio zaznaczyć
        // Może to być np. ustawienie innej wartości alpha lub ramki wokół emotki
    }

    private fun checkEmotionStateAndSetEmotions(view: View) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users/${user.uid}/EmotionDiary")
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            databaseReference.child(currentDate).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    setEmotionsDisabled(view)
                    // Dodatkowo zaznacz wybraną emotkę, jeśli istnieje taka informacja w bazie danych
                    highlightSelectedEmotion(view, dataSnapshot)
                }
            }
        }
    }

    private fun highlightSelectedEmotion(view: View, dataSnapshot: DataSnapshot) {
        // Pobierz wartość 'emotionState' z dataSnapshot
        val selectedEmotionState = dataSnapshot.child("emotionState").getValue(String::class.java) ?: return

        // Mapowanie stanu emocji na identyfikator widoku
        val emotionToViewIdMap = mapOf(
            "joy" to R.id.joyRL,
            "happiness" to R.id.happinessRL,
            "indifference" to R.id.indifferenceRL,
            "horror" to R.id.horrorRL,
            "anger" to R.id.angerRL,
            "confidence" to R.id.angerRL,
            "cry" to R.id.cryRL,
            "creativity" to R.id.creativityRL,
            "disappointment" to R.id.disappointmentRL,
            "sadness" to R.id.sadnessRL
        )

        // Znajdź identyfikator widoku dla wybranej emotki
        val selectedViewId = emotionToViewIdMap[selectedEmotionState]

        // Wyróżnij wybraną emotkę
        selectedViewId?.let { id ->
            val selectedEmotionView = view.findViewById<RelativeLayout>(id)
            selectedEmotionView.alpha = 1.0f // Ustaw pełną widoczność dla wybranej emotki
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
            // Add 8 days to the last test date
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
