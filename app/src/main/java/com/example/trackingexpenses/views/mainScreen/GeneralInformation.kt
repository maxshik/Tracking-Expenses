package com.example.trackingexpenses.views.mainScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trackingexpenses.DefaultDialogFragment
import com.example.trackingexpenses.R
import com.example.trackingexpenses.ui.theme.MontserratFontFamily
import com.example.trackingexpenses.ui.theme.textColor
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.viewModels.UserViewModel

@Composable
fun GeneralInformation(
    userViewModel: UserViewModel,
    transactionManagementViewModel: TransactionManagementViewModel,
) {
    val incomeForPeriod by userViewModel.userIncomeForPeriod.collectAsState()
    val expensesForPeriod by userViewModel.userExpensesForPeriod.collectAsState()
    val userExpensesForDay by userViewModel.userExpensesForDay.collectAsState()
    val dayLimit by userViewModel.dayLimit.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    val colorOfTodayExpenses: Color;

    if (transactionManagementViewModel.isLimitExceeded.value) {
        Log.i("RED", transactionManagementViewModel.isLimitExceeded.value.toString())

        colorOfTodayExpenses = MaterialTheme.colorScheme.error
    } else {
        Log.i("WHITE", transactionManagementViewModel.isLimitExceeded.value.toString())

        colorOfTodayExpenses = MaterialTheme.colorScheme.tertiary
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
            fontWeight = FontWeight.Medium,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            BalanceText(
                stringResource(R.string.balance_text),
                incomeForPeriod - expensesForPeriod,
            )
            BalanceText(
                stringResource(R.string.income_text),
                incomeForPeriod,
            )
            BalanceText(
                stringResource(R.string.expenses_text),
                expensesForPeriod,
            )
            BalanceText(
                stringResource(R.string.expenses_today_text),
                userExpensesForDay,
                colorOfTodayExpenses
            )
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.tertiary

                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(id = R.string.start_new_period),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showDialog) {
        DefaultDialogFragment(
            onDismiss = { showDialog = false },
            onConfirm = {
                userViewModel.increasePeriod()
                showDialog = false
            },
            stringResource(id = R.string.confirmation_dialog_title),
            stringResource(id = R.string.confirmation_dialog_message),
            stringResource(id = R.string.start_button)
        )
    }
}

@Composable
fun BalanceText(
    annotationString: String,
    moneyAmount: Float,
    colorText: Color = MaterialTheme.colorScheme.tertiary,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(5.dp)
                .weight(1f), text = buildAnnotatedString {
                append(annotationString)
            }, style = MaterialTheme.typography.bodyMedium, color = colorText
        )

        Text(
            modifier = Modifier.padding(5.dp), text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium, fontSize = 22.sp
                    )
                ) {
                    append(" $moneyAmount BYN")
                }
            }, style = MaterialTheme.typography.bodyMedium, color = colorText
        )
    }
}

