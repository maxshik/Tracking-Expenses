package com.example.trackingexpenses.views.mainScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trackingexpenses.R
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.models.Transaction
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES
import com.example.trackingexpenses.objects.TypeOfTransactions.INCOME
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.viewModels.UserViewModel
import com.example.trackingexpenses.views.PrimaryButton
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun ActionsWithBalance(
    transactionManagementViewModel: TransactionManagementViewModel,
    userViewModel: UserViewModel,
    categoriesViewModel: CategoriesViewModel,
    context: Context,
) {
    val showDialogWithAddExpenditure = remember { mutableStateOf(false) }
    val showDialogWithAddIncome = remember { mutableStateOf(false) }

    fun zeroingOutFields() {
        val selectedDate =
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("ru", "RU")))

        with(transactionManagementViewModel) {
            coast.value = ""
            notes.value = ""
            date.value = selectedDate
            category.value = ""
        }
    }

    val dateString = transactionManagementViewModel.date.value
    val transactionDate = if (dateString.isNotEmpty()) {
        LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } else {
        LocalDate.now()
    }

    val transactionTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    val dateTime = Timestamp(
        Date.from(
            transactionDate.atTime(LocalTime.parse(transactionTime)).atZone(ZoneId.systemDefault())
                .toInstant()
        )
    )

    if (showDialogWithAddExpenditure.value) {
        AddTransactionDialog(
            EXPENSES,
            onDismiss = { showDialogWithAddExpenditure.value = false },
            onReset = {
                val newExpenditure = Transaction(
                    coast = transactionManagementViewModel.coast.value,
                    notes = transactionManagementViewModel.notes.value.trim(),
                    date = transactionManagementViewModel.date.value,
                    time = transactionTime,
                    category = transactionManagementViewModel.category.value,
                    type = EXPENSES,
                    dateTime = dateTime,
                    period = userViewModel.userCurrentPeriod.intValue
                )

                transactionManagementViewModel.addTransaction(
                    EXPENSES,
                    transactionManagementViewModel.category.value,
                    newExpenditure,
                ) { documentId ->
                    Log.i("Test", documentId)
                }
                showDialogWithAddExpenditure.value = false

                zeroingOutFields()
            },
            transactionManagementViewModel,
            categoriesViewModel,
            context
        )
    }

    if (showDialogWithAddIncome.value) {
        AddTransactionDialog(
            INCOME,
            onDismiss = { showDialogWithAddIncome.value = false },
            onReset = {
                val newIncome = Transaction(
                    coast = transactionManagementViewModel.coast.value,
                    notes = transactionManagementViewModel.notes.value.trim(),
                    date = transactionManagementViewModel.date.value,
                    time = transactionTime,
                    category = transactionManagementViewModel.category.value,
                    type = INCOME,
                    dateTime = dateTime,
                    period = userViewModel.userCurrentPeriod.intValue
                )

                transactionManagementViewModel.addTransaction(
                    INCOME,
                    transactionManagementViewModel.category.value,
                    newIncome
                ) { documentId ->
                    Log.i("Test", documentId)
                }

                showDialogWithAddIncome.value = false

                zeroingOutFields()
            },
            transactionManagementViewModel,
            categoriesViewModel,
            context
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
    ) {

        PrimaryButton(
            buttonText = stringResource(R.string.add_income),
            buttonSize = 0.45f,
            onClick = {
                showDialogWithAddIncome.value = true
            }
        )

        PrimaryButton(
            buttonText = stringResource(R.string.add_expenses),
            buttonSize = 0.85f,
            onClick = {
                showDialogWithAddExpenditure.value = true
            }
        )
    }
}