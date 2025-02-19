package com.example.trackingexpenses.views.mainScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.viewModels.UserViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneralInformation(userViewModel: UserViewModel, transactionManagementViewModel: TransactionManagementViewModel) {
    val incomeForPeriod by userViewModel.userIncomeForPeriod.collectAsState()
    val expensesForPeriod by userViewModel.userExpensesForPeriod.collectAsState()
    val userExpensesForDay by userViewModel.userExpensesForDay.collectAsState()
    val dayLimit by userViewModel.dayLimit.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var colorOfTodayExpenses = MaterialTheme.colorScheme.tertiary

    if (transactionManagementViewModel.isLimitExceeded.value) {
        colorOfTodayExpenses = androidx.compose.ui.graphics.Color.Red
    }

    transactionManagementViewModel.checkLimit(userExpensesForDay, dayLimit)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.general_information_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            Text(
                modifier = Modifier.padding(5.dp, end = 10.dp),
                text = stringResource(id = R.string.balance_text, incomeForPeriod - expensesForPeriod),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.W500
            )

            Text(
                modifier = Modifier.padding(5.dp, end = 10.dp),
                text = stringResource(id = R.string.income_text, incomeForPeriod),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.W500
            )

            Text(
                modifier = Modifier.padding(5.dp, end = 10.dp),
                text = stringResource(id = R.string.expenses_text, expensesForPeriod),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.W500
            )

            Text(
                modifier = Modifier.padding(5.dp, end = 10.dp),
                text = stringResource(id = R.string.expenses_today_text, userExpensesForDay),
                style = MaterialTheme.typography.titleMedium,
                color = colorOfTodayExpenses,
                fontWeight = FontWeight.W500
            )
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.start_new_period),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }

    if (showDialog) {
        ConfirmationDialogToIncreasePeriod(
            onDismiss = { showDialog = false },
            onConfirm = {
                userViewModel.increasePeriod()
                showDialog = false
            }
        )
    }
}