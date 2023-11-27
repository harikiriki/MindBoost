package com.example.mindboost

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.databinding.ActivityCompleteGoogleSignInBinding
import com.example.mindboost.dataclasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

class CompleteGoogleSignInActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityCompleteGoogleSignInBinding
    private lateinit var databaseReference : DatabaseReference
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompleteGoogleSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Locale.setDefault(Locale("pl", "PL"))

        setupDatePicker()
        setupGenderPopup()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val name = intent.getStringExtra("name")
        val lastName = intent.getStringExtra("lastName")
        val email = intent.getStringExtra("email")

        binding.completionButton.setOnClickListener {
            saveUserData(name, lastName, email)
        }
    }

    private fun setupDatePicker() {
        binding.birthdate.setOnClickListener {
            val locale = Locale("pl", "PL")
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setButton(DatePickerDialog.BUTTON_POSITIVE, "Zatwierdź", this)
                setButton(DatePickerDialog.BUTTON_NEGATIVE, "Anuluj", this)
                context.resources.configuration.setLocale(locale)
                show()
            }
        }
    }

    private fun setupGenderPopup() {
        binding.gender.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.gender_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.male -> {
                        binding.gender.setText("Mężczyzna")
                        true
                    }
                    R.id.female -> {
                        binding.gender.setText("Kobieta")
                        true
                    }
                    R.id.other -> {
                        binding.gender.setText("Inna")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun saveUserData(nickname: String?, lastName: String?, email: String?) {
        val birthDate = binding.birthdate.text.toString().trim()
        val gender = binding.gender.text.toString().trim()

        if (birthDate.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Uzupełnij wszystkie pola!", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val user = User(nickname, email, "", birthDate, gender, notifications = true)
        databaseReference.child(uid).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error saving user details!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.birthdate).text = dateFormat.format(calendar.time)
    }
}
