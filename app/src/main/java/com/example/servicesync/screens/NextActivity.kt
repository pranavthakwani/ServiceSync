package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.servicesync.R

class NextActivity : AppCompatActivity() {

    private lateinit var nextBTN: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        nextBTN = findViewById(R.id.nextButton)
        nextBTN.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

    }
}