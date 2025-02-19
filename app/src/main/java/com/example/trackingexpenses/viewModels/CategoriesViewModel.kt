package com.example.trackingexpenses.mainScreen.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoriesViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _categoriesOfExpenditure = MutableLiveData<MutableList<String>>()
    val categoriesOfExpenditure: LiveData<MutableList<String>> get() = _categoriesOfExpenditure

    private val _categoriesOfIncome = MutableLiveData<MutableList<String>>()
    val categoriesOfIncome: LiveData<MutableList<String>> get() = _categoriesOfIncome

    init {
        loadCategories()
        loadIncomeCategories()
    }

    private fun loadIncomeCategories() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("categoriesOfIncome")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fetchedIncomeCategories =
                            document.get("categoriesOfIncome") as? List<String>
                        _categoriesOfIncome.value =
                            fetchedIncomeCategories?.toMutableList()?.sorted()?.toMutableList()
                                ?: mutableListOf()
                    } else {
                        _categoriesOfIncome.value = mutableListOf()
                    }
                }
                .addOnFailureListener { e ->
                    _categoriesOfIncome.value = mutableListOf()
                }
        } else {
            _categoriesOfIncome.value = mutableListOf()
        }
    }

    fun addCategoryOfIncome(newCategory: String) {
        val currentIncomeCategories = _categoriesOfIncome.value ?: mutableListOf()
        val normalizedCategory = newCategory.lowercase()

        if (!currentIncomeCategories.any { it.lowercase() == normalizedCategory }) {
            currentIncomeCategories.add(newCategory)
            currentIncomeCategories.sort()
            _categoriesOfIncome.value = currentIncomeCategories
            updateCategoriesOfIncome(currentIncomeCategories)
        }
    }

    private fun updateCategoriesOfIncome(categories: MutableList<String>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("categoriesOfIncome")
                .document(userId)
                .set(mapOf("categoriesOfIncome" to categories))
        }
    }

    private fun loadCategories() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("categoriesOfExpenditure")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fetchedCategories =
                            document.get("categoriesOfExpenditure") as? List<String>
                        _categoriesOfExpenditure.value =
                            fetchedCategories?.toMutableList()?.sorted()?.toMutableList()
                                ?: mutableListOf()
                    } else {
                        _categoriesOfExpenditure.value = mutableListOf()
                    }
                }
                .addOnFailureListener { e ->
                    _categoriesOfExpenditure.value = mutableListOf()
                }
        } else {
            _categoriesOfExpenditure.value = mutableListOf()
        }
    }

    fun addCategoryOfExpenditure(newCategory: String) {
        val currentCategories = _categoriesOfExpenditure.value ?: mutableListOf()
        val normalizedCategory = newCategory.lowercase()

        if (!currentCategories.any { it.lowercase() == normalizedCategory }) {
            currentCategories.add(newCategory)
            currentCategories.sort()
            _categoriesOfExpenditure.value = currentCategories
            updateCategoriesOfExpenditure(currentCategories)
        }
    }

    private fun updateCategoriesOfExpenditure(categories: MutableList<String>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("categoriesOfExpenditure")
                .document(userId)
                .set(mapOf("categoriesOfExpenditure" to categories))
        }
    }
}