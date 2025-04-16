package com.example.trackingexpenses.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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

@Composable
fun TransactionListItem(
    transaction: Transaction,
    transactionManagementViewModel: TransactionManagementViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val category = stringResource(id = R.string.category)

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

    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onClick = {
            Toast.makeText(
                context,
                 category + ": ${transaction.category}",
                Toast.LENGTH_SHORT
            ).show()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Image(
                painter = painterResource(id = checkTransactionForReturnImage(context, transaction.category)),
                contentDescription = null,
                Modifier
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
}