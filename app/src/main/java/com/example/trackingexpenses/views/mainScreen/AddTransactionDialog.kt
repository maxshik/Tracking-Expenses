package com.example.trackingexpenses.views.mainScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trackingexpenses.R
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.mainScreen.viewModels.TransactionManagementViewModel
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES

@Composable
fun AddTransactionDialog(
    type: String,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    transactionManagementViewModel: TransactionManagementViewModel,
    categoriesViewModel: CategoriesViewModel,
    context: Context
) {
    var errorMessage by remember { mutableStateOf("") }
    var isNotesValid by remember { mutableStateOf(true) }

    val enterExpenseAmount = stringResource(id = R.string.enter_expense_amount)
    val enterIncomeAmount = stringResource(id = R.string.enter_income_amount)
    val notesError = stringResource(id = R.string.notes_error)
    val mandatoryFieldsError = stringResource(id = R.string.mandatory_fields_error)

    val enterMoney = if (type == EXPENSES) enterExpenseAmount else enterIncomeAmount

    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
                .padding(10.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = stringResource(id = R.string.add_transaction_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )

                TextField(
                    value = transactionManagementViewModel.coast.value,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            transactionManagementViewModel.coast.value = it
                        }
                    },
                    label = {
                        Text(enterMoney, color = MaterialTheme.colorScheme.tertiary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .border(0.5.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(5.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                DatePickerDocked(transactionManagementViewModel)

                CategoryMenu(type, categoriesViewModel, transactionManagementViewModel)

                TextField(
                    value = transactionManagementViewModel.notes.value,
                    onValueChange = { newText ->
                        if (newText.length <= 50) {
                            transactionManagementViewModel.notes.value = newText
                            isNotesValid = true
                        } else {
                            isNotesValid = false
                            Toast.makeText(
                                context,
                                notesError,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    label = {
                        Text(stringResource(id = R.string.notes_label), color = MaterialTheme.colorScheme.tertiary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                        .border(0.5.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(5.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel_button), color = MaterialTheme.colorScheme.tertiary)
                    }
                    Button(
                        onClick = {
                            if (transactionManagementViewModel.coast.value.isEmpty() ||
                                transactionManagementViewModel.category.value.isEmpty()
                            ) {
                                errorMessage = mandatoryFieldsError
                            } else {
                                errorMessage = ""
                                onReset()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.add_button), color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}