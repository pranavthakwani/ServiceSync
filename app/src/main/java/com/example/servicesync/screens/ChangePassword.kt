package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar

class ChangePassword : AppCompatActivity() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var showCurrentPasswordButton: ImageButton
    private lateinit var showPasswordButton: ImageButton
    private lateinit var showConfirmPasswordButton: ImageButton
    private lateinit var confirmBTN: Button
    private lateinit var forgotPasswordTXT: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        currentPasswordEditText = findViewById(R.id.editTextTextPassword2)
        newPasswordEditText = findViewById(R.id.editTextTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextTextPassword3)
        showCurrentPasswordButton = findViewById(R.id.showCurrentPasswordBTN)
        showPasswordButton = findViewById(R.id.showPasswordBTN)
        showConfirmPasswordButton = findViewById(R.id.showConfirmPasswordBTN)
        confirmBTN = findViewById(R.id.confirmPasswordBTN)
        forgotPasswordTXT = findViewById(R.id.forgotPasswordTXT)

        forgotPasswordTXT.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }

        confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = s.toString()

                if (confirmPassword == newPassword) {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.bg_box)
                } else {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.error_otpbox)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        confirmBTN.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (newPassword == confirmPassword) {
                showSuccessDialog(
                    title = "Update Successfully!",
                    message = "Password has been updated successfully.",
                    btnText = "Ok",
                    imageResId = R.drawable.success // Make sure this drawable exists in your resources
                )
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Passwords do not match", Snackbar.LENGTH_SHORT).show()
            }
        }

        showCurrentPasswordButton.setOnClickListener {
            togglePasswordVisibility(currentPasswordEditText, showCurrentPasswordButton)
        }

        showPasswordButton.setOnClickListener {
            togglePasswordVisibility(newPasswordEditText, showPasswordButton)
        }

        showConfirmPasswordButton.setOnClickListener {
            togglePasswordVisibility(confirmPasswordEditText, showConfirmPasswordButton)
        }
    }

    private fun showSuccessDialog(title: String, message: String, btnText: String, imageResId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val dialog = AlertDialog.Builder(this)
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
            finish()
        }
        dialog.show()
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        if (editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(R.drawable.showpassword)  // eye icon for hidden password
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            button.setImageResource(R.drawable.hidepassword)  // eye-off icon for visible password
        }
        editText.setSelection(editText.text.length)
    }
}
