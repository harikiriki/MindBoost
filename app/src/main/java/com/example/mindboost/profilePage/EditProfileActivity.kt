package com.example.mindboost.profilePage

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.R
import com.example.mindboost.databinding.ActivityEditProfileBinding
import com.example.mindboost.dataclasses.User
import com.example.mindboost.homePage.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference : DatabaseReference
    private val calendar = Calendar.getInstance()
    private lateinit var profileImage : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(User::class.java)
                getUserData(userData)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Database error: ${databaseError.message}")
            }
        })

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

        binding.buttonEditData.setOnClickListener {
            val nickname = binding.nickname.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val birthDate = binding.birthdate.text.toString()
            val gender = binding.gender.text.toString()

            if (nickname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && birthDate.isNotEmpty() && gender.isNotEmpty()) {
                if (email.contains('@')) {
                    val updates = mapOf<String, Any>(
                        "nickname" to nickname,
                        "email" to email,
                        "password" to password,
                        "birthDate" to birthDate,
                        "gender" to gender
                    )
                    currentUser.updateEmail(email)
                    currentUser.updatePassword(password)
                    databaseReference.updateChildren(updates)
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.backButton.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.birthdate).text = dateFormat.format(calendar.time)
    }

    private fun getUserData(userData: User?) {
        binding.nickname.text = Editable.Factory.getInstance().newEditable(userData?.nickname.toString())
        binding.email.text = Editable.Factory.getInstance().newEditable(userData?.email.toString())
        binding.password.text = Editable.Factory.getInstance().newEditable(userData?.password.toString())
        binding.birthdate.text = Editable.Factory.getInstance().newEditable(userData?.birthDate.toString())
        binding.gender.text = Editable.Factory.getInstance().newEditable(userData?.gender.toString())
    }

}