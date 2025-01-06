package com.example.servicesync.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import com.example.servicesync.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressLayout.visibility = View.GONE
        firebaseAuth = FirebaseAuth.getInstance()
        val getDOB = findViewById<ImageButton>(R.id.dobPickerBTN)

        getDOB.setOnClickListener {
            showDatePickerDialog()
        }

        binding.signUPbtn.setOnClickListener {
            val phoneNumber = binding.phoneTXT.text.toString()

            if (phoneNumber.isNotEmpty()) {
                startPhoneNumberVerification(phoneNumber)
            } else {
                Toast.makeText(this, "Enter Phone Number!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        binding.progressLayout.visibility = View.VISIBLE

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    binding.progressLayout.visibility = View.GONE
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    binding.progressLayout.visibility = View.GONE
                    Toast.makeText(this@SignUp, "Verification Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    binding.progressLayout.visibility = View.GONE
                    storedVerificationId = verificationId
                    resendToken = token
                    val intent = Intent(this@SignUp, OTPVerificationActivity::class.java)
                    intent.putExtra("storedVerificationId", storedVerificationId)
                    intent.putExtra("resendToken", resendToken)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val dobField = findViewById<EditText>(R.id.dateofBirthField)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
            dobField.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}
