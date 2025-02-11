package com.example.trackingexpenses.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.trackingexpenses.views.BottomNavigationBar
import com.example.trackingexpenses.views.UpperMenu
import com.example.trackingexpenses.views.mainScreen.ActionsWithBalance
import com.example.trackingexpenses.views.mainScreen.GeneralInformation
import com.example.trackingexpenses.views.mainScreen.RecentTransactions
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.lifecycle.ViewModelProvider
import com.example.trackingexpenses.R
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.mainScreen.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.viewModels.TransactionManagementViewModelFactory
import com.example.trackingexpenses.viewModels.UserViewModel

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
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

        transactionHistoryViewModel.fetchRecentTransactions()
        userViewModel.fetchUserData()
        userViewModel.checkIfTodayIsTheDayToUpdateSpendingColumnForToday()

        val currentDate =
            LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru", "RU")))

        setContent {
            TrackingExpensesTheme() {
                Scaffold(
                    content = { innerPadding ->
                        MainScreen(
                            transactionManagementViewModel,
                            userViewModel,
                            categoriesViewModel,
                            transactionHistoryViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(MainActivity::class.java, this)
                    },
                    topBar = {
                        UpperMenu("${getString(R.string.my_finance)} | $currentDate")
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    transactionManagementViewModel: TransactionManagementViewModel,
    userViewModel: UserViewModel,
    categoriesViewModel: CategoriesViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    modifier: Modifier,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            GeneralInformation(userViewModel)
        }
        item {
            ActionsWithBalance(transactionManagementViewModel, userViewModel, categoriesViewModel, context)
        }
        item {
            RecentTransactions(transactionManagementViewModel, transactionHistoryViewModel,context)
        }
        item {
        }
    }
}