package com.example.mindboost.historyPage

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mindboost.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MoodChartFragment : Fragment() {

    private lateinit var lineChart: LineChart
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_chart, container, false)
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            lineChart = view.findViewById(R.id.chart)
            val user = FirebaseAuth.getInstance().currentUser
            val uid = user?.uid ?: "" // Użyj pustego stringu lub obsłuż brak zalogowanego użytkownika

            if (uid.isNotEmpty()) {
                database = FirebaseDatabase.getInstance().getReference("Users/$uid/EmotionDiary")
                setupChart()
                loadDataFromFirebase()
            } else {
                // Obsługa braku zalogowanego użytkownika
            }
        }

    private fun setupChart() {
        // Usuń legendę
        lineChart.legend.isEnabled = false

        // Usuń opis wykresu
        lineChart.description.isEnabled = false

        // Ustawienie osi X na dole
        lineChart.xAxis.setDrawAxisLine(false) // Jeśli nie chcesz linii osi
        lineChart.xAxis.setDrawGridLines(false) // Jeśli nie chcesz linii siatki
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.setDrawLabels(true) // Jeśli chcesz widzieć etykiety
        lineChart.xAxis.granularity = 1f // Wyświetlaj tylko wartości całkowite
        lineChart.xAxis.isGranularityEnabled = true // Włącz granulację

        // Ustawienie osi Y po lewej stronie zawsze wyświetlającej wszystkie etykiety
        lineChart.axisLeft.setDrawLabels(true) // Jeśli chcesz widzieć etykiety
        lineChart.axisLeft.setDrawAxisLine(false) // Jeśli nie chcesz linii osi
        lineChart.axisLeft.setDrawGridLines(false) // Jeśli nie chcesz linii siatki
        lineChart.axisLeft.granularity = 1f // Ustaw granulację, aby pasowała do twoich danych
        lineChart.axisLeft.isGranularityEnabled = true // Włącz granulację

        // Wyłączenie osi Y po prawej stronie
        lineChart.axisRight.isEnabled = false

        // Ustawienie wartości osi Y
        lineChart.axisLeft.valueFormatter = MyYAxisValueFormatter()
    }


    private fun loadDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sortedEntries = snapshot.children.mapNotNull { child ->
                    val dateStr = child.child("date").getValue(String::class.java)
                    val emotionState = child.child("emotionState").getValue(String::class.java)
                    if (dateStr != null && emotionState != null) {
                        try {
                            val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            Triple(date.toEpochDay(), getEmojiResourceId(emotionState), emotionState)
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                }.sortedBy { it.first }

                val entries = ArrayList<Entry>()
                var index = 1f
                sortedEntries.forEach { (_, yValue, emotionState) ->
                    val entry = Entry(index++, yValue)
                    val icon = getIconForEmotionState(emotionState)
                    icon?.let { entry.icon = it }
                    entries.add(entry)
                }

                val dataSet = LineDataSet(entries, "Emotion States")

                if (entries.isNotEmpty()) {
                    lineChart.data = LineData(dataSet)
                    lineChart.invalidate() // Odświeżenie wykresu
                } else {
                    Log.d("MoodChartFragment", "Brak wpisów do wyświetlenia na wykresie.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MoodChartFragment", "Błąd pobierania danych: ${error.message}")
            }
        })
    }


    private fun getDrawableIdForEmotionState(emotionState: String?): Int {
        return when (emotionState) {
            "joy" -> R.drawable.ic_joy_emoji
            "happiness" -> R.drawable.ic_happiness_emoji
            "indifference" -> R.drawable.ic_indifference_emoji
            "sadness" -> R.drawable.ic_sadness_emoji
            "cry" -> R.drawable.ic_cry_emoji
            "confidence" -> R.drawable.ic_confidence_emoji
            "anger" -> R.drawable.ic_anger_emoji
            "disappointment" -> R.drawable.ic_disappointment_emoji
            "horror" -> R.drawable.ic_horror_emoji
            "creativity" -> R.drawable.ic_creativity_emoji
            else -> R.drawable.ic_joy_emoji // Załóżmy, że masz domyślną ikonę
        }
    }

    private fun getIconForEmotionState(emotionState: String?): BitmapDrawable? {
        val id = getDrawableIdForEmotionState(emotionState)
        val drawable = ContextCompat.getDrawable(requireContext(), id)
        return drawable?.let {
            val bitmap = Bitmap.createScaledBitmap(
                (it as BitmapDrawable).bitmap,
                30, // Szerokość ikony
                30, // Wysokość ikony
                false
            )
            BitmapDrawable(resources, bitmap)
        }
    }

    private fun getEmojiResourceId(emotionState: String?): Float {
        return when (emotionState) {
            "joy" -> 10f
            "happiness" -> 9f
            "indifference" -> 8f
            "sadness" -> 7f
            "cry" -> 6f
            "confidence" -> 5f
            "anger" -> 4f
            "disappointment" -> 3f
            "horror" -> 2f
            "creativity" -> 1f
            else -> 0f
        }
    }

    class MyYAxisValueFormatter : ValueFormatter() {
        private val emotions = hashMapOf(
            10f to "radość",
            9f to "szczęście",
            8f to "obojętność",
            7f to "smutek",
            6f to "płacz",
            5f to "odwaga",
            4f to "złość",
            3f to "zawiedzenie",
            2f to "przerażenie",
            1f to "kreatywność",
        )

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return emotions[value] ?: value.toString()
        }
    }

}

