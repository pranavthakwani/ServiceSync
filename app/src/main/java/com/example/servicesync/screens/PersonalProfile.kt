package com.example.servicesync.screens

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar

class PersonalProfile : AppCompatActivity() {
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_profile)

        val profileLayout = findViewById<ScrollView>(R.id.profileLayout)
        val editBTN = findViewById<ImageButton>(R.id.editProfileBTN)
        val back = findViewById<ImageButton>(R.id.backProfile)
        val update = findViewById<Button>(R.id.updateProfile)
        val cancel = findViewById<Button>(R.id.cancelProfile)

        // Disable all EditTexts inside profileLayout initially
        setEditable(profileLayout, false)
        update.visibility = Button.GONE
        cancel.visibility = Button.GONE

        editBTN.setOnClickListener {
            isEditMode = !isEditMode
            setEditable(profileLayout, isEditMode)
            update.visibility = if (isEditMode) Button.VISIBLE else Button.GONE
            cancel.visibility = if (isEditMode) Button.VISIBLE else Button.GONE
            val message = if (isEditMode) "Edit Mode enabled" else "Edit Mode disabled"
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }

        back.setOnClickListener {
            if (isEditMode) {
                showConfirmDialog()
            } else {
                finish()
            }
        }

        update.setOnClickListener {
            // Handle update logic here
            showSuccessDialog(
                title = "Update Successful",
                message = "Profile has been updated successfully.",
                btnText = "Ok",
                imageResId = R.drawable.success // Make sure this drawable exists in your resources
            )
            isEditMode = false
            setEditable(profileLayout, isEditMode)
            update.visibility = Button.GONE
            cancel.visibility = Button.GONE
        }

        cancel.setOnClickListener {
            if (isEditMode) {
                showConfirmDialog()
            }
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

    override fun onBackPressed() {
        if (isEditMode) {
            showConfirmDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun setEditable(viewGroup: ViewGroup, editable: Boolean) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view is EditText) {
                view.isFocusable = editable
                view.isFocusableInTouchMode = editable
                view.isClickable = editable
            } else if (view is ViewGroup) {
                setEditable(view, editable)
            }
        }
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Changes can't be changed. Do you want to discard changes?")
            .setPositiveButton("Yes") { dialog, id ->
                isEditMode = false
                val profileLayout = findViewById<ScrollView>(R.id.profileLayout)
                setEditable(profileLayout, isEditMode)
                findViewById<Button>(R.id.updateProfile).visibility = Button.GONE
                findViewById<Button>(R.id.cancelProfile).visibility = Button.GONE
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
