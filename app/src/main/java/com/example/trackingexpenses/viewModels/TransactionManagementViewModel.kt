package com.example.trackingexpenses.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import co.yml.charts.common.extensions.isNotNull
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.models.Transaction
import com.example.trackingexpenses.models.User
import com.example.trackingexpenses.objects.Collections
import com.example.trackingexpenses.objects.Fields
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES
import com.example.trackingexpenses.objects.TypeOfTransactions.INCOME
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionManagementViewModel(
    private val categoriesViewModel: CategoriesViewModel,
    private val familyViewModel: FamilyViewModel
) : ViewModel() {
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
        } else {
            isLimitExceeded.value = false
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
            val userDocRef = db.collection(Collections.USERS).document(userId)

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
                                    Fields.TOTAL_EXPENDITURE,
                                    (user.totalExpenditure + newTransaction.coast.toFloat()).toDouble(),
                                    Fields.EXPENSES_FOR_DAY,
                                    newExpensesForDay,
                                    Fields.EXPENSES_FOR_THE_PERIOD,
                                    (user.expensesForThePeriod + newTransaction.coast.toFloat()).toDouble()
                                )
                            }

                            INCOME -> {
                                userDocRef.update(
                                    Fields.TOTAL_INCOME,
                                    (user.totalIncome + newTransaction.coast.toFloat()).toDouble(),
                                    Fields.INCOME_FOR_THE_PERIOD,
                                    (user.incomeForThePeriod + newTransaction.coast.toFloat()).toDouble()
                                )
                            }
                        }
                    }

                    db.collection(Collections.TRANSACTIONS)
                        .document(userId)
                        .collection(Fields.TRANSACTION)
                        .add(newTransaction)
                        .addOnSuccessListener { document ->
                            val updatedTransaction = newTransaction.copy(id = document.id)
                            db.collection(Collections.TRANSACTIONS)
                                .document(userId)
                                .collection(Fields.TRANSACTION)
                                .document(document.id)
                                .set(updatedTransaction)
                                .addOnSuccessListener {
                                    user?.familyId?.let { familyId ->
                                        familyViewModel.updateFamilyProfile(familyId, updatedTransaction, type)
                                    }
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
            val transactionDocRef = db.collection(Collections.TRANSACTIONS)
                .document(userId)
                .collection(Fields.TRANSACTION)
                .document(transactionId)

            transactionDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val transaction = document.toObject(Transaction::class.java)
                    val userDocRef = db.collection(Collections.USERS).document(userId)

                    userDocRef.get().addOnSuccessListener { userDocument ->
                        if (userDocument.exists()) {
                            val user = userDocument.toObject(User::class.java)

                            if (transaction?.period == user?.currentPeriod) {
                                when (transaction!!.type) {
                                    EXPENSES -> {
                                        userDocRef.update(
                                            Fields.TOTAL_EXPENDITURE,
                                            FieldValue.increment(-transaction!!.coast.toDouble()),
                                            Fields.EXPENSES_FOR_THE_PERIOD,
                                            FieldValue.increment(-transaction!!.coast.toDouble()),
                                            Fields.EXPENSES_FOR_DAY,
                                            FieldValue.increment(-transaction.coast.toDouble())
                                        )
                                    }
                                    INCOME -> {
                                        userDocRef.update(
                                            Fields.TOTAL_INCOME,
                                            FieldValue.increment(-transaction!!.coast.toDouble()),
                                            Fields.INCOME_FOR_THE_PERIOD,
                                            FieldValue.increment(-transaction.coast.toDouble())
                                        )
                                    }
                                }
                            }

                            checkLimit(user?.expensesForDay ?: 0f, user?.dayLimit ?: 0f)

                            updatePeriodData(transaction, -transaction!!.coast.toFloat())

                            if (user?.familyId.isNotNull()) {
                                familyViewModel.deleteFamilyTransaction(user!!.familyId.toString(), transactionId)
                            }

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

    fun updateTransaction(updatedTransaction: Transaction) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val transactionDocRef = db.collection(Collections.TRANSACTIONS)
                .document(userId)
                .collection(Fields.TRANSACTION)
                .document(updatedTransaction.id)

            transactionDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val oldTransaction = document.toObject(Transaction::class.java)
                    val userDocRef = db.collection(Collections.USERS).document(userId)

                    userDocRef.get().addOnSuccessListener { userDocument ->
                        if (userDocument.exists()) {
                            val user = userDocument.toObject(User::class.java)
                            if (oldTransaction == null) {
                                Log.w("Firestore", "Old transaction is null")
                                return@addOnSuccessListener
                            }

                            if (oldTransaction.period == user?.currentPeriod) {
                                val coastDifference = updatedTransaction.coast.toDouble() - oldTransaction.coast.toDouble()
                                val transactionDate = LocalDate.parse(
                                    updatedTransaction.date,
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                )
                                val today = LocalDate.now()

                                when (updatedTransaction.type) {
                                    EXPENSES -> {
                                        val newExpensesForDay = if (transactionDate.isEqual(today)) {
                                            (user.expensesForDay + coastDifference.toFloat()).toDouble()
                                        } else {
                                            user.expensesForDay.toDouble()
                                        }

                                        checkLimit(newExpensesForDay.toFloat(), user.dayLimit)

                                        userDocRef.update(
                                            Fields.TOTAL_EXPENDITURE,
                                            FieldValue.increment(coastDifference.toDouble()),
                                            Fields.EXPENSES_FOR_THE_PERIOD,
                                            FieldValue.increment(coastDifference.toDouble()),
                                            Fields.EXPENSES_FOR_DAY,
                                            newExpensesForDay
                                        )
                                    }
                                    INCOME -> {
                                        userDocRef.update(
                                            Fields.TOTAL_INCOME,
                                            FieldValue.increment(coastDifference.toDouble()),
                                            Fields.INCOME_FOR_THE_PERIOD,
                                            FieldValue.increment(coastDifference.toDouble())
                                        )
                                    }
                                }
                            }

                            // Update category in CategoriesViewModel if changed
                            if (oldTransaction.category != updatedTransaction.category) {
                                when (updatedTransaction.type) {
                                    EXPENSES -> categoriesViewModel.addCategoryOfExpenditure(updatedTransaction.category)
                                    INCOME -> categoriesViewModel.addCategoryOfIncome(updatedTransaction.category)
                                }
                            }

                            // Update period data
                            updatePeriodData(oldTransaction, -oldTransaction.coast.toFloat())
                            updatePeriodData(updatedTransaction, updatedTransaction.coast.toFloat())

                            // Update family profile if applicable
                            if (user?.familyId.isNotNull()) {
                                familyViewModel.updateFamilyProfile(
                                    user!!.familyId!!,
                                    updatedTransaction,
                                    updatedTransaction.type
                                )
                            }

                            // Update transaction in Firestore
                            transactionDocRef.set(updatedTransaction)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Transaction successfully updated!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error updating transaction", e)
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
            val periodDocRef = db.collection(Collections.PERIODS)
                .whereEqualTo(Fields.PERIOD, transaction.period)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (doc in querySnapshot.documents) {
                        val type = transaction.type
                        if (type == EXPENSES) {
                            doc.reference.update(
                                Fields.EXPENSES_FOR_THE_PERIOD,
                                FieldValue.increment(amountChange.toDouble())
                            )
                        } else if (type == INCOME) {
                            doc.reference.update(
                                Fields.INCOME_FOR_THE_PERIOD,
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