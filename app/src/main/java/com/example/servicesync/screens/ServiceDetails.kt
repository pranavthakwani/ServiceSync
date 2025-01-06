package com.example.servicesync.screens

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ServiceDetails : AppCompatActivity() {

    private lateinit var rootView: View
    private var isEditMode = false
    private lateinit var scrollView: ScrollView
    private lateinit var updateServiceButton: Button
    private lateinit var cancelServiceButton: Button
    private lateinit var editDate: ImageButton
    private lateinit var editDueDate: ImageButton
    private lateinit var editDateField: EditText
    private lateinit var editDueDateField: EditText
    private lateinit var dueDateEdit: EditText
    private lateinit var mobileNO: EditText
    private lateinit var back: ImageButton
    private lateinit var name : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_details)

        rootView = findViewById(android.R.id.content)
        back = findViewById(R.id.backButton)
        with(findViewById<Toolbar>(R.id.
        toolbar7)) {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""
        }

        val companyName = intent.getStringExtra("companyName")
        val serviceDetailsTextView: EditText = findViewById(R.id.NameViewField)

        Snackbar.make(rootView, "Company name ==  $companyName", Snackbar.LENGTH_LONG).show()
        if (companyName != null) {
            serviceDetailsTextView.setText(companyName)
        }

        bindViews()
        setupListeners()
        toggleEditMode(false)
        populateFields()
    }

    private fun bindViews() {
        scrollView = findViewById(R.id.scrollViewService)
        updateServiceButton = findViewById(R.id.UpdateServiceBTN)
        cancelServiceButton = findViewById(R.id.cancelService)
        editDate = findViewById(R.id.editDateBTN)
        editDueDate = findViewById(R.id.editDueDateBTN)
        editDateField = findViewById(R.id.editDateField)
        editDueDateField = findViewById(R.id.editDueDateField)
        mobileNO = findViewById(R.id.editMobileNumberField)
        name = findViewById(R.id.NameViewField)
    }

    private fun setupListeners() {
        updateServiceButton.setOnClickListener {
            showSuccessDialog(title = "Update Successfully",
            message = "Service details has been updated successfully.",
            btnText = "Ok",
            imageResId = R.drawable.success)
            toggleEditMode(false)
        }

        cancelServiceButton.setOnClickListener {
            if (isEditMode) showCancelConfirmationDialog() else finish()
        }

        editDate.setOnClickListener { showDatePickerDialog(editDateField) }
        editDueDate.setOnClickListener { showDatePickerDialog(editDueDateField) }

        back.setOnClickListener {
            if (!isEditMode) onBackPressed()
        }
    }

    private fun showSuccessDialog(title: String, message: String, btnText: String, imageResId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

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

    private fun populateFields() {
        // Get the service details from the intent
        val serviceDate = intent.getStringExtra("service_date")
        val customerName = intent.getStringExtra("customer_name")
        val dueDate = intent.getStringExtra("due_date")

        if (intent.hasExtra("FROM_NOTIFICATION_ADAPTER")) {
            // Coming from NotificationAdapter, set the fields directly
            name.setText(customerName)
            editDateField.setText(serviceDate)
            editDueDateField.setText(dueDate)
        } else if (intent.hasExtra("FROM_INDIVIDUAL_CUSTOMER_SERVICES")) {
            // If intent is from IndividualCustomerServices, use data from individualCustomerServiceDetails.kt
            val individualServiceDate = intent.getStringExtra("individual_service_date")
            val individualCustomerName = intent.getStringExtra("individual_customer_name")
            val individualDueDate = intent.getStringExtra("individual_due_date")

            // Set the service details to the respective EditText fields
            editDateField.setText(individualServiceDate)
            editDueDateField.setText(individualDueDate)
            name.setText(individualCustomerName)
        } else {
            editDateField.setText(serviceDate)
            editDueDateField.setText(dueDate)
            name.setText(customerName)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.service_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (isEditMode) {
            return true // Disable menu items when in edit mode
        }
        return when (item.itemId) {
            R.id.callServiceBTN -> {
                val customerPhoneNumber = mobileNO.text.toString()
                if (customerPhoneNumber.isNotEmpty()) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL_PHONE)
                    } else {
                        directCallService(customerPhoneNumber)
                    }
                    true
                } else {
                    Snackbar.make(rootView, "Phone number is empty", Snackbar.LENGTH_LONG).show()
                    true
                }
            }
            R.id.editservice -> {
                toggleEditMode(!isEditMode)
                return true
            }
            R.id.deleteservice -> {
                showConfirmDeleteService()
                return true
            }
            R.id.download -> {
                Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.reminder -> {
                if (checkSmsPermission()) {
                    sendReminder()
                } else {
                    requestSmsPermission()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            AlertDialog.Builder(this)
                .setTitle("SMS Permission Required")
                .setMessage("This app needs SMS permission to send service reminders.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_SEND_SMS)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_SEND_SMS)
        }
    }

    private fun toggleEditMode(enable: Boolean) {
        isEditMode = enable
        val visibility = if (enable) View.VISIBLE else View.INVISIBLE
        listOf(updateServiceButton, cancelServiceButton, editDate, editDueDate).forEach {
            it.visibility = visibility
        }
        scrollView.isEnabled = enable
        enableChildViews(scrollView, enable)
        supportActionBar?.setDisplayHomeAsUpEnabled(!enable)
        back.isEnabled = !enable
        back.isClickable = !enable
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            editText.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cancel Edit?")
            .setMessage("Changes will not be saved.")
            .setPositiveButton("OK") { _, _ -> toggleEditMode(false) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showConfirmDeleteService() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this service?")
            .setPositiveButton("Yes") { _, _ -> showDeletionSuccessDialog() }
            .setNegativeButton("No", null)
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

    private fun enableChildViews(viewGroup: ViewGroup, enable: Boolean) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            child.isEnabled = enable
            if (child is ViewGroup) enableChildViews(child, enable)
        }
    }

    override fun onBackPressed() {
        if (isEditMode) {
            showCancelConfirmationDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date1 }
        val calendar2 = Calendar.getInstance().apply { time = date2 }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    private fun sendReminder() {
        val dueDateString = dueDateEdit.text.toString()
        val phoneNumber = mobileNO.text.toString()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dueDate: Date
        try {
            dueDate = dateFormat.parse(dueDateString) ?: throw IllegalArgumentException()
        } catch (e: Exception) {
            showAlertDialog("Please enter a valid due date")
            return
        }

        val calendar = Calendar.getInstance()
        val today = calendar.time
        val diffInMillis = dueDate.time - today.time
        val diffInDays = if (isSameDay(today, dueDate)) {
            0
        } else {
            TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt() + 1
        }


        Snackbar.make(rootView, "Days until due date: $diffInDays", Snackbar.LENGTH_LONG).show()

        val reminderMessage: String = when {
            diffInDays > 0 -> "Your service is due in $diffInDays days on $dueDateString"
            diffInDays == 0 -> "Your service is due today on $dueDateString"
            else -> {
                showAlertDialog("The due date is in the past")
                return
            }
        }

        scheduleSms(phoneNumber, reminderMessage)
    }

    private fun scheduleSms(phoneNumber: String, message: String) {
        val data = Data.Builder()
            .putString("PHONE_NUMBER", phoneNumber)
            .putString("MESSAGE", message)
            .build()

        val sendSmsWorkRequest = OneTimeWorkRequest.Builder(SendSmsWorker::class.java)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(sendSmsWorkRequest)
        Toast.makeText(this, "SMS sent to: $phoneNumber", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PERMISSION_REQUEST_SEND_SMS = 1
        private const val PERMISSION_REQUEST_CALL_PHONE = 2 // Unique code for call permission
    }


    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Oops!")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun directCallService(customerPhoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$customerPhoneNumber")
                startActivity(callIntent)
            } catch (e: SecurityException) {
                Toast.makeText(this, "Failed to place the call", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL_PHONE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val customerPhoneNumber = mobileNO.text.toString()
                    if (customerPhoneNumber.isNotEmpty()) {
                        directCallService(customerPhoneNumber)
                    }
                } else {
                    Toast.makeText(this, "Permission to make calls was denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
