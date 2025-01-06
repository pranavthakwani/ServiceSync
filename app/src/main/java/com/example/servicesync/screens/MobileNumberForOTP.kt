package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.servicesync.R

class MobileNumberForOTP : AppCompatActivity() {

    private lateinit var nextButton: Button
    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_number_for_otp)

        nextButton = findViewById(R.id.nextMobileotpButton)
        backButton = findViewById(R.id.backMobileNumberButon)

        backButton.setOnClickListener {
            finish()
        }

        nextButton.setOnClickListener {
            startActivity(Intent(this, OTPVerificationActivity::class.java))
        }
    }
}
