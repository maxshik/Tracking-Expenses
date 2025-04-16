package com.example.trackingexpenses.views.mainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import com.example.trackingexpenses.R
import com.example.trackingexpenses.mainScreen.viewModels.CategoriesViewModel
import com.example.trackingexpenses.objects.TypeOfTransactions.EXPENSES
import com.example.trackingexpenses.objects.TypeOfTransactions.INCOME
import com.example.trackingexpenses.viewModels.TransactionManagementViewModel

@Composable
fun CategoryMenu(type: String, categoriesViewModel: CategoriesViewModel, transactionManagementViewModel: TransactionManagementViewModel) {

    val categories by when (type) {
        EXPENSES -> categoriesViewModel.categoriesOfExpenditure.observeAsState(initial = emptyList())
        INCOME -> categoriesViewModel.categoriesOfIncome.observeAsState(initial = emptyList())
        else -> remember { mutableStateOf(emptyList<String>()) }
    }

    var expanded by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(categories) {
        suggestions = categories
    }

    val icon: ImageVector =
        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = transactionManagementViewModel.category.value,
        onValueChange = { transactionManagementViewModel.category.value = it.trim() },
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                textfieldSize = coordinates.size.toSize()
            },
        label = { Text(stringResource(R.string.choice_category)) },
        trailingIcon = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.clickable { expanded = !expanded },
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            unfocusedLabelColor = MaterialTheme.colorScheme.tertiary
        ),
        interactionSource = interactionSource
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.width(with(LocalDensity.current) { textfieldSize.width.toDp() })
    ) {
        suggestions.forEach { suggestion ->
            DropdownMenuItem(onClick = {
                transactionManagementViewModel.category.value = suggestion
                expanded = false
            },
                text = { Text(suggestion, style = MaterialTheme.typography.bodyMedium)  }
            )
        }
    }
}