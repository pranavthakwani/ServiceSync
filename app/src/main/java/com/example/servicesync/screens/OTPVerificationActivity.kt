package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servicesync.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import java.util.concurrent.TimeUnit

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var resendOTP: TextView
    private lateinit var otpTextView: OtpTextView
    private lateinit var verifyButton: Button
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var progressLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        firebaseAuth = FirebaseAuth.getInstance()
        otpTextView = findViewById(R.id.otp_view)
        resendOTP = findViewById(R.id.resendOTPTXT)
        verifyButton = findViewById(R.id.verifyOtpButton)
        progressLayout = findViewById(R.id.progressLayout)

        verifyButton.isEnabled = false
        progressLayout.visibility = View.GONE

        storedVerificationId = intent.getStringExtra("storedVerificationId").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        verifyButton.setOnClickListener {
            val otp = otpTextView.otp
            if (otp != null) {
                if (otp.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                    showProgress()
                    signInWithPhoneAuthCredential(credential)
                } else {
                    showToast("Please enter the OTP")
                }
            }
        }

        resendOTP.setOnClickListener {
            showToast("OTP has been resent")
            resendVerificationCode(phoneNumber, resendToken)
        }

        otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // Do nothing on interaction
            }

            override fun onOTPComplete(otp: String) {
                // Verify the OTP with Firebase
                if (otp.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                    showProgress()
                    signInWithPhoneAuthCredential(credential)
                } else {
                    showToast("Invalid OTP. Please try again.")
                    otpTextView.showError()
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            hideProgress()
            if (task.isSuccessful) {
                otpTextView.showSuccess()
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish()
            } else {
                otpTextView.showError()
                showToast("Verification failed: ${task.exception?.message}")
            }
        }
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken) {
        showProgress()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    hideProgress()
                    showToast("Verification failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    showToast("OTP has been resent") // Notify user
                    storedVerificationId = verificationId
                    resendToken = token
                    hideProgress()
                }
            })
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun showProgress() {
        progressLayout.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressLayout.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
