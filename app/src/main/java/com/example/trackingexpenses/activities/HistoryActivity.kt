package com.example.trackingexpenses.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.trackingexpenses.R
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.views.BottomNavigationBar
import com.example.trackingexpenses.mainScreen.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.objects.SortTypesInHistoryActivity.ALL_TRANSACTIONS
import com.example.trackingexpenses.views.UpperMenu
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.viewModels.TransactionManagementViewModelFactory
import com.example.trackingexpenses.views.historyScreen.HistoryScreen

class HistoryActivity : ComponentActivity() {
    private val transactionHistoryViewModel: TransactionHistoryViewModel by viewModels()
    private val categoriesViewModel: CategoriesViewModel by viewModels()

    private lateinit var transactionManagementViewModel: TransactionManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        transactionManagementViewModel = ViewModelProvider(
            this,
            TransactionManagementViewModelFactory(categoriesViewModel)
        ).get(TransactionManagementViewModel::class.java)

        setContent {
            TrackingExpensesTheme() {
                Scaffold(
                    content = { innerPadding ->
                        HistoryScreen(
                            modifier = Modifier.padding(innerPadding),
                            transactionHistoryViewModel,
                            transactionManagementViewModel,
                            LocalContext.current
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(HistoryActivity::class.java, this)
                    },
                    topBar = {
                        UpperMenu(getString(R.string.transaction_history))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        transactionHistoryViewModel.currentSortType.value = ALL_TRANSACTIONS
    }
}