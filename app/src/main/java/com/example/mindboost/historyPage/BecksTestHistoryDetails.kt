package com.example.mindboost.historyPage

import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mindboost.adapters.BecksTestHistoryDetailsAdapter
import com.example.mindboost.dataclasses.BecksTestHistoryDetail
import com.example.mindboost.dataclasses.Odpowiedz
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import android.provider.Settings
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.mindboost.R


class BecksTestHistoryDetails : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BecksTestHistoryDetailsAdapter
    private val detailsList = mutableListOf<BecksTestHistoryDetail>()
    private lateinit var testDateTextView: TextView
    private var testDate: String? = null
    companion object {
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_becks_test_history_details)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = BecksTestHistoryDetailsAdapter(detailsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        testDateTextView = findViewById(R.id.testDate)
        findViewById<ImageButton>(R.id.pdfBtn).setOnClickListener {
            showCreatePdfDialog()
        }

        testDate = intent.getStringExtra("TEST_DATE") // Pobierz datę testu z Intentu
        testDate?.let {
            testDateTextView.text = "Oto udzielone przez Ciebie odpowiedzi na poszczególne pytania w dniu: $it."
        }
        loadTestDetails(testDate)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }

        if (Environment.isExternalStorageManager()) {
            // Uprawnienia zostały już przyznane
        } else {
            // Prośba o uprawnienia
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }

    }

    private fun loadTestDetails(testDate: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && testDate != null) {
            val userId = currentUser.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users/$userId/BeckTests/$testDate")
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                for (i in 1..20) { // Zakładając, że mamy 20 pytań
                    val questionRef = FirebaseDatabase.getInstance().getReference("testy/test1/pytania/pytanie$i")
                    questionRef.get().addOnSuccessListener { questionSnapshot ->
                        val question = questionSnapshot.child("tresc").getValue(String::class.java)
                        val score = dataSnapshot.child("question${i}score").getValue(Int::class.java) ?: 0

                        // Teraz pobieramy odpowiedź na podstawie punktów.
                        val answerRef = questionSnapshot.child("odpowiedzi/odpowiedz${score + 1}")
                        val answerObject = answerRef.getValue(Odpowiedz::class.java) // Tworzymy obiekt odpowiedzi
                        val answer = answerObject?.tresc

                        if (question != null && answer != null) {
                            detailsList.add(BecksTestHistoryDetail(question, answer))
                            adapter.notifyDataSetChanged()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Nie udało się załadować pytań.", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Nie udało się załadować detali testu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPdf(testResults: List<BecksTestHistoryDetail>, testDate: String) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        var y = 25f // Koordynaty Y, od których zaczniemy pisać tekst
        paint.textSize = 12f

        // Ustawienia dla tytułu
        val titlePaint = Paint()
        titlePaint.textSize = 16f
        titlePaint.style = Paint.Style.FILL

        // Tworzenie pierwszej strony
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Tytuł dokumentu
        canvas.drawText("Wynik testu Becka z dnia: $testDate", 10f, y, titlePaint)
        y += titlePaint.textSize + 10 // Dodajemy miejsce pod tytułem

        // Wypełnianie stron treścią
        testResults.forEachIndexed { index, result ->
            val questionText = "Pytanie ${index + 1}/20: ${result.question}"
            val answerText = "Odpowiedź: ${result.answer}"

            // Sprawdzanie, czy jest miejsce na pytanie i odpowiedź, jeśli nie - tworzymy nową stronę
            if (y + paint.descent() - paint.ascent() > pageInfo.pageHeight - 50f) {
                pdfDocument.finishPage(page)
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 25f
            }

            // Rysowanie pytania
            canvas.drawText(questionText, 10f, y, paint)
            y += paint.descent() - paint.ascent() + 5 // Dodajemy miejsce po pytaniu

            // Rysowanie odpowiedzi
            canvas.drawText(answerText, 10f, y, paint)
            y += paint.descent() - paint.ascent() + 15 // Dodajemy miejsce po odpowiedzi
        }

        // Kończenie strony i zapisywanie dokumentu
        pdfDocument.finishPage(page)
        savePdfFile(pdfDocument, testDate)
    }

    private fun savePdfFile(pdfDocument: PdfDocument, testDate: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Wynik_testu_Becka_$testDate.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        try {
            uri?.let {
                resolver.openOutputStream(it).use { outputStream ->
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

    private fun showCreatePdfDialog() {
        AlertDialog.Builder(this)
            .setTitle("Generowanie PDF")
            .setMessage("Czy chcesz wygenerować PDF z wynikami testu?")
            .setPositiveButton("Tak") { dialog, which ->
                // Pobieramy tekst z TextView jako datę testu i przekazujemy do funkcji createPdf
                val testDateText = testDateTextView.text.toString().substringAfter("dniu: ").trim()
                createPdf(detailsList, testDateText)
            }
            .setNegativeButton("Nie") { dialog, which ->
                // Zamknięcie dialogu, nic więcej nie rób
                dialog.dismiss()
            }
            .show()
    }
}
