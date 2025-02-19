package com.example.trackingexpenses.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.models.Transaction
import com.example.trackingexpenses.models.User
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES
import com.example.trackingexpenses.objects.TypeOfTransactions.INCOME
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionManagementViewModel(private val categoriesViewModel: CategoriesViewModel) :
    ViewModel() {
    var coast = mutableStateOf("")
    var notes = mutableStateOf("")
    val date = mutableStateOf("")
    val category = mutableStateOf("")
    val time = mutableStateOf("")

    val isLimitExceeded = mutableStateOf(false)

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun checkLimit(newExpensesForDay: Float, dayLimit: Float?) {
        if (newExpensesForDay > (dayLimit?.toFloat() ?: Float.MAX_VALUE)) {
            isLimitExceeded.value = true
        }
    }

    fun addTransaction(
        type: String,
        category: String,
        newTransaction: Transaction,
        onComplete: (String) -> Unit,
    ) {
        when (type) {
            EXPENSES -> categoriesViewModel.addCategoryOfExpenditure(category)
            INCOME -> categoriesViewModel.addCategoryOfIncome(category)
        }

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    val user = userDocument.toObject(User::class.java)

                    val transactionDate = LocalDate.parse(
                        newTransaction.date,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                    val today = LocalDate.now()

                    if (newTransaction.period == user?.currentPeriod) {
                        when (type) {
                            EXPENSES -> {
                                val newExpensesForDay = if (transactionDate.isEqual(today)) {
                                    (user.expensesForDay + newTransaction.coast.toFloat()).toDouble()
                                } else {
                                    user.expensesForDay.toDouble()
                                }

                                checkLimit(newExpensesForDay.toFloat(), user.dayLimit)

                                userDocRef.update(
                                    "totalExpenditure",
                                    (user.totalExpenditure + newTransaction.coast.toFloat()).toDouble(),
                                    "expensesForDay",
                                    newExpensesForDay,
                                    "expensesForThePeriod",
                                    (user.expensesForThePeriod + newTransaction.coast.toFloat()).toDouble()
                                )
                            }

                            INCOME -> {
                                userDocRef.update(
                                    "totalIncome",
                                    (user.totalIncome + newTransaction.coast.toFloat()).toDouble(),
                                    "incomeForThePeriod",
                                    (user.incomeForThePeriod + newTransaction.coast.toFloat()).toDouble()
                                )
                            }
                        }
                    }

                    db.collection("transactions")
                        .document(userId)
                        .collection("transaction")
                        .add(newTransaction)
                        .addOnSuccessListener { document ->
                            val updatedTransaction = newTransaction.copy(id = document.id)
                            db.collection("transactions")
                                .document(userId)
                                .collection("transaction")
                                .document(document.id)
                                .set(updatedTransaction)
                                .addOnSuccessListener {
                                    onComplete(document.id)
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error updating transaction", e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error adding transaction", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error getting user document", e)
            }
        }
    }

    fun deleteTransaction(transactionId: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val transactionDocRef = db.collection("transactions")
                .document(userId)
                .collection("transaction")
                .document(transactionId)

            transactionDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val transaction = document.toObject(Transaction::class.java)
                    val userDocRef = db.collection("users").document(userId)

                    userDocRef.get().addOnSuccessListener { userDocument ->
                        if (userDocument.exists()) {
                            val user = userDocument.toObject(User::class.java)

                            if (transaction?.period == user?.currentPeriod) {
                                when (transaction?.type) {
                                    EXPENSES -> {
                                        userDocRef.update(
                                            "totalExpenditure",
                                            FieldValue.increment(
                                                -transaction.coast.toFloat().toDouble()
                                            ),
                                            "expensesForThePeriod",
                                            FieldValue.increment(
                                                -transaction.coast.toFloat().toDouble()
                                            ),
                                            "expensesForDay",
                                            FieldValue.increment(
                                                -transaction.coast.toFloat().toDouble()
                                            )
                                        )
                                    }

                                    INCOME -> {
                                        userDocRef.update(
                                            "totalIncome",
                                            FieldValue.increment(
                                                -transaction.coast.toFloat().toDouble()
                                            ),
                                            "incomeForThePeriod",
                                            FieldValue.increment(
                                                -transaction.coast.toFloat().toDouble()
                                            )
                                        )
                                    }
                                }
                            }

                            updatePeriodData(transaction, -transaction?.coast!!.toFloat())

                            transactionDocRef.delete().addOnSuccessListener {
                                Log.d("Firestore", "Transaction successfully deleted!")
                            }.addOnFailureListener { e ->
                                Log.w("Firestore", "Error deleting transaction", e)
                            }
                        }
                    }.addOnFailureListener { e ->
                        Log.w("Firestore", "Error getting user document", e)
                    }
                }
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error getting transaction", e)
            }
        }
    }

    private fun updatePeriodData(transaction: Transaction?, amountChange: Float) {
        val userId = auth.currentUser?.uid
        if (userId != null && transaction != null) {
            val periodDocRef = db.collection("periods")
                .whereEqualTo("period", transaction.period)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (doc in querySnapshot.documents) {
                        val type = transaction.type
                        if (type == EXPENSES) {
                            doc.reference.update(
                                "expensesForThePeriod",
                                FieldValue.increment(amountChange.toDouble())
                            )
                        } else if (type == INCOME) {
                            doc.reference.update(
                                "incomeForThePeriod",
                                FieldValue.increment(amountChange.toDouble())
                            )
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.w("Firestore", "Error updating period data", e)
                }
        }
    }
}