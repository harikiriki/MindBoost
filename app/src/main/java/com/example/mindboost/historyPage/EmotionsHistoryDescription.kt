package com.example.mindboost.historyPage

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class EmotionsHistoryDescription : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotions_history_description)

        val emotionDateTextView = findViewById<TextView>(R.id.emotionDate)
        val emotionImageView = findViewById<ImageView>(R.id.emotionPicture)

        val editTextEmotion1 = findViewById<EditText>(R.id.editTextEmotion1)
        val editTextEmotion2 = findViewById<EditText>(R.id.editTextEmotion2)
        val editTextEmotion3 = findViewById<EditText>(R.id.editTextEmotion3)
        val editTextEmotion4 = findViewById<EditText>(R.id.editTextEmotion4)
        val editTextEmotion5 = findViewById<EditText>(R.id.editTextEmotion5)

        val saveButton = findViewById<Button>(R.id.saveBtn)

        val date = intent.getStringExtra("DATE") ?: ""
        val emotionState = intent.getStringExtra("EMOTION_STATE") ?: ""
        val answer1 = intent.getStringExtra("ANSWER1") ?: ""
        val answer2 = intent.getStringExtra("ANSWER2") ?: ""
        val answer3 = intent.getStringExtra("ANSWER3") ?: ""
        val answer4 = intent.getStringExtra("ANSWER4") ?: ""

        emotionDateTextView.text = "Oto zapis Twojego stanu z dnia: $date"
        val emotionId = resources.getIdentifier(
            "ic_${emotionState}_emoji", "drawable", packageName
        )
        emotionImageView.setImageResource(emotionId)

        editTextEmotion1.setText(answer1)
        editTextEmotion2.setText(answer2)
        editTextEmotion3.setText(answer3)
        editTextEmotion4.setText(answer4)

        saveButton.setOnClickListener {
            val updatedAnswer1 = editTextEmotion1.text.toString()
            val updatedAnswer2 = editTextEmotion2.text.toString()
            val updatedAnswer3 = editTextEmotion3.text.toString()
            val updatedAnswer4 = editTextEmotion4.text.toString()
            val answer5 = editTextEmotion5.text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users/${user.uid}/EmotionDiary/$date")
                val updateMap = mapOf(
                    "answer1" to updatedAnswer1,
                    "answer2" to updatedAnswer2,
                    "answer3" to updatedAnswer3,
                    "answer4" to updatedAnswer4,
                    "answer5" to answer5
                )
                databaseReference.updateChildren(updateMap)
            }
            finish()
        }


        val pdfButton = findViewById<ImageButton>(R.id.pdfBtn)
        pdfButton.setOnClickListener {
            createPdf()
        }
    }

    private fun createPdf() {
        val date = intent.getStringExtra("DATE") ?: ""
        val questions = listOf(
            findViewById<TextView>(R.id.question1).text.toString(),
            findViewById<TextView>(R.id.question2).text.toString(),
            findViewById<TextView>(R.id.question3).text.toString(),
            findViewById<TextView>(R.id.question4).text.toString(),
            findViewById<TextView>(R.id.question5).text.toString(),
        )
        val answers = listOf(
            findViewById<EditText>(R.id.editTextEmotion1).text.toString(),
            findViewById<EditText>(R.id.editTextEmotion2).text.toString(),
            findViewById<EditText>(R.id.editTextEmotion3).text.toString(),
            findViewById<EditText>(R.id.editTextEmotion4).text.toString(),
            findViewById<EditText>(R.id.editTextEmotion5).text.toString()
        )

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 16f
            style = Paint.Style.FILL
        }
        paint.textSize = 12f

        var pageNumber = 1  // Zmienna do śledzenia numeru strony
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 25f // Koordynaty Y, od których zaczniemy pisać tekst

        canvas.drawText("Zapis stanu emocjonalnego z dnia: $date", 10f, y, titlePaint)
        y += titlePaint.textSize + 10 // Dodajemy miejsce pod tytułem

        questions.zip(answers).forEachIndexed { index, (question, answer) ->
            var result = drawText(canvas, "Pytanie ${index + 1}: $question", 10f, y, pageInfo.pageWidth - 20f, paint)
            y = result.second + paint.textSize + 10
            if (y > pageInfo.pageHeight - 50f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 25f
            }
            result = drawText(canvas, "Odpowiedź: $answer", 10f, y, pageInfo.pageWidth - 20f, paint)
            y = result.second + paint.textSize + 10
        }

        pdfDocument.finishPage(page)
        savePdfFile(pdfDocument, date)
    }

    private fun drawText(canvas: Canvas, text: String, x: Float, startY: Float, maxWidth: Float, paint: Paint): Pair<Boolean, Float> {
        val words = text.split(" ")
        var line = ""
        var y = startY
        var didWrap = false

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val bounds = Rect()
            paint.getTextBounds(testLine, 0, testLine.length, bounds)
            if (bounds.width() > maxWidth) {
                if (line.isNotEmpty()) {
                    canvas.drawText(line, x, y, paint)
                    line = word
                    y += bounds.height() + 10f
                    didWrap = true
                }
            } else {
                line = testLine
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, y, paint)
            y += paint.descent() - paint.ascent()
        }
        return Pair(didWrap, y)
    }

    private fun savePdfFile(pdfDocument: PdfDocument, date: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Emotional_State_$date.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString())
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        try {
            uri?.let {
                contentResolver.openOutputStream(it).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                Toast.makeText(this, "PDF został zapisany w Dokumentach", Toast.LENGTH_LONG).show()
            } ?: throw IOException("Nie udało się stworzyć URI dla dokumentu")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Wystąpił błąd podczas zapisu PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }
}
