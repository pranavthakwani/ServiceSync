package com.example.servicesync.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.servicesync.R
import com.example.servicesync.screens.ChangePassword
import com.example.servicesync.screens.LoginScreen
import com.example.servicesync.screens.PersonalProfile

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val companyInfo = view.findViewById<ConstraintLayout>(R.id.companyInfo)
        val changePassword = view.findViewById<ConstraintLayout>(R.id.changePassword)
        val aboutus = view.findViewById<ConstraintLayout>(R.id.aboutus)
        val logout = view.findViewById<ConstraintLayout>(R.id.logout)

        companyInfo.setOnClickListener {
            startActivity(Intent(requireContext(), PersonalProfile::class.java))
        }

        changePassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePassword::class.java))
        }

        aboutus.setOnClickListener {
            Toast.makeText(requireContext(), "About us clicked", Toast.LENGTH_SHORT).show()
        }

        logout.setOnClickListener {
            showLogoutConfirmationDialog()
            Toast.makeText(requireContext(), "Log out clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm !")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(requireContext(), LoginScreen::class.java)
            startActivity(intent)
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }



}