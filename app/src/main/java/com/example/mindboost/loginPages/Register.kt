package com.example.mindboost.loginPages

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.mindboost.R
import com.example.mindboost.databinding.ActivityRegisterBinding
import com.example.mindboost.dataclasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class Register : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference : DatabaseReference
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        Locale.setDefault(Locale("pl", "PL"))

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

        binding.button.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val nickname = binding.nickname.text.toString()
            val birthDate = binding.birthdate.text.toString()
            val gender = binding.gender.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && nickname.isNotEmpty() && birthDate.isNotEmpty() && gender.isNotEmpty()) {
                if (email.contains('@')) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{authResult ->
                        if (authResult.isSuccessful) {
                            val uid = authResult.result.user!!.uid
                            val user = User(nickname, email, password, birthDate, gender, notifications = true)
                            databaseReference.child(uid).setValue(user).addOnCompleteListener{
                                if(it.isSuccessful) {
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                }
                            }
                        } else {
                            Toast.makeText(this, authResult.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Niewłaściwy e-mail!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Uzupełnij wszystkie pola!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.birthdate).text = dateFormat.format(calendar.time)
    }


}