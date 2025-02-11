package com.example.trackingexpenses.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.yml.charts.common.extensions.isNotNull
import com.example.trackingexpenses.R
import com.example.trackingexpenses.views.logInToTheApp.LoginScreen
import com.example.trackingexpenses.models.User
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        if (auth.currentUser.isNotNull()) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        setContent {
            TrackingExpensesTheme {
                LoginScreen(
                    onGoogleSignIn = { signInWithGoogle() },
                    onResetPassword = { email -> resetPassword(email, this, auth) },
                    onSignInWithEmail = { email, password ->
                        signInWithEmail(email, password, auth, this)
                    }
                )
            }
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 200)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        checkUserAndProceed(userId)
                    }
                } else {
                    Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserAndProceed(userId: String) {
        val db = Firebase.firestore
        val userDoc = db.collection("users").document(userId)

        userDoc.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val newUser = User(
                    first_login = true,
                    total_income = 0f,
                    total_expenditure = 0f,
                    expenses_for_the_period = 0f,
                    income_for_the_period = 0f,
                    expenses_for_day = 0f
                )

                userDoc.set(newUser).addOnSuccessListener {
                    addDefaultCategoriesForUser(userId)
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun addDefaultCategoriesForUser(userId: String) {
        addDefaultCategoriesForExpenditure(userId)
        addDefaultCategoriesForIncome(userId)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addDefaultCategoriesForExpenditure(userId: String) {
        Firebase.firestore.collection("categoriesOfExpenditure").document(userId)
            .set(mapOf("categoriesOfExpenditure" to R.array.expenditure_categories))
    }

    private fun addDefaultCategoriesForIncome(userId: String) {
        Firebase.firestore.collection("categoriesOfIncome").document(userId)
            .set(mapOf("categoriesOfIncome" to R.array.income_categories))
    }

    private fun resetPassword(email: String, context: Context, auth: FirebaseAuth) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    context, getString(R.string.password_reset_email_sent, email), Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, getString(R.string.error_message, task.exception?.message), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun signInWithEmail(
        email: String,
        password: String,
        auth: FirebaseAuth,
        context: Context,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener((context as Activity)) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        checkUserAndProceed(userId)
                    }
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.authentication_error),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google Sign-In failed", e)
            }
        }
    }
}