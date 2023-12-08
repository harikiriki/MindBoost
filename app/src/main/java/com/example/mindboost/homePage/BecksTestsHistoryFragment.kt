package com.example.mindboost.homePage

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.R
import com.example.mindboost.adapters.BecksTestsAdapter
import com.example.mindboost.dataclasses.BecksTestDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class BecksTestsHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BecksTestsAdapter
    private val becksTestsList = mutableListOf<BecksTestDetail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_becks_tests_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = BecksTestsAdapter(becksTestsList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        loadBecksTests()

        val pdfBtn = view.findViewById<ImageButton>(R.id.pdfBtn)
        pdfBtn.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Generowanie PDF")
        builder.setMessage("Czy chcesz wygenerować plik PDF z wszystkimi wynikami testów?")

        builder.setPositiveButton("Tak") { dialog, which ->
            createPdf(becksTestsList)
        }

        builder.setNegativeButton("Nie") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun loadBecksTests() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/BeckTests")
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                becksTestsList.clear() // Czyść listę przed dodaniem nowych elementów
                dataSnapshot.children.forEach { snapshot ->
                    val testDate = snapshot.child("date").getValue(String::class.java)
                    val testScore = snapshot.child("totalScore").getValue(Int::class.java)

                    val questionsAndAnswers = mutableListOf<Pair<String, String>>()
                    for (i in 1..20) {
                        val questionRef = FirebaseDatabase.getInstance().getReference("testy/test1/pytania/pytanie$i/tresc")
                        val answerIndex = snapshot.child("question${i}score").getValue(Int::class.java) ?: 0

                        questionRef.get().addOnSuccessListener { questionSnapshot ->
                            val questionText = questionSnapshot.getValue(String::class.java) ?: ""
                            val answerRef = FirebaseDatabase.getInstance().getReference("testy/test1/pytania/pytanie$i/odpowiedzi/odpowiedz${answerIndex + 1}/tresc")
                            answerRef.get().addOnSuccessListener { answerSnapshot ->
                                val answerText = answerSnapshot.getValue(String::class.java) ?: ""
                                questionsAndAnswers.add(Pair(questionText, answerText))
                                if (questionsAndAnswers.size == 20) {
                                    if (testDate != null && testScore != null) {
                                        becksTestsList.add(BecksTestDetail(testDate, testScore, questionsAndAnswers))
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    private fun createPdf(testResults: List<BecksTestDetail>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        var y = 25f
        paint.textSize = 12f

        val titlePaint = Paint()
        titlePaint.textSize = 16f
        titlePaint.style = Paint.Style.FILL

        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        canvas.drawText("Wszystkie wyniki testów Becka", 10f, y, titlePaint)
        y += titlePaint.textSize + 10

        testResults.forEach { testDetail ->
            val dateText = "Data: ${testDetail.date}"
            val scoreText = "Wynik: ${testDetail.score} punkty"
            val categoryText = "Kategoria: ${getDepressionCategory(testDetail.score)}"

            if (y + paint.descent() - paint.ascent() > pageInfo.pageHeight - 50f) {
                pdfDocument.finishPage(page)
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 25f
            }

            canvas.drawText(dateText, 10f, y, paint)
            y += paint.descent() - paint.ascent() + 5

            canvas.drawText(scoreText, 10f, y, paint)
            y += paint.descent() - paint.ascent() + 5

            canvas.drawText(categoryText, 10f, y, paint)
            y += paint.descent() - paint.ascent() + 15

            testDetail.questionsAndAnswers.forEachIndexed { index, (question, answer) ->
                val questionNumber = index + 1
                canvas.drawText("Pytanie $questionNumber/20: $question", 10f, y, paint)
                y += paint.descent() - paint.ascent() + 5
                canvas.drawText("Odpowiedź: $answer", 10f, y, paint)
                y += paint.descent() - paint.ascent() + 15

                if (y + paint.descent() - paint.ascent() > pageInfo.pageHeight - 50f) {
                    pdfDocument.finishPage(page)
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 25f
                }
            }
        }

        pdfDocument.finishPage(page)
        savePdfFile(pdfDocument)
    }

    private fun getDepressionCategory(score: Int): String {
        return when (score) {
            in 0..11 -> "brak depresji"
            in 12..19 -> "depresja łagodna"
            in 20..25 -> "depresja umiarkowana"
            in 26..63 -> "depresja ciężka"
            else -> "nieokreślony wynik"
        }
    }


    private fun savePdfFile(pdfDocument: PdfDocument) {
        val fileName = "Wszystkie_Wyniki_Testow_Becka_${System.currentTimeMillis()}.pdf"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        try {
            uri?.let {
                resolver.openOutputStream(it).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                Toast.makeText(context, "PDF został zapisany w Dokumentach", Toast.LENGTH_LONG).show()
            } ?: throw IOException("Nie udało się stworzyć URI dla dokumentu")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Wystąpił błąd podczas zapisu PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }

}

