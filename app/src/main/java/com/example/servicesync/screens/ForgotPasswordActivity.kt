package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var viaSMS: ConstraintLayout
    private lateinit var viaEmail: ConstraintLayout
    private lateinit var nextButton: Button

    private var selectedOption: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        nextButton = findViewById(R.id.nextOTPButton)
        viaSMS = findViewById(R.id.viaSMS)
        viaEmail = findViewById(R.id.viaEmail)

        viaSMS.setOnClickListener {
            viaSMS.setBackgroundResource(R.drawable.active_otpbox)
            viaEmail.setBackgroundResource(R.drawable.bg_box) // reset email background
            selectedOption = "via SMS"
        }

        viaEmail.setOnClickListener {
            viaEmail.setBackgroundResource(R.drawable.active_otpbox)
            viaSMS.setBackgroundResource(R.drawable.bg_box) // reset SMS background
            selectedOption = "via Email"
        }

        nextButton.setOnClickListener {
            if (selectedOption != null) {
                startActivity(Intent(this, OTPVerificationActivity::class.java))

            } else {
                startActivity(Intent(this, OTPVerificationActivity::class.java))
            }
        }
    }
}
