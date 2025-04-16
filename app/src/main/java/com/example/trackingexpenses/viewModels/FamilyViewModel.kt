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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FamilyViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userId = auth.currentUser?.uid
    private val userDocRef = userId?.let { db.collection(Collections.USERS).document(it) }

    private var _userFamilyMembers = MutableStateFlow<List<FamilyMember>>(emptyList())
    val userFamilyMembers: StateFlow<List<FamilyMember>> get() = _userFamilyMembers

    private var _currentTypeOfAccount = MutableStateFlow(USER)
    val currentTypeOfAccount: StateFlow<String> get() = _currentTypeOfAccount

    init {
        calculateCurrentTypeOfAccount()
        fetchFamilyMembers()
    }

    fun calculateCurrentTypeOfAccount() {
        userDocRef?.get()?.addOnSuccessListener { userDocument ->
            if (userDocument.exists()) {
                val user = userDocument.toObject(User::class.java)

                if (user?.profileType == FAMILY) {
                    _currentTypeOfAccount.value = FAMILY
                } else if (user?.profileType != FAMILY && !user?.familyId.isNotNull()) {
                    _currentTypeOfAccount.value = USER
                } else {
                    _currentTypeOfAccount.value = MEMBER_OF_FAMILY
                }
            }
        }
    }

    fun createFamilyAccount() {
        val emptyMap: Map<String, Any> = mapOf()

        db.collection(Collections.FAMILIES).document(userId.toString())
            .set(emptyMap)
            .addOnSuccessListener {
                Log.d("FamilyViewModel", "Empty family document created successfully.")
            }
            .addOnFailureListener { e ->
                Log.w("FamilyViewModel", "Error creating empty family document", e)
            }
        userDocRef?.update(Fields.PROFILE_TYPE, FAMILY)

        calculateCurrentTypeOfAccount()
    }

    fun sendInviteToFamily(context: Context) {
        val userId = auth.currentUser?.uid
        val message =
            "Привет! Я хочу пригласить тебя в нашу семейную учетную запись. Вот мой идентификатор: `$userId`"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val chooser = Intent.createChooser(sendIntent, "Поделиться через")
        context.startActivity(chooser)
    }

    fun sendQueryToJoinInTheFamily(familyId: String, callback: (Boolean) -> Unit) {
        val familyDocRef = db.collection(Collections.FAMILIES).document(familyId)

        familyDocRef.get().addOnSuccessListener { document ->
            val userId = auth.currentUser?.uid
            if (document.exists()) {
                val userDocRef = userId?.let { db.collection(Collections.USERS).document(it) }

                userDocRef?.addSnapshotListener { documentSnapshot, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        callback(false)
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val familyMember = FamilyMember(
                            userId,
                            auth.currentUser?.displayName,
                            auth.currentUser?.email.toString(),
                            auth.currentUser?.photoUrl.toString(),
                            PENDING
                        )
                        Log.d(
                            "Firestore",
                            "User ID: $userId, Display Name: ${auth.currentUser?.displayName}, Email: ${auth.currentUser?.email}, Photo URL: ${auth.currentUser?.photoUrl}"
                        )

                        familyDocRef.update(Fields.MEMBERS, FieldValue.arrayUnion(familyMember))
                            .addOnSuccessListener {
                                Log.d("Firestore", "Member added to family with ID: $familyId")
                                callback(true)
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error adding member to family", e)
                                callback(false)
                            }
                    } else {
                        callback(false)
                    }
                }
            } else {
                callback(false)
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error checking family existence", e)
            callback(false)
        }
    }

    fun acceptUserToTheFamily(futureMemberOfFamily: String) {
        val familyId = auth.currentUser?.uid
        val userDocRef = db.collection(Collections.USERS).document(futureMemberOfFamily)
        val familyDocRef = db.collection(Collections.FAMILIES).document(familyId.toString())

        familyDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val members = document.get(Fields.MEMBERS) as? List<Map<String, Any>> ?: emptyList()

                val memberIndex = members.indexOfFirst { it[Fields.USER_ID] == futureMemberOfFamily }

                if (memberIndex != -1) {
                    val updatedMembers = members.toMutableList()
                    updatedMembers[memberIndex] = updatedMembers[memberIndex].toMutableMap().apply {
                        this[Fields.STATUS] = MEMBER_OF_FAMILY
                    }

                    familyDocRef.set(
                        mapOf(Fields.MEMBERS to updatedMembers),
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        Log.d("Firestore", "$futureMemberOfFamily status updated to MEMBER_OF_FAMILY")
                    }.addOnFailureListener { e ->
                        Log.w("Firestore", "Error updating member status", e)
                    }
                } else {
                    val newMember = mapOf(
                        Fields.USER_ID to futureMemberOfFamily,
                        Fields.STATUS to MEMBER_OF_FAMILY
                    )
                    val updatedMembers = members + newMember

                    familyDocRef.set(
                        mapOf(Fields.MEMBERS to updatedMembers),
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        Log.d("Firestore", "$futureMemberOfFamily added to family with status MEMBER_OF_FAMILY")
                    }.addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding member to family", e)
                    }
                }

                userDocRef.update(Fields.FAMILY_ID, familyId)
                    .addOnSuccessListener {
                        Log.d("Firestore", "User family ID updated successfully.")
                        fetchFamilyMembers()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error updating user family ID", e)
                    }

            } else {
                Log.w("Firestore", "Family document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error getting family document", e)
        }
    }

    private fun fetchFamilyMembers() {
        val userId = auth.currentUser?.uid
        lateinit var familyId: String
        val userDocRef = userId?.let { db.collection(Collections.USERS).document(it) }

        userDocRef?.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                familyId = user?.familyId ?: userId.toString()
            }

            val familyDocRef = db.collection(Collections.FAMILIES).document(familyId)

            familyDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val members =
                            document.get(Fields.MEMBERS) as? List<Map<String, Any>>
                                ?: emptyList()
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
                        Log.i("TestTest", familyMembers.toString())

                    } else {
                        Log.w("Firestore", "Document with ID $userId does not exist")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error fetching family members", e)
                }
        }
    }

    fun updateFamilyProfile(familyId: String, newTransaction: Transaction, type: String) {
        val familyDocRef = db.collection(Collections.USERS).document(familyId)

        familyDocRef.get().addOnSuccessListener { familyDocument ->
            if (familyDocument.exists()) {
                val familyUser = familyDocument.toObject(User::class.java)
                if (familyUser != null) {
                    newTransaction.period = familyUser.currentPeriod
                    when (type) {
                        EXPENSES -> {
                            val updatedTotalExpenditure =
                                (familyUser.totalExpenditure + newTransaction.coast.toFloat()).toDouble()
                            val updatedExpensesForDay =
                                familyUser.expensesForDay + newTransaction.coast.toFloat()
                            val updatedExpensesForPeriod =
                                (familyUser.expensesForThePeriod + newTransaction.coast.toFloat()).toDouble()

                            familyDocRef.update(
                                Fields.TOTAL_EXPENDITURE, updatedTotalExpenditure,
                                Fields.EXPENSES_FOR_DAY, updatedExpensesForDay,
                                Fields.EXPENSES_FOR_THE_PERIOD, updatedExpensesForPeriod
                            )
                        }

                        INCOME -> {
                            val updatedTotalIncome =
                                (familyUser.totalIncome + newTransaction.coast.toFloat()).toDouble()
                            val updatedIncomeForPeriod =
                                (familyUser.incomeForThePeriod + newTransaction.coast.toFloat()).toDouble()

                            familyDocRef.update(
                                Fields.TOTAL_INCOME, updatedTotalIncome,
                                Fields.INCOME_FOR_THE_PERIOD, updatedIncomeForPeriod
                            )
                        }
                    }

                    db.collection(Collections.TRANSACTIONS)
                        .document(familyId)
                        .collection(Fields.TRANSACTION)
                        .document(newTransaction.id)
                        .set(newTransaction)
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error updating family transaction", e)
                        }
                        .addOnSuccessListener {
                            Log.w("Firestore", "tran suc add in fam")

                        }

                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error getting family document", e)
        }
    }

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
                    if (familyDocument.exists()) {
                        when (transaction?.type) {
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

                        updateFamilyPeriodData(
                            transaction,
                            -transaction?.coast!!.toFloat()
                        )

                        transactionDocRef.delete().addOnSuccessListener {
                            Log.d("Firestore", "Family transaction successfully deleted!")
                        }.addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting family transaction", e)
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting family document", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error getting family transaction", e)
        }
    }

    private fun updateFamilyPeriodData(
        transaction: Transaction?,
        amountChange: Float,
    ) {
        if (transaction != null) {
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
                    Log.w("Firestore", "Error updating family period data", e)
                }
        }
    }

    fun deleteFamily() {
        val userId = auth.currentUser?.uid ?: return
        val familyDocRef = db.collection(Collections.FAMILIES).document(userId)

        familyDocRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Family document successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting family document", e)
            }

        val membersDocRef = db.collection(Collections.USERS).whereEqualTo(Fields.FAMILY_ID, userId)
        membersDocRef.get().addOnSuccessListener { querySnapshot ->
            for (doc in querySnapshot.documents) {
                doc.reference.update(Fields.FAMILY_ID, FieldValue.delete())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Family ID removed from user: ${doc.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error removing family ID from user", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error fetching family members", e)
        }

        userDocRef?.update(Fields.PROFILE_TYPE, USER)

        calculateCurrentTypeOfAccount()
    }

    fun exitFromFamily() {
        val userId = auth.currentUser?.uid ?: return
        val userDocRef = db.collection(Collections.USERS).document(userId)

        userDocRef.get().addOnSuccessListener { userDocument ->
            if (userDocument.exists()) {
                val familyId = userDocument.getString(Fields.FAMILY_ID)
                val familyDocRef =
                    familyId?.let { db.collection(Collections.FAMILIES).document(it) }

                familyDocRef?.get()?.addOnSuccessListener { familyDocument ->
                    if (familyDocument.exists()) {
                        familyDocRef.update(Fields.MEMBERS, FieldValue.arrayRemove(userId))
                            .addOnSuccessListener {
                                Log.d("Firestore", "User $userId removed from family ${familyId}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error removing user from family", e)
                            }

                        userDocRef.update(Fields.FAMILY_ID, FieldValue.delete())
                            .addOnSuccessListener {
                                Log.d("Firestore", "User $userId family ID removed successfully.")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error removing family ID from user", e)
                            }
                    }
                }?.addOnFailureListener { e ->
                    Log.w("Firestore", "Error fetching family document", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error getting user document", e)
        }

        calculateCurrentTypeOfAccount()
    }
}