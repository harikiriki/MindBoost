package com.example.mindboost

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class TestResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result)

        val score = intent.getIntExtra("SCORE", 0)
        val scoreTextView = findViewById<TextView>(R.id.testResult)
        val resultInfoTextView = findViewById<TextView>(R.id.resultInfo) // Dodaj ten element
        val resultInfoDescriptionTextView = findViewById<TextView>(R.id.resultInfoDescription) // Dodaj ten element


        scoreTextView.text = "$score punktów"

        // Ustawienie tekstu na podstawie wartości score
        val resultText = when {
            score in 0..11 -> "Brak depresji "
            score in 12..19 -> "Depresja łagodna"
            score in 20..25 -> "Depresja umiarkowana"
            score in 26..63 -> "Depresja ciężka"
            else -> "nieokreślony wynik"
        }
        val resultInfoDescription = when {
            score in 0..11 -> "\nPrawdopodobnie to tymczasowe pogorszenie nastroju, spowodowane bieżącymi wydarzeniami w Twoim życiu. \nJeśli przykre objawy będą utrzymywać się nadal, wykonaj ten test po 7 dniach i porównaj wyniki czy następuje pogorszenie czy poprawa."
            score in 12..19 -> "\nWynik w tym przedziale wskazuje na potrzebę udania się do psychologa lub psychoterapeuty w celu dalszej diagnostyki. \nŁagodne objawy depresyjne leczone są psychoterapią, bez konieczności włączania farmakoterapii. \nPsycholog/ psychoterapeuta w razie konieczności skieruje Cię do lekarza psychiatry. "
            score in 20..25 -> "\nPunktacja w tym przedziale sugeruje podjęcie szybkich działań i kontakt z psychologiem/psychoterapeutą lub psychiatrą. \nIstnieje prawdopodobieństwo włączenia leczenia farmakologicznego, przeciwdepresyjnego przez psychiatrę. Ważne aby oprócz działań farmakologicznych rozpocząć psychoterapię. \nTo warunkuje skuteczne leczenie depresji. "
            score in 26..63 -> "\nKonieczne jest udanie się do lekarza psychiatry. \nTo niebezpieczny stan dla zdrowia i życia, głównie gdy pojawiają się myśli samobójcze. Psychoterapia jest bardziej intensywna. \nW niektórych przypadkach koniczne jest leczenie szpitalne aby nie dopuścić do zagrożenia życia."
            else -> "nieokreślony wynik" // na wypadek, gdyby score wykraczał poza zakładane przedziały
        }

        resultInfoTextView.text = resultText
        resultInfoDescriptionTextView.text = resultInfoDescription
        saveResultToFirebase(score)


        val homePageButton = findViewById<Button>(R.id.homePageButton)
        homePageButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val phoneListButton = findViewById<Button>(R.id.phoneListActivityBtn)
        phoneListButton.setOnClickListener {
            navigateToPhoneListFragment()
        }

    }

    private fun navigateToPhoneListFragment() {
        val intent = Intent(this, Home::class.java) // Zastąp MainActivity nazwą aktywności zawierającej PhoneListFragment
        intent.putExtra("NAVIGATE_TO", "PhoneListFragment")
        startActivity(intent)
    }

    private fun saveResultToFirebase(score: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()

        if (user != null) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = dateFormat.format(Date())

            val resultData = mapOf(
                "score" to score,
                "date" to date
            )

            val resultRef = database.getReference("Users/${user.uid}/BeckTests/$date")
            resultRef.setValue(resultData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Zapisano wynik!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Błąd zapisu: ${it.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Użytkownik nie jest zalogowany.", Toast.LENGTH_LONG).show()
        }
    }
}
