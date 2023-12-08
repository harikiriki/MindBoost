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
                Log.d("MoodChartFragment", "Snapshot: $snapshot")

                val entries = ArrayList<Entry>()
                var xValue = 1f
                snapshot.children.forEach { child ->
                    val emotionState = child.child("emotionState").getValue(String::class.java)
                    val yValue = getEmojiResourceId(emotionState)
                    val icon = getIconForEmotionState(emotionState)
                    val entry = Entry(xValue++, yValue).apply {
                        icon?.let {
                            setIcon(it)
                        }
                    }
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
                40, // Szerokość ikony
                40, // Wysokość ikony
                false
            )
            BitmapDrawable(resources, bitmap)
        }
    }

    private fun getEmojiResourceId(emotionState: String?): Float {
        return when (emotionState) {
            "joy" -> 1f
            "happiness" -> 2f
            "indifference" -> 3f
            "sadness" -> 4f
            "cry" -> 5f
            "confidence" -> 6f
            "anger" -> 7f
            "disappointment" -> 8f
            "horror" -> 9f
            "creativity" -> 10f
            else -> 0f
        }
    }

    class MyYAxisValueFormatter : ValueFormatter() {
        private val emotions = hashMapOf(
            1f to "radość",
            2f to "szczęście",
            3f to "obojętność",
            4f to "smutek",
            5f to "płacz",
            6f to "odwaga",
            7f to "złość",
            8f to "zawiedzenie",
            9f to "przerażenie",
            10f to "kreatywność",
        )

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return emotions[value] ?: value.toString()
        }
    }

}

