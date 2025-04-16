package com.example.trackingexpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel

class TransactionManagementViewModelFactory(
    private val categoriesViewModel: CategoriesViewModel,
    private val familyViewModel: FamilyViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionManagementViewModel(categoriesViewModel, familyViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}