package com.example.mindboost

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mindboost.databinding.FragmentProfileBinding
import com.example.mindboost.dataclasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var profilePictureRef: StorageReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserUid = currentUser!!.uid
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserUid)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    database = dataSnapshot.ref
                    addValueEventListener()

                    val fileName = "profileImage_$currentUserUid.jpg"
                    profilePictureRef = FirebaseStorage.getInstance().getReference("profileImages/$fileName")
                    profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(uri).into(binding.recImage)
                    }
                } else {
                    // User not found in "Users" node
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.settings.setOnClickListener {
//            val intent = Intent(activity, SettingsActivity::class.java)
//            startActivity(intent)
        }


        binding.addPictureBtn.setOnClickListener {
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 1000)

        }

        binding.chooseAvatarBtn.setOnClickListener {
            val intent = Intent(activity, ChooseAvatarActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.buttonGoToEdit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.buttonDeleteAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Usuń konto")
                .setMessage("Czy na pewno chcesz usunąć konto? Ta akcja nie może zostać cofnięta.")
                .setPositiveButton("Usuń") { dialog, which ->
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(it.uid)
                        dbRef.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                it.delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(ContentValues.TAG, "User account deleted.")
                                            val intent = Intent(activity, Login::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            Log.w(ContentValues.TAG, "deleteUser:failure", task.exception)
                                            Toast.makeText(requireContext(), "Failed to delete account.",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Log.w(ContentValues.TAG, "deleteUserData:failure", task.exception)
                                Toast.makeText(requireContext(), "Failed to delete user data.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Anuluj", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val view: View = requireView()
        val profileImage: ImageView = view.findViewById(R.id.recImage)

        if(requestCode == 1000 ){
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data!!.data
                if (imageUri != null) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid)
                    val currentUserUid = user!!.uid
                    val fileName = "profileImage_$currentUserUid.jpg"
                    storageReference = FirebaseStorage.getInstance().getReference("profileImages/$fileName")
                    storageReference.putFile(imageUri).addOnSuccessListener{
                        Toast.makeText(requireContext(), "Poprawnie dodano zdjęcie!", Toast.LENGTH_SHORT).show()

                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            Picasso.get().load(uri).into(profileImage)
                        }

                    }.addOnFailureListener{
                        Toast.makeText(requireContext(), "Coś poszło nie tak!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addValueEventListener() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(User::class.java)
                updateProfileUI(userData)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Database error: ${databaseError.message}")
            }
        })
    }

    private fun updateProfileUI(userData: User?) {
        var nickname = userData?.nickname.toString()
        var email = userData?.email.toString()
        var birthDate = userData?.birthDate.toString()
        var gender = userData?.gender.toString()

        binding.nickname.text = nickname
        binding.email.text = email
        binding.birthdate2.text = birthDate
        binding.gender2.text = gender
    }
}
