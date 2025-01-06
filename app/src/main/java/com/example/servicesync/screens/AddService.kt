// AddService.kt
package com.example.servicesync.screens

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.servicesync.R
import com.example.servicesync.fragments.CustomerModel
import com.example.servicesync.fragments.Customer_serviceViewAdapter
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.kernel.pdf.PdfName.r
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.UnitValue
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class AddService : AppCompatActivity() {

    private val CREATE_FILE = 1
    private val SHARE_PDF = 2
    private lateinit var customerAdapter: Customer_serviceViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceDateEditText: EditText
    private lateinit var dueDateEditText: EditText
    private lateinit var phoneNumberText: EditText
    private lateinit var companyNameField: EditText
    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)

        val saveButton = findViewById<Button>(R.id.saveServiceBTN)
        val cancelButton = findViewById<Button>(R.id.cancelServiceBTN)
        val serviceDateButton = findViewById<ImageButton>(R.id.serviceDatePicker)
        val dueDatePickerButton = findViewById<ImageButton>(R.id.dueDatePicker)
        serviceDateEditText = findViewById(R.id.serviceDate)
        dueDateEditText = findViewById(R.id.serviceDueDate)
        phoneNumberText = findViewById(R.id.mobileNumberField)
        companyNameField = findViewById(R.id.companyName)
        val total = findViewById<EditText>(R.id.totalhours)
        val load = findViewById<EditText>(R.id.load)
        val unload = findViewById<EditText>(R.id.unload)
        val unloadPer = findViewById<EditText>(R.id.unloadPercentage)

        unload.isEnabled = false
        unload.isFocusable = false
        unload.isFocusableInTouchMode = false

        unloadPer.isEnabled = false
        unloadPer.isFocusable = false
        unloadPer.isFocusableInTouchMode = false

        saveButton.setOnClickListener {
            if (checkSmsPermission()) {
                if (areFieldsValid()) {
                    setReminders()
                    createPDFFile(companyNameField.text.toString())
                }
            } else {
                requestSmsPermission()
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }

        serviceDateButton.setOnClickListener {
            showDatePicker(serviceDateEditText)
        }

        dueDatePickerButton.setOnClickListener {
            showDatePicker(dueDateEditText)
        }

        val customers = listOf(
            CustomerModel("John Doe", "1234567890", "New York"),
            CustomerModel("Jane Smith", "0987654321", "Los Angeles"),
            CustomerModel("Dhaval", "9879732574", "Rajkot"),
            CustomerModel("Pandit", "7878787878", "Vapi"),
            CustomerModel("Tiwari", "0987654321", "Vapi"),
            CustomerModel("Pranav", "0987654321", "Jamnagar"),
            CustomerModel("Sanjeev", "123321232", "Kutch")
        )

        recyclerView = findViewById(R.id.recyclerViewCustomers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        customerAdapter = Customer_serviceViewAdapter(customers) { customer, _ ->
            companyNameField.setText(customer.name)
            recyclerView.visibility = View.GONE // Hide the RecyclerView after clicking on a list item
        }


        recyclerView.adapter = customerAdapter

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                customerAdapter.filter(newText.orEmpty())
                toggleRecyclerViewVisibility()
                return true
            }
        })

        recyclerView.visibility = View.GONE

        load.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (total.text.isNotEmpty() && load.text.isNotEmpty()) {
                    try {
                        val totalValue = total.text.toString().toDouble()
                        val loadValue = load.text.toString().toDouble()
                        val unloadValue = totalValue - loadValue

                        if (unloadValue >= 0) {
                            unload.setText(unloadValue.toString())
                            val unloadPercentageValue = (unloadValue * 100) / totalValue
                            val formattedUnloadPercentage = String.format("%.2f", unloadPercentageValue)
                            unloadPer.setText("$formattedUnloadPercentage %")
                        } else {
                            unload.setText("")
                            unloadPer.setText("")
                        }
                    } catch (e: NumberFormatException) {
                        unload.setText("")
                        unloadPer.setText("")
                    }
                } else {
                    unload.setText("")
                    unloadPer.setText("")
                }
            }
        })
    }

    override fun onBackPressed() {
        recyclerView.visibility = View.GONE
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        recyclerView.visibility = View.GONE
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

    private fun createPDFFile(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "$fileName.pdf")
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                pdfUri = uri
                generatePDFInvoice(uri)
                sharePDF(uri)
                showSuccessDialog(
                    title = "Added Successfully",
                    message = "Service Details added Successfully!",
                    btnText = "Ok",
                    imageResId = R.drawable.success
                )
            }
        }
    }

    private fun generatePDFInvoice(uri: Uri) {
        try {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                val document = Document(pdfDocument)

                val paragraph = Paragraph("Invoice")
                document.add(paragraph)

                val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f, 3f)))
                table.addCell("ID")
                table.addCell("Description")
                table.addCell("Amount")

                for (i in 1..5) {
                    table.addCell(i.toString())
                    table.addCell("Item $i")
                    table.addCell("$${10 * i}")
                }

                document.add(table)
                document.close()
                println("PDF generated successfully") // Add this line for logging
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun sharePDF(uri: Uri?) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
        }
        startActivityForResult(Intent.createChooser(shareIntent, "Share PDF via"), SHARE_PDF)
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date1 }
        val calendar2 = Calendar.getInstance().apply { time = date2 }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isValidIndianPhoneNumber(phoneNumber: String): Boolean {
        val regex = Pattern.compile("^\\+91\\d{10}$")
        return regex.matcher(phoneNumber).matches()
    }

    private fun getDelayUntil12AM(dueDate: Date, daysOffset: Int): Long {
        val calendar = Calendar.getInstance().apply {
            time = dueDate
            add(Calendar.DAY_OF_MONTH, daysOffset)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        return if (delay > 0) delay else 0
    }

    private fun scheduleSmsAndNotification(phoneNumber: String, message: String, delayInMillis: Long) {
        val inputData = Data.Builder()
            .putString("PHONE_NUMBER", phoneNumber)
            .putString("MESSAGE", message)
            .build()

        val reminderRequest = OneTimeWorkRequestBuilder<SendSmsWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(reminderRequest)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (areFieldsValid()) {
                        setReminders()
                    }
                } else {
                    showAlertDialog("SMS permission denied")
                }
            }
            PERMISSION_REQUEST_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setReminders()
                } else {
                    showAlertDialog("Notification permission denied")
                }
            }
        }
    }

    private fun areFieldsValid(): Boolean {
        val phoneNumber = phoneNumberText.text.toString()
        val serviceDate = serviceDateEditText.text.toString()
        val dueDate = dueDateEditText.text.toString()
        val companyName = companyNameField.text.toString()

        return when {
            companyName.isEmpty() -> {
                showAlertDialog("Please enter the Customer Name")
                false
            }

            phoneNumber.isEmpty() -> {
                showAlertDialog("Please enter the phone number")
                false
            }
            serviceDate.isEmpty() -> {
                showAlertDialog("Please enter the service date")
                false
            }
            dueDate.isEmpty() -> {
                showAlertDialog("Please enter the due date")
                false
            }
            !isValidIndianPhoneNumber(phoneNumber) -> {
                showAlertDialog("Please enter a valid Indian phone number")
                false
            }
            else -> true
        }
    }

    private fun setReminders() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_POST_NOTIFICATIONS)
            return
        }
        val dueDateString = dueDateEditText.text.toString()
        val phoneNumber = phoneNumberText.text.toString()
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
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, "Days until due date: $diffInDays", Snackbar.LENGTH_LONG).show()

        val workManager = WorkManager.getInstance(applicationContext)

        when {
            diffInDays > 3 -> {
                showAlertDialog("The due date is too far in the future to schedule reminders")
            }
            diffInDays == 3 -> {
                scheduleSmsAndNotification(phoneNumber, "Your service is due in 3 days on $dueDateString", getDelayUntil12AM(dueDate, -3))
                scheduleSmsAndNotification(phoneNumber, "Your service is due in 2 days on $dueDateString", getDelayUntil12AM(dueDate, -2))
                scheduleSmsAndNotification(phoneNumber, "Your service is due tomorrow on $dueDateString", getDelayUntil12AM(dueDate, -1))
                scheduleSmsAndNotification(phoneNumber, "Your service is due today on $dueDateString", getDelayUntil12AM(dueDate, 0))
            }
            diffInDays == 2 -> {
                scheduleSmsAndNotification(phoneNumber, "Your service is due in 2 days on $dueDateString", getDelayUntil12AM(dueDate, -2))
                scheduleSmsAndNotification(phoneNumber, "Your service is due tomorrow on $dueDateString", getDelayUntil12AM(dueDate, -1))
                scheduleSmsAndNotification(phoneNumber, "Your service is due today on $dueDateString", getDelayUntil12AM(dueDate, 0))
            }
            diffInDays == 1 -> {
                scheduleSmsAndNotification(phoneNumber, "Your service is due tomorrow on $dueDateString", getDelayUntil12AM(dueDate, -1))
                scheduleSmsAndNotification(phoneNumber, "Your service is due today on $dueDateString", getDelayUntil12AM(dueDate, 0))
            }
            diffInDays == 0 -> {
                scheduleSmsAndNotification(phoneNumber, "Your service is due today on $dueDateString", getDelayUntil12AM(dueDate, 0))
            }
            else -> {
                showAlertDialog("The due date is in the past")
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            editText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun toggleRecyclerViewVisibility() {
        recyclerView.visibility = if (customerAdapter.itemCount > 0) View.VISIBLE else View.GONE
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
            finish()
        }

        dialog.show()
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Oops !")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        private const val PERMISSION_REQUEST_SEND_SMS = 1
        private const val PERMISSION_REQUEST_POST_NOTIFICATIONS = 2
    }
}
