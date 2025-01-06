package com.example.servicesync.screens

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar

class CustomerDetails : AppCompatActivity() {

    private lateinit var rootView: View
    private lateinit var updateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var nameField: EditText
    private lateinit var phoneField: EditText
    private lateinit var cityField: EditText
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_details)

        rootView = findViewById(android.R.id.content)
        val toolbar: Toolbar = findViewById(R.id.toolbar5)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        updateButton = findViewById(R.id.button)
        cancelButton = findViewById(R.id.button2)
        nameField = findViewById(R.id.nameTXT)
        phoneField = findViewById(R.id.mobileTXT)
        cityField = findViewById(R.id.cityField)

        // Initially hide the buttons
        updateButton.visibility = Button.INVISIBLE
        cancelButton.visibility = Button.INVISIBLE

        val back = findViewById<ImageButton>(R.id.backBTN)
        back.setOnClickListener {
            if (!isEditMode) {
                finish()
            }
        }

        // Get the customer details from the intent
        val customerName = intent.getStringExtra("customer_name")
        val customerPhoneNumber = intent.getStringExtra("customer_phone")
        val customerCity = intent.getStringExtra("customer_city")

        // Set the customer details to the respective EditText fields
        nameField.setText(customerName)
        phoneField.setText(customerPhoneNumber)
        cityField.setText(customerCity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.customer_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Disable menu items when in edit mode
        menu.findItem(R.id.editcustomer).isEnabled = !isEditMode
        menu.findItem(R.id.deletecustomer).isEnabled = !isEditMode
        menu.findItem(R.id.services).isEnabled = !isEditMode
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (isEditMode) {
            return true // Disable menu items when in edit mode
        }
        return when (item.itemId) {
            R.id.callCustomerBTN -> {
                val customerPhoneNumber = phoneField.text.toString()
                if (customerPhoneNumber.isNotEmpty()) {
                    directCallService(customerPhoneNumber)
                    Toast.makeText(this, "Phone number is Calling . . . . .", Toast.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(rootView, "Enter Phone Number", Snackbar.LENGTH_LONG).show()
                }
                true
            }

            R.id.editcustomer -> {
                Toast.makeText(this, "Edit Customer", Toast.LENGTH_SHORT).show()
                // Unlock all EditText fields
                unlockEditTextFields()
                // Show buttons
                updateButton.visibility = Button.VISIBLE
                cancelButton.visibility = Button.VISIBLE

                updateButton.setOnClickListener {
                    showSuccessDialog(
                        title = "Update Successfully",
                        message = "Details has been updated successfully.",
                        btnText = "Ok",
                        imageResId = R.drawable.success)
                }

                cancelButton.setOnClickListener {
                    showDialogConfirmationDialog()
                }
                // Set edit mode flag
                isEditMode = true
                true
            }

            R.id.deletecustomer -> {
                showConfirmDeleteCustomer()
                Toast.makeText(this, "Delete Customer", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.services -> {
                Toast.makeText(this, "Services ", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSuccessDialog(title: String, message: String, btnText: String, imageResId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
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
            isEditMode = false
            lockEditTextFields() // Call this method to lock the fields again
            updateButton.visibility = Button.INVISIBLE
            cancelButton.visibility = Button.INVISIBLE
        }

        dialog.show()
    }

    private fun unlockEditTextFields() {
        val editTextIds = listOf(R.id.nameTXT, R.id.mobileTXT, R.id.emailField, R.id.dateTXT, R.id.cityField)
        editTextIds.forEach { id ->
            findViewById<EditText>(id).apply {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
            }
        }
    }

    private fun lockEditTextFields() {
        val editTextIds = listOf(R.id.nameTXT, R.id.mobileTXT, R.id.emailField, R.id.dateTXT, R.id.cityField)
        editTextIds.forEach { id ->
            findViewById<EditText>(id).apply {
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
        }
        isEditMode = false
    }

    private fun showDialogConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Oops!")
            .setMessage("Changes cannot be saved!")
            .setPositiveButton("Ok") { dialog, _ ->
                updateButton.visibility = Button.INVISIBLE
                cancelButton.visibility = Button.INVISIBLE
                lockEditTextFields()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onBackPressed() {
        if (isEditMode) {
            Toast.makeText(this, "Please use the Cancel button to exit edit mode", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    private fun showConfirmDeleteCustomer() {
        AlertDialog.Builder(this)
            .setTitle("Confirm !")
            .setMessage("Are you sure to delete customer?")
            .setPositiveButton("Yes") { _, _ ->
                showDeletionSuccessDialog()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showDeletionSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null).apply {
            findViewById<ImageView>(R.id.successIcon).setImageResource(R.drawable.success)
            findViewById<TextView>(R.id.successTitle).text = "Success"
            findViewById<TextView>(R.id.successMessage).text = "Customer deleted successfully!"
            findViewById<Button>(R.id.okButton).text = "Ok"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.okButton).setOnClickListener {
            dialog.dismiss()
            finish()
            startActivity(Intent(this, HomePage::class.java))
        }
        dialog.show()
    }

    private fun directCallService(customerPhoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$customerPhoneNumber")
                startActivity(callIntent)
            } catch (e: SecurityException) {
                Toast.makeText(this, "Failed to place the call", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to place the call", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Request the CALL_PHONE permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the call
                val customerPhoneNumber = phoneField.text.toString()
                if (customerPhoneNumber.isNotEmpty()) {
                    directCallService(customerPhoneNumber)
                }
            } else {
                // Permission denied

                Snackbar.make(rootView, "Permission to make calls was denied", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val REQUEST_CALL_PERMISSION = 1
    }
}
