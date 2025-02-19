package com.example.trackingexpenses.views.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.viewModels.UserViewModel

@Composable
fun MainScreen(
    transactionManagementViewModel: TransactionManagementViewModel,
    userViewModel: UserViewModel,
    categoriesViewModel: CategoriesViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    modifier: Modifier,
    navController: NavHostController
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            GeneralInformation(userViewModel, transactionManagementViewModel)
        }
        item {
            ActionsWithBalance(transactionManagementViewModel, userViewModel, categoriesViewModel, LocalContext.current)
        }
        item {
            RecentTransactions(transactionManagementViewModel, transactionHistoryViewModel, navController) // Передайте navController
        }
    }
}