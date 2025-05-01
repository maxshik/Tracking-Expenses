package com.example.trackingexpenses.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trackingexpenses.DefaultDialogFragment
import com.example.trackingexpenses.R
import com.example.trackingexpenses.models.Transaction
import com.example.trackingexpenses.ui.theme.MontserratFontFamily
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListItem(
    transaction: Transaction,
    transactionManagementViewModel: TransactionManagementViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val category = stringResource(id = R.string.category)

    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .combinedClickable(
                onClick = {
                    Toast.makeText(
                        context,
                        "$category: ${transaction.category}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onLongClick = { /* Долгий клик больше не нужен, но оставим для возможного будущего использования */ }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = checkTransactionForReturnImage(context, transaction.category)),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(6.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, fontFamily = MontserratFontFamily)) {
                            append("${transaction.type}: ")
                        }
                        append("${transaction.coast} BYN")
                    },
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(2.dp),
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Light
                )
                Text(
                    modifier = Modifier.padding(2.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, fontFamily = MontserratFontFamily)) {
                            append(stringResource(id = R.string.date) + ": ")
                        }
                        append("${transaction.date} ${transaction.time}")
                    },
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Light
                )

                if (transaction.notes.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, fontFamily = MontserratFontFamily)) {
                                append(stringResource(id = R.string.notes) + ": ")
                            }
                            append(transaction.notes)
                        },
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            Row {
                IconButton(onClick = {
                    showEditDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit),
                        tint = MaterialTheme.colorScheme.errorContainer
                    )
                }
                IconButton(onClick = {
                    showDeleteDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        DefaultDialogFragment(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                transactionManagementViewModel.deleteTransaction(transaction.id)
            },
            stringResource(id = R.string.confirmation_dialog_for_delete),
            null,
            stringResource(id = R.string.delete)
        )
    }

    if (showEditDialog) {
        EditTransactionDialog(
            transaction = transaction,
            transactionManagementViewModel = transactionManagementViewModel,
            context = context,
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    transactionManagementViewModel: TransactionManagementViewModel,
    context: Context,
    onDismiss: () -> Unit
) {
    val coast = remember { mutableStateOf(transaction.coast.toString()) }
    val notes = remember { mutableStateOf(transaction.notes) }
    val selectedDate = remember { mutableStateOf(parseDateToCalendar(transaction.date, transaction.time)) }
    val category = remember { mutableStateOf(transaction.category) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.edit_transaction),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = coast.value,
                    onValueChange = { coast.value = it },
                    label = { Text(stringResource(id = R.string.amount)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                TextField(
                    value = category.value,
                    onValueChange = { category.value = it },
                    label = { Text(stringResource(id = R.string.category)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                TextField(
                    value = notes.value,
                    onValueChange = { notes.value = it },
                    label = { Text(stringResource(id = R.string.notes)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Row(Modifier.fillMaxWidth().padding(2.dp)) {
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                                selectedDate.value.set(year, month, dayOfMonth)
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.select_date))
                    }

                    Spacer(Modifier.width(4.dp))

                    Button(
                        onClick = {
                            val hour = selectedDate.value.get(Calendar.HOUR_OF_DAY)
                            val minute = selectedDate.value.get(Calendar.MINUTE)
                            TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                                selectedDate.value.set(Calendar.HOUR_OF_DAY, selectedHour)
                                selectedDate.value.set(Calendar.MINUTE, selectedMinute)
                            }, hour, minute, true).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.select_time))
                    }
                }

                Text(
                    text = "Текущее значение: ${selectedDate.value.get(Calendar.DAY_OF_MONTH)}/${selectedDate.value.get(Calendar.MONTH) + 1}/${selectedDate.value.get(Calendar.YEAR)} ${selectedDate.value.get(Calendar.HOUR_OF_DAY)}:${String.format("%02d", selectedDate.value.get(Calendar.MINUTE))}",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(id = R.string.cancel_button),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Button(
                        onClick = {
                            val updatedCoast = coast.value.toDoubleOrNull() ?: transaction.coast
                            val updatedTransaction = transaction.copy(
                                coast = updatedCoast.toString(),
                                category = category.value,
                                notes = notes.value,
                                date = String.format(
                                    "%02d/%02d/%d",
                                    selectedDate.value.get(Calendar.DAY_OF_MONTH),
                                    selectedDate.value.get(Calendar.MONTH) + 1,
                                    selectedDate.value.get(Calendar.YEAR)
                                ),
                                time = String.format(
                                    "%02d:%02d",
                                    selectedDate.value.get(Calendar.HOUR_OF_DAY),
                                    selectedDate.value.get(Calendar.MINUTE)
                                )
                            )
                            transactionManagementViewModel.updateTransaction(updatedTransaction)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.save),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

private fun parseDateToCalendar(date: String, time: String): Calendar {
    val calendar = Calendar.getInstance()
    val dateParts = date.split("/")
    val timeParts = time.split(":")
    if (dateParts.size == 3 && timeParts.size == 2) {
        calendar.set(
            dateParts[2].toInt(),
            dateParts[1].toInt() - 1,
            dateParts[0].toInt(),
            timeParts[0].toInt(),
            timeParts[1].toInt()
        )
    }
    return calendar
}

fun checkTransactionForReturnImage(context: Context, category: String): Int {
    return when (category) {
        context.getString(R.string.category_liquor) -> R.drawable.liquor
        context.getString(R.string.category_bookshelf) -> R.drawable.bookshelf
        context.getString(R.string.category_ticket) -> R.drawable.ticket
        context.getString(R.string.category_gadget) -> R.drawable.gadget
        context.getString(R.string.category_family) -> R.drawable.family
        context.getString(R.string.category_book) -> R.drawable.book
        context.getString(R.string.category_cosmetics) -> R.drawable.cosmetics
        context.getString(R.string.category_cooking) -> R.drawable.cooking
        context.getString(R.string.category_analysis) -> R.drawable.analysis
        context.getString(R.string.category_room) -> R.drawable.room
        context.getString(R.string.category_medical) -> R.drawable.medical
        context.getString(R.string.category_maintenance) -> R.drawable.maintenance
        context.getString(R.string.category_clothes) -> R.drawable.clothes
        context.getString(R.string.category_barber) -> R.drawable.barber
        context.getString(R.string.category_products) -> R.drawable.products
        context.getString(R.string.category_tv) -> R.drawable.tv
        context.getString(R.string.category_sports) -> R.drawable.sports
        context.getString(R.string.category_fitness) -> R.drawable.fitness
        context.getString(R.string.category_taxi) -> R.drawable.taxi
        context.getString(R.string.category_skills) -> R.drawable.skills
        context.getString(R.string.category_customer_service) -> R.drawable.customer_service
        context.getString(R.string.category_siren) -> R.drawable.siren
        context.getString(R.string.category_penalty) -> R.drawable.penalty
        context.getString(R.string.category_hobby) -> R.drawable.hobby
        context.getString(R.string.category_home) -> R.drawable.home
        context.getString(R.string.category_gift) -> R.drawable.gift
        context.getString(R.string.category_walk) -> R.drawable.walk
        context.getString(R.string.category_health) -> R.drawable.health
        context.getString(R.string.category_rent) -> R.drawable.rent
        context.getString(R.string.category_bonus) -> R.drawable.bonus
        context.getString(R.string.category_dividend) -> R.drawable.dividend
        context.getString(R.string.category_salary) -> R.drawable.salary
        context.getString(R.string.category_earning) -> R.drawable.earning
        context.getString(R.string.category_crowdfunding) -> R.drawable.crowdfunding
        context.getString(R.string.category_testament) -> R.drawable.testament
        context.getString(R.string.category_affiliate_program) -> R.drawable.affiliate_program
        context.getString(R.string.category_retirement) -> R.drawable.retirement
        context.getString(R.string.category_parttime) -> R.drawable.parttime
        context.getString(R.string.category_point) -> R.drawable.point
        context.getString(R.string.category_asset_management) -> R.drawable.asset_management
        context.getString(R.string.category_scholarship) -> R.drawable.scholarship
        context.getString(R.string.category_stock_market) -> R.drawable.stock_market
        context.getString(R.string.category_freelance) -> R.drawable.freelance
        context.getString(R.string.category_deposit) -> R.drawable.deposit
        context.getString(R.string.category_success) -> R.drawable.success
        else -> R.drawable.idk
    }
}