package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import com.example.servicesync.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val login = findViewById<Button>(R.id.loginToHome)
        val forgotPassword = findViewById<TextView>(R.id.forgotPass)
        val signUp = findViewById<TextView>(R.id.signUpTXT)

        login.setOnClickListener {
            val email = binding.userTxt.text.toString()
            val password = binding.passTxt.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        startActivity(Intent(this, HomePage::class.java))
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidUserException) {
                            Snackbar.make(binding.root, "User not registered. Please sign up.", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(binding.root, "Incorrect password. Please try again.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Snackbar.make(binding.root, "Enter Email and password !!", Snackbar.LENGTH_SHORT).show()
            }
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, MobileNumberForOTP::class.java))
        }

        signUp.setOnClickListener {
            Toast.makeText(this, "SignUP clicked!", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, SignUp::class.java))
        }
    }
}
