package com.example.trackingexpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.mainScreen.viewModels.TransactionManagementViewModel

class TransactionManagementViewModelFactory(
    private val categoriesViewModel: CategoriesViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionManagementViewModel(categoriesViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}