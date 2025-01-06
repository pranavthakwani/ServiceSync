package com.example.servicesync.screens

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar

class SetNewPassword : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var setNewPasswordButton: Button
    private lateinit var showNewPassword: ImageButton
    private lateinit var showConfirmNewPassword: ImageButton
    private lateinit var newPasswordField: EditText
    private lateinit var confirmNewPasswordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_password)

        backButton = findViewById(R.id.backNewPassword)
        setNewPasswordButton = findViewById(R.id.setNewPassword)
        showNewPassword = findViewById(R.id.showNewPasswordBTN)
        showConfirmNewPassword = findViewById(R.id.showConfirmNewpasswordBTN)
        newPasswordField = findViewById(R.id.newPasswordField)
        confirmNewPasswordField = findViewById(R.id.confirmNewPasswordField)

        backButton.setOnClickListener {
            finish()
        }

        showNewPassword.setOnClickListener {
            togglePasswordVisibility(newPasswordField, showNewPassword)
        }

        showConfirmNewPassword.setOnClickListener {
            togglePasswordVisibility(confirmNewPasswordField, showConfirmNewPassword)
        }

        confirmNewPasswordField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newPasswordText = newPasswordField.text.toString()
                val confirmNewPasswordText = s.toString()

                if (confirmNewPasswordText == newPasswordText) {
                    confirmNewPasswordField.setBackgroundResource(R.drawable.bg_box)
                } else {
                    confirmNewPasswordField.setBackgroundResource(R.drawable.error_otpbox)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        setNewPasswordButton.setOnClickListener {
            val newPasswordText = newPasswordField.text.toString()
            val confirmNewPasswordText = confirmNewPasswordField.text.toString()

            if (newPasswordText == confirmNewPasswordText) {
                showSuccessDialog(
                    title = "Successfully",
                    message = "Password has been reset Successfully!",
                    btnText = "Ok",
                    imageResId = R.drawable.success
                )
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Passwords do not match", Snackbar.LENGTH_SHORT).show()
            }
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
