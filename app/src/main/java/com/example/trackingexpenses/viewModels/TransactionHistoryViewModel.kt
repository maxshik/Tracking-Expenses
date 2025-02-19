package com.example.trackingexpenses.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trackingexpenses.models.Period
import com.example.trackingexpenses.models.Transaction
import com.example.trackingexpenses.objects.SortTypesInHistoryActivity.ONLY_EXPENSES
import com.example.trackingexpenses.objects.SortTypesInHistoryActivity.ONLY_INCOME
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES
import com.example.trackingexpenses.objects.TypeOfTransactions.INCOME
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionHistoryViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    val currentSortType = mutableStateOf("")

    val recentTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions = MutableLiveData<List<Transaction>>()
    val popularCategoriesLiveData = MutableLiveData<List<Pair<Double, String>>>()
    val transactionsLast30Days = MutableLiveData<List<Pair<Double, String>>>()
    val expensesDataForPeriods = MutableLiveData<List<Pair<Double, String>>>()
    val incomeDataForPeriods = MutableLiveData<List<Pair<Double, String>>>()

    fun fetchRecentTransactions() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("transactions")
                .document(userId)
                .collection("transaction")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val transactions = snapshot.toObjects(Transaction::class.java)
                        recentTransactions.value = transactions
                    }
                }
        }
    }

    fun fetchAndFilterTransactions(type: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("transactions")
                .document(userId)
                .collection("transaction")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val transactions = snapshot.toObjects(Transaction::class.java)

                        val filteredTransactions = when (type) {
                            ONLY_EXPENSES -> transactions.filter { it.type == EXPENSES }
                            ONLY_INCOME -> transactions.filter { it.type == INCOME }
                            else -> transactions
                        }

                        allTransactions.value = filteredTransactions
                    }
                }
        }
    }

    fun fetchPopularCategoriesOfExpenses() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("transactions")
                .document(userId)
                .collection("transaction")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("Firestore Error", "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val transactions = snapshot.toObjects(Transaction::class.java)

                        val categoryMap =
                            transactions.filter { it.type == EXPENSES }.groupBy { it.category }
                                .mapValues { (_, transactions) ->
                                    transactions.sumOf {
                                        it.coast.toDoubleOrNull() ?: 0.0
                                    }
                                }
                                .filter { it.value > 0 }

                        val popularCategories = categoryMap.map { (category, totalSpent) ->
                            Pair(totalSpent, category)
                        }.sortedByDescending { it.first }

                        popularCategoriesLiveData.value = popularCategories
                    } else {
                        Log.d("Fetch Popular Categories", "No transactions found.")
                    }
                }
        }
    }

    fun fetchTransactionsLast30Days() {
        val userId = auth.currentUser?.uid
        Log.i("TestTest", "User ID: $userId")

        if (userId != null) {
            val calendar = Calendar.getInstance()
            val endDate = Timestamp.now()
            calendar.add(Calendar.DAY_OF_YEAR, -30)
            val startDate = Timestamp(calendar.time)

            Log.i("TestTest", "Fetching transactions for user: $userId")
            Log.i("TestTest", "Start date: $startDate, End date: $endDate")

            db.collection("transactions")
                .document(userId)
                .collection("transaction")
                .whereGreaterThan("dateTime", startDate)
                .whereLessThan("dateTime", endDate)
                .whereEqualTo("type", EXPENSES)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val transactions = snapshot.toObjects(Transaction::class.java)
                        Log.i("TestTest", "Fetched transactions: ${transactions.size}")

                        val dailyExpenditures = transactions.groupBy { transaction ->
                            transaction.dateTime.toDate().let { date ->
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                            }
                        }.mapValues { entry ->
                            entry.value.sumOf { it.coast.toDoubleOrNull() ?: 0.0 }
                        }

                        dailyExpenditures.forEach { (date, totalCost) ->
                            Log.i("TestTest", "Date: $date, Total Cost: $totalCost")
                        }

                        val transactionPairs = dailyExpenditures.map { (date, totalCost) ->
                            Pair(totalCost, date)
                        }

                        transactionsLast30Days.value = transactionPairs
                    } else {
                        Log.i("TestTest", "No transactions found or snapshot is null")
                    }
                }
        } else {
            Log.i("TestTest", "User ID is null")
        }
    }

    fun fetchPeriodsData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("periods")
                .get()
                .addOnSuccessListener { result ->
                    val expensesData = mutableListOf<Pair<Double, String>>()
                    val incomeData = mutableListOf<Pair<Double, String>>()

                    for (document in result) {
                        val periodData = document.toObject(Period::class.java)
                        val period = periodData.period.toString()
                        expensesData.add(Pair(periodData.expensesForThePeriod!!.toDouble(), period))
                        incomeData.add(Pair(periodData.incomeForThePeriod!!.toDouble(), period))
                    }

                    expensesData.sortBy { it.second.toInt() }
                    incomeData.sortBy { it.second.toInt() }

                    incomeDataForPeriods.value = incomeData
                    expensesDataForPeriods.value = expensesData
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting periods data", e)
                }
        }
    }
}