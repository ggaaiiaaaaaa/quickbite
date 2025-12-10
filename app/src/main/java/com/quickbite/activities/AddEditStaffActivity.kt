package com.quickbite.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.R
import com.quickbite.databinding.ActivityAddEditStaffBinding
import com.quickbite.models.User

class AddEditStaffActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditStaffBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val roles = arrayOf("Manager", "Kitchen Staff", "Cashier")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        binding.spinnerStaffRole.adapter = adapter

        binding.btnSaveStaff.setOnClickListener {
            val name = binding.etStaffName.text.toString().trim()
            val email = binding.etStaffEmail.text.toString().trim()
            val password = binding.etStaffPassword.text.toString().trim()
            val role = binding.spinnerStaffRole.selectedItem.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val user = User(firebaseUser!!.uid, name, email, role)
                            firestore.collection("users").document(firebaseUser.uid)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Staff member added successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
