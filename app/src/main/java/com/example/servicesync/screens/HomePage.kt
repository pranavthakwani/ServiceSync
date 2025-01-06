package com.example.servicesync.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.servicesync.R
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Fragment controller
        var NavController = findNavController(R.id.fragment)
        var bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomnav.setupWithNavController(NavController)

    }
}
