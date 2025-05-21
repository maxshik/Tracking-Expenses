package com.example.trackingexpenses.views.historyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.views.SelectSortTypeMenu
import com.example.trackingexpenses.views.TransactionListItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    modifier: Modifier,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    transactionManagementViewModel: TransactionManagementViewModel,
) {
    val allTransactions by transactionHistoryViewModel.allTransactions.observeAsState(emptyList())
    val selectedSortType by transactionHistoryViewModel.currentSortType

    // Trigger fetchAndFilterTransactions when selectedSortType changes
    LaunchedEffect(selectedSortType) {
        transactionHistoryViewModel.fetchAndFilterTransactions(selectedSortType)
    }

    val groupedTransactions = allTransactions.groupBy { transaction ->
        LocalDate.parse(
            transaction.date,
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("ru", "RU"))
        )
    }.toSortedMap(reverseOrder())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp)
    ) {
        item {
            SelectSortTypeMenu(
                transactionHistoryViewModel,
                stringArrayResource(id = R.array.transaction_categories).toSet()
            )
        }

        if (groupedTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.box),
                            contentDescription = stringResource(id = R.string.no_transactions),
                            modifier = Modifier.wrapContentHeight()
                        )
                        Text(
                            text = stringResource(id = R.string.no_transactions),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        } else {
            groupedTransactions.forEach { (date, transactions) ->
                item {
                    Text(
                        text = date.format(
                            DateTimeFormatter.ofPattern(
                                "d MMMM yyyy",
                                Locale("ru", "RU")
                            )
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                transactions.forEach { transaction ->
                    item {
                        TransactionListItem(transaction, transactionManagementViewModel)
                    }
                }
            }
        }
    }
}