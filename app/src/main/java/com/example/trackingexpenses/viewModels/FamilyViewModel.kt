package com.example.trackingexpenses.viewModels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import co.yml.charts.common.extensions.isNotNull
import com.example.trackingexpenses.models.FamilyMember
import com.example.trackingexpenses.models.Transaction
import com.example.trackingexpenses.models.User
import com.example.trackingexpenses.objects.Collections
import com.example.trackingexpenses.objects.Fields
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES
import com.example.trackingexpenses.objects.TypeOfTransactions.INCOME
import com.example.trackingexpenses.objects.TypesOfAccount
import com.example.trackingexpenses.objects.TypesOfAccount.FAMILY
import com.example.trackingexpenses.objects.TypesOfAccount.MEMBER_OF_FAMILY
import com.example.trackingexpenses.objects.TypesOfAccount.PENDING
import com.example.trackingexpenses.objects.TypesOfAccount.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FamilyViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userId = auth.currentUser?.uid
    private val userDocRef = userId?.let { db.collection(Collections.USERS).document(it) }

    private val _userFamilyMembers = MutableStateFlow<List<FamilyMember>>(emptyList())
    val userFamilyMembers: StateFlow<List<FamilyMember>> get() = _userFamilyMembers

    private val _currentTypeOfAccount = MutableStateFlow(USER)
    val currentTypeOfAccount: StateFlow<String> get() = _currentTypeOfAccount

    init {
        calculateCurrentTypeOfAccount()
        fetchFamilyMembers()
    }

    /**
     * Определяет тип аккаунта пользователя (USER, FAMILY, MEMBER_OF_FAMILY).
     */
    fun calculateCurrentTypeOfAccount() {
        userDocRef?.get()?.addOnSuccessListener { userDocument ->
            if (userDocument.exists()) {
                val user = userDocument.toObject(User::class.java)
                _currentTypeOfAccount.value = when {
                    user?.profileType == FAMILY -> FAMILY
                    user?.familyId.isNotNull() -> MEMBER_OF_FAMILY
                    else -> USER
                }
            } else {
                _currentTypeOfAccount.value = USER
            }
        }?.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching user document", e)
        }
    }

    /**
     * Создает семейный аккаунт для текущего пользователя.
     */
    fun createFamilyAccount() {
        val emptyMap: Map<String, Any> = mapOf(Fields.MEMBERS to emptyList<Any>())

        db.collection(Collections.FAMILIES).document(userId.toString())
            .set(emptyMap)
            .addOnSuccessListener {
                Log.d("FamilyViewModel", "Family document created successfully")
                userDocRef?.update(Fields.PROFILE_TYPE, FAMILY)
                    ?.addOnSuccessListener {
                        calculateCurrentTypeOfAccount()
                    }
                    ?.addOnFailureListener { e ->
                        Log.w("Firestore", "Error updating profile type", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating family document", e)
            }
    }

    /**
     * Отправляет приглашение для присоединения к семье через шаринг.
     */
    fun sendInviteToFamily(context: Context) {
        val message = "Привет! Присоединяйся к нашей семейной учетной записи. Мой ID: `$userId`"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "Поделиться через")
        context.startActivity(chooser)
    }

    /**
     * Отправляет запрос на присоединение к семье, проверяя отсутствие дубликатов.
     */
    fun sendQueryToJoinInTheFamily(familyId: String, callback: (Boolean) -> Unit) {
        if (userId == null) {
            callback(false)
            return
        }

        val familyDocRef = db.collection(Collections.FAMILIES).document(familyId)

        familyDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val members = document.get(Fields.MEMBERS) as? List<Map<String, Any>> ?: emptyList()
                // Проверка на существование пользователя в семье
                if (members.any { it[Fields.USER_ID] == userId }) {
                    Log.d("Firestore", "User $userId already in family $familyId")
                    callback(false)
                    return@addOnSuccessListener
                }

                val familyMember = FamilyMember(
                    userId = userId,
                    name = auth.currentUser?.displayName,
                    email = auth.currentUser?.email.toString(),
                    img = auth.currentUser?.photoUrl.toString(),
                    status = PENDING
                )

                familyDocRef.update(Fields.MEMBERS, FieldValue.arrayUnion(familyMember))
                    .addOnSuccessListener {
                        Log.d("Firestore", "Request to join family $familyId sent")
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error sending join request", e)
                        callback(false)
                    }
            } else {
                Log.w("Firestore", "Family $familyId does not exist")
                callback(false)
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error checking family existence", e)
            callback(false)
        }
    }

    /**
     * Принимает пользователя в семью, обновляя familyId и статус на MEMBER_OF_FAMILY.
     */
    fun acceptUserToTheFamily(futureMemberOfFamily: String) {
        val familyId = userId ?: return
        val userDocRef = db.collection(Collections.USERS).document(futureMemberOfFamily)
        val familyDocRef = db.collection(Collections.FAMILIES).document(familyId)

        familyDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val members = document.get(Fields.MEMBERS) as? List<Map<String, Any>> ?: emptyList()
                val memberIndex = members.indexOfFirst { it[Fields.USER_ID] == futureMemberOfFamily }

                if (memberIndex != -1) {
                    // Обновляем только статус существующего участника
                    val updatedMember = members[memberIndex].toMutableMap().apply {
                        this[Fields.STATUS] = MEMBER_OF_FAMILY
                    }
                    val updatedMembers = members.toMutableList().apply {
                        this[memberIndex] = updatedMember
                    }

                    // Обновляем список участников в Firestore
                    familyDocRef.update(Fields.MEMBERS, updatedMembers)
                        .addOnSuccessListener {
                            Log.d("Firestore", "$futureMemberOfFamily status updated to MEMBER_OF_FAMILY")
                            // Обновляем familyId пользователя
                            userDocRef.update(Fields.FAMILY_ID, familyId)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "User $futureMemberOfFamily family ID updated")
                                    fetchFamilyMembers()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error updating user family ID", e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error updating member status", e)
                        }
                } else {
                    Log.w("Firestore", "Member $futureMemberOfFamily not found in family")
                }
            } else {
                Log.w("Firestore", "Family $familyId does not exist")
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching family document", e)
        }
    }

    /**
     * Получает список членов семьи из Firestore.
     */
    private fun fetchFamilyMembers() {
        if (userId == null) return

        userDocRef?.get()?.addOnSuccessListener { userDocument ->
            if (userDocument.exists()) {
                val user = userDocument.toObject(User::class.java)
                val familyId = user?.familyId ?: userId

                db.collection(Collections.FAMILIES).document(familyId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val members = document.get(Fields.MEMBERS) as? List<Map<String, Any>> ?: emptyList()
                            val familyMembers = members.map { memberMap ->
                                FamilyMember(
                                    userId = memberMap[Fields.USER_ID] as String,
                                    status = memberMap[Fields.STATUS] as String,
                                    name = memberMap[Fields.NAME] as String?,
                                    email = memberMap[Fields.EMAIL] as String,
                                    img = memberMap[Fields.IMG] as String?
                                )
                            }
                            _userFamilyMembers.value = familyMembers
                            Log.d("Firestore", "Fetched family members: $familyMembers")
                        } else {
                            Log.w("Firestore", "Family $familyId does not exist")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error fetching family members", e)
                    }
            }
        }?.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching user document", e)
        }
    }

    /**
     * Обновляет данные семьи при добавлении новой транзакции.
     */
    fun updateFamilyProfile(familyId: String, newTransaction: Transaction, type: String) {
        val familyDocRef = db.collection(Collections.USERS).document(familyId)

        familyDocRef.get().addOnSuccessListener { familyDocument ->
            if (familyDocument.exists()) {
                val familyUser = familyDocument.toObject(User::class.java)
                if (familyUser != null) {
                    newTransaction.period = familyUser.currentPeriod
                    when (type) {
                        EXPENSES -> {
                            familyDocRef.update(
                                Fields.TOTAL_EXPENDITURE,
                                familyUser.totalExpenditure + newTransaction.coast.toFloat().toDouble(),
                                Fields.EXPENSES_FOR_DAY,
                                familyUser.expensesForDay + newTransaction.coast.toFloat(),
                                Fields.EXPENSES_FOR_THE_PERIOD,
                                familyUser.expensesForThePeriod + newTransaction.coast.toFloat().toDouble()
                            )
                        }
                        INCOME -> {
                            familyDocRef.update(
                                Fields.TOTAL_INCOME,
                                familyUser.totalIncome + newTransaction.coast.toFloat().toDouble(),
                                Fields.INCOME_FOR_THE_PERIOD,
                                familyUser.incomeForThePeriod + newTransaction.coast.toFloat().toDouble()
                            )
                        }
                    }

                    db.collection(Collections.TRANSACTIONS)
                        .document(familyId)
                        .collection(Fields.TRANSACTION)
                        .document(newTransaction.id)
                        .set(newTransaction)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Transaction added for family $familyId")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error adding family transaction", e)
                        }
                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching family document", e)
        }
    }

    /**
     * Удаляет транзакцию семьи и обновляет соответствующие данные.
     */
    fun deleteFamilyTransaction(familyId: String, transactionId: String) {
        val transactionDocRef = db.collection(Collections.TRANSACTIONS)
            .document(familyId)
            .collection(Fields.TRANSACTION)
            .document(transactionId)

        transactionDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val transaction = document.toObject(Transaction::class.java)
                val familyDocRef = db.collection(Collections.USERS).document(familyId)

                familyDocRef.get().addOnSuccessListener { familyDocument ->
                    if (familyDocument.exists() && transaction != null) {
                        when (transaction.type) {
                            EXPENSES -> {
                                familyDocRef.update(
                                    Fields.TOTAL_EXPENDITURE,
                                    FieldValue.increment(-transaction.coast.toFloat().toDouble()),
                                    Fields.EXPENSES_FOR_THE_PERIOD,
                                    FieldValue.increment(-transaction.coast.toFloat().toDouble())
                                )
                            }
                            INCOME -> {
                                familyDocRef.update(
                                    Fields.TOTAL_INCOME,
                                    FieldValue.increment(-transaction.coast.toFloat().toDouble()),
                                    Fields.INCOME_FOR_THE_PERIOD,
                                    FieldValue.increment(-transaction.coast.toFloat().toDouble())
                                )
                            }
                        }

                        updateFamilyPeriodData(transaction, -transaction.coast.toFloat())

                        transactionDocRef.delete()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Transaction $transactionId deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error deleting transaction", e)
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.w("Firestore", "Error fetching family document", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching transaction", e)
        }
    }

    /**
     * Обновляет данные периода семьи при изменении транзакции.
     */
    private fun updateFamilyPeriodData(transaction: Transaction, amountChange: Float) {
        db.collection(Collections.PERIODS)
            .whereEqualTo(Fields.PERIOD, transaction.period)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    when (transaction.type) {
                        EXPENSES -> {
                            doc.reference.update(
                                Fields.EXPENSES_FOR_THE_PERIOD,
                                FieldValue.increment(amountChange.toDouble())
                            )
                        }
                        INCOME -> {
                            doc.reference.update(
                                Fields.INCOME_FOR_THE_PERIOD,
                                FieldValue.increment(amountChange.toDouble())
                            )
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error updating period data", e)
            }
    }

    /**
     * Удаляет семейный аккаунт и очищает связанные данные.
     */
    fun deleteFamily() {
        if (userId == null) return

        val familyDocRef = db.collection(Collections.FAMILIES).document(userId)

        // Удаляем семейный документ
        familyDocRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Family $userId deleted")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting family document", e)
            }

        // Очищаем familyId у всех членов семьи
        db.collection(Collections.USERS)
            .whereEqualTo(Fields.FAMILY_ID, userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    doc.reference.update(Fields.FAMILY_ID, FieldValue.delete())
                        .addOnSuccessListener {
                            Log.d("Firestore", "Family ID removed from user ${doc.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error removing family ID from user ${doc.id}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching family members", e)
            }

        // Обновляем профиль текущего пользователя
        userDocRef?.update(Fields.PROFILE_TYPE, USER)
            ?.addOnSuccessListener {
                calculateCurrentTypeOfAccount()
            }
            ?.addOnFailureListener { e ->
                Log.w("Firestore", "Error updating user profile type", e)
            }
    }

    /**
     * Выход текущего пользователя из семьи.
     */
    fun exitFromFamily() {
        if (userId == null) return

        userDocRef?.get()?.addOnSuccessListener { userDocument ->
            if (userDocument.exists()) {
                val familyId = userDocument.getString(Fields.FAMILY_ID) ?: return@addOnSuccessListener
                val familyDocRef = db.collection(Collections.FAMILIES).document(familyId)

                familyDocRef.get().addOnSuccessListener { familyDocument ->
                    if (familyDocument.exists()) {
                        val members = familyDocument.get(Fields.MEMBERS) as? List<Map<String, Any>> ?: emptyList()
                        val updatedMembers = members.filterNot { it[Fields.USER_ID] == userId }

                        familyDocRef.update(Fields.MEMBERS, updatedMembers)
                            .addOnSuccessListener {
                                Log.d("Firestore", "User $userId removed from family $familyId")
                                userDocRef.update(Fields.FAMILY_ID, FieldValue.delete())
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Family ID removed from user $userId")
                                        calculateCurrentTypeOfAccount()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firestore", "Error removing family ID from user", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error removing user from family", e)
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.w("Firestore", "Error fetching family document", e)
                }
            }
        }?.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching user document", e)
        }
    }
}