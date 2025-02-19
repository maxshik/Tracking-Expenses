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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trackingexpenses.R
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.viewModels.TransactionManagementViewModelFactory
import com.example.trackingexpenses.viewModels.UserViewModel
import com.example.trackingexpenses.views.BottomNavigationBar
import com.example.trackingexpenses.views.UpperMenu
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.views.graphicsScreen.GraphicScreen
import com.example.trackingexpenses.views.historyScreen.HistoryScreen
import com.example.trackingexpenses.views.mainScreen.MainScreen
import com.example.trackingexpenses.views.profileScreen.ProfileScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
            TrackingExpensesTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "mainScreen") {
                    composable("mainScreen") {
                        Scaffold(
                            content = { innerPadding ->
                                MainScreen(
                                    transactionManagementViewModel,
                                    userViewModel,
                                    categoriesViewModel,
                                    transactionHistoryViewModel,
                                    modifier = Modifier.padding(innerPadding),
                                    navController
                                )
                            },
                            bottomBar = {
                                BottomNavigationBar(navController, "mainScreen")
                            },
                            topBar = {
                                UpperMenu("${getString(R.string.my_finance)} | $currentDate")
                            }
                        )
                    }

                    composable("history") {
                        Scaffold(
                            content = { innerPadding ->
                                HistoryScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    transactionHistoryViewModel,
                                    transactionManagementViewModel
                                )
                            },
                            bottomBar = {
                                BottomNavigationBar(navController, "history")
                            },
                            topBar = {
                                UpperMenu(getString(R.string.transaction_history))
                            }
                        )
                    }

                    composable("graphics") {
                        Scaffold(content = { innerPadding ->
                            GraphicScreen(
                                Modifier.padding(innerPadding),
                                resources.getStringArray(R.array.charts_categories).toSet(),
                                transactionHistoryViewModel
                            )
                        }, bottomBar = {
                            BottomNavigationBar(navController, "graphics")
                        }, topBar = {
                            UpperMenu(getString(R.string.graphics))
                        })

                    }

                    composable("profile") {
                        Scaffold(content = { innerPadding ->
                            ProfileScreen(
                                Modifier.padding(innerPadding),
                                userViewModel,
                                LocalContext.current
                            )
                        }, bottomBar = {
                            BottomNavigationBar(navController, "profile")
                        }, topBar = {
                            UpperMenu(getString(R.string.profile))
                        })
                    }
                }
            }
        }
    }
}