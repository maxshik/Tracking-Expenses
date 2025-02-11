package com.example.trackingexpenses.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.trackingexpenses.R
import com.example.trackingexpenses.objects.ChartTypes.POPULAR_EXPENSES_BAR_CHART
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.views.BottomNavigationBar
import com.example.trackingexpenses.views.UpperMenu
import com.example.trackingexpenses.views.graphicsScreen.GraphicScreen

class GraphicsActivity : ComponentActivity() {
    private val transactionHistoryViewModel: TransactionHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TrackingExpensesTheme {
                Scaffold(content = { innerPadding ->
                    GraphicScreen(
                        Modifier.padding(innerPadding),
                        resources.getStringArray(R.array.charts_categories).toSet(),
                        transactionHistoryViewModel
                    )
                }, bottomBar = {
                    BottomNavigationBar(GraphicsActivity::class.java, this)
                }, topBar = {
                    UpperMenu(getString(R.string.graphics))
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        transactionHistoryViewModel.currentSortType.value =
            POPULAR_EXPENSES_BAR_CHART

        transactionHistoryViewModel.fetchPopularCategoriesOfExpenses()
        transactionHistoryViewModel.fetchTransactionsLast30Days()
        transactionHistoryViewModel.fetchPeriodsData()
    }
}