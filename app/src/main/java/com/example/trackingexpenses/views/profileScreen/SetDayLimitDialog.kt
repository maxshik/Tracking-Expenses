package com.example.trackingexpenses.views.profileScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trackingexpenses.R

@Composable
fun SetDayLimitDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit,
    context: Context
) {
    val sum = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.set_day_limit_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = stringResource(id = R.string.set_day_limit_dialog_message),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = sum.value,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            sum.value = it
                        }
                    },
                    label = { Text(stringResource(R.string.enter_money)) },
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

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel_button), color = MaterialTheme.colorScheme.tertiary)
                    }
                    Button(
                        onClick = {
                            if (sum.value.isEmpty()) {
                                showError.value = true
                            } else {
                                val limit = sum.value.toFloatOrNull() ?: 0f
                                onConfirm(limit)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.add), color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }

    if (showError.value) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, context.getString(R.string.please_enter_sum), Toast.LENGTH_SHORT).show()
            showError.value = false
        }
    }
}