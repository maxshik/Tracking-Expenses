package com.example.trackingexpenses.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel

@Composable
fun SelectSortTypeMenu(transactionHistoryViewModel: TransactionHistoryViewModel, categories: Set<String>) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon: ImageVector =
        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = transactionHistoryViewModel.currentSortType.value,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            label = { Text(stringResource(R.string.choice_category), style = MaterialTheme.typography.labelSmall) },
            trailingIcon = {
                Icon(icon, contentDescription = null, modifier = Modifier.clickable { expanded = !expanded })
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            categories.forEach { suggestion ->
                DropdownMenuItem(onClick = {
                    transactionHistoryViewModel.currentSortType.value = suggestion
                    expanded = false
                }, text = { Text(text = suggestion, style = MaterialTheme.typography.bodyMedium) })
            }
        }
    }
}