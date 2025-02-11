package com.example.trackingexpenses.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.trackingexpenses.R
import com.example.trackingexpenses.views.logInToTheApp.RegistrationScreen
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        setContent {
            TrackingExpensesTheme() {
                RegistrationScreen(this, ::createAccount)
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.registration_success),
                        Toast.LENGTH_SHORT,
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 2000)
                } else {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.registration_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}