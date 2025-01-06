package com.example.servicesync.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import java.util.Calendar

class AddCustomer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        val saveButton = findViewById<Button>(R.id.saveBTN)
        val cancelButton = findViewById<Button>(R.id.cancelBTN)
        val date = findViewById<ImageButton>(R.id.datePickerBTN)

        saveButton.setOnClickListener {

            showSuccessDialog(
                title = "Added Successfully",
                message = "Customer Details added Successfully !",
                btnText = "Ok",
                imageResId = R.drawable.success // Make sure this drawable exists in your resources
            )
            Toast.makeText(applicationContext, "Customer Added Successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CustomerDetails::class.java))
        }

        cancelButton.setOnClickListener {
            Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }

        date.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showSuccessDialog(title: String, message: String, btnText: String, imageResId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Makes it so the dialog can only be closed intentionally by an action
            .create()

        // Set the title, message, and image
        val titleView = dialogView.findViewById<TextView>(R.id.successTitle)
        val messageView = dialogView.findViewById<TextView>(R.id.successMessage)
        val imageView = dialogView.findViewById<ImageView>(R.id.successIcon)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        titleView.text = title
        messageView.text = message
        okButton.text = btnText
        imageView.setImageResource(imageResId)

        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDatePickerDialog() {

        val dateText = findViewById<EditText>(R.id.dateField)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->

            val formattedDate = String.format("%02d/%02d/%04d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
            dateText.setText(formattedDate)
        }, year, month, day)

        // Show the DatePickerDialog
        datePickerDialog.show()
    }
}
