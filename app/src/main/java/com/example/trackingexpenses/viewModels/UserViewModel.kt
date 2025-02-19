package com.example.trackingexpenses.viewModels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.trackingexpenses.activities.LoginActivity
import com.example.trackingexpenses.activities.MainActivity
import com.example.trackingexpenses.models.Period
import com.example.trackingexpenses.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class UserViewModel : ViewModel() {
    val a = "";
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var _userIncomeForPeriod = MutableStateFlow(0f)
    val userIncomeForPeriod: StateFlow<Float> get() = _userIncomeForPeriod

    private var _userExpensesForPeriod = MutableStateFlow(0f)
    val userExpensesForPeriod: StateFlow<Float> get() = _userExpensesForPeriod

    private var _userTotalIncome = MutableStateFlow(0f)
    val userTotalIncome: StateFlow<Float> get() = _userTotalIncome

    private var _userTotalExpenditure = MutableStateFlow(0f)
    val userTotalExpenditure: StateFlow<Float> get() = _userTotalExpenditure

    private var _userExpensesForDay = MutableStateFlow(0f)
    val userExpensesForDay: StateFlow<Float> get() = _userExpensesForDay

    private var _dayLimit = MutableStateFlow(0f)
    val dayLimit: StateFlow<Float> get() = _dayLimit

    private var _spendInPercent = MutableStateFlow(0f)
    val spendInPercent: StateFlow<Float> get() = _spendInPercent

    val userCurrentPeriod = mutableIntStateOf(1)

    private val _userPhotoUrl = MutableStateFlow<Uri?>(null)
    val userPhotoUrl: StateFlow<Uri?> get() = _userPhotoUrl

    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> get() = _userDisplayName

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> get() = _userEmail

    private val userId = auth.currentUser?.uid
    private val userDocRef = userId?.let { db.collection("users").document(it) }
    private val currentDate =
        LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru", "RU")))

    init {
        fetchUserData()
    }

    private fun setDateOfLastEnterInApp() {
        userDocRef?.update("lastEnterInApp", currentDate)
            ?.addOnSuccessListener {
                Log.d("Firestore", "Дата последнего входа успешно обновлена.")
            }
            ?.addOnFailureListener { e ->
                Log.w("Firestore", "Ошибка обновления даты последнего входа", e)
            }
    }

    fun checkIfTodayIsTheDayToUpdateSpendingColumnForToday() {
        userDocRef?.get()?.addOnSuccessListener { userDocument ->
            if (userDocument.exists()) {
                val user = userDocument.toObject(User::class.java)

                val lastEnterDate = user?.lastEnterInApp

                if (lastEnterDate != currentDate) {
                    userDocRef.update("expensesForDay", 0f)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Поле expenses_for_day успешно обновлено на 0.")
                            setDateOfLastEnterInApp()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Ошибка обновления expenses_for_day", e)
                            setDateOfLastEnterInApp()
                        }
                }
            }
        }?.addOnFailureListener { e ->
            Log.w("Firestore", "Ошибка получения документа пользователя", e)
        }
    }

    fun increasePeriod() {
        userId?.let { userId ->
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    val user = userDocument.toObject(User::class.java)

                    val newPeriod = user?.currentPeriod ?: 0

                    val newPeriodData = Period(
                        period = newPeriod,
                        expensesForThePeriod = user?.expensesForThePeriod,
                        incomeForThePeriod = user?.incomeForThePeriod
                    )

                    db.collection("periods")
                        .add(newPeriodData)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                "Firestore",
                                "New period data saved successfully with ID: ${documentReference.id}"
                            )

                            userDocRef.update("currentPeriod", newPeriod + 1)

                            userDocRef.update(
                                "incomeForThePeriod", 0f,
                                "expensesForThePeriod", 0f
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error saving new period data", e)
                        }
                }
                fetchUserData()
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error getting user", e)
            }
        }
    }

    fun fetchUserData() {
        userDocRef?.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                _userTotalIncome.value = user?.totalIncome ?: 0f
                _userTotalExpenditure.value = user?.totalExpenditure ?: 0f
                _userExpensesForPeriod.value = user?.expensesForThePeriod ?: 0f
                _userIncomeForPeriod.value = user?.incomeForThePeriod ?: 0f
                _userExpensesForDay.value = user?.expensesForDay ?: 0f
                _dayLimit.value = user?.dayLimit ?: 0f
                userCurrentPeriod.intValue = user?.currentPeriod ?: 1

                _userPhotoUrl.value = auth.currentUser?.photoUrl
                _userDisplayName.value = auth.currentUser?.displayName
                _userEmail.value = auth.currentUser?.email

                calculateExpensesInPercent()
            } else {
                Log.w("Firestore", "User document does not exist")
            }
        }
    }

    private fun calculateExpensesInPercent() {
        val totalIncome = _userTotalIncome.value
        val totalExpenditure = _userTotalExpenditure.value

        _spendInPercent.value = if (totalIncome + totalExpenditure > 0) {
            totalExpenditure / (totalIncome + totalExpenditure)
        } else {
            0f
        }.also {
            Log.i("TestTest", it.toString())
        }
    }

    fun signOut(context: Context) {
        auth.signOut()
        (context as MainActivity).finish()
        val i = Intent(context, LoginActivity::class.java)
        context.startActivity(i)
    }

    fun setDayLimit(limit: Float) {
        userDocRef?.update("dayLimit", limit)
    }
}