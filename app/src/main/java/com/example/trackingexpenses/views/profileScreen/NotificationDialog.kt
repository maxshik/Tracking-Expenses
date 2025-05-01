package com.example.trackingexpenses.views.profileScreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trackingexpenses.NotificationScheduler
import com.example.trackingexpenses.R
import com.example.trackingexpenses.views.PrimaryButton
import java.util.Calendar

@Composable
fun NotificationDialog(
    scheduler: NotificationScheduler,
    context: Context,
    onDismiss: () -> Unit,
) {
    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    val message = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Создание кастомных уведомлений",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Выберите время и введите текст сообщения",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(Modifier.fillMaxWidth().padding(2.dp)) {
                    PrimaryButton("Выбрать дату", 0.5f, {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, year, month, dayOfMonth ->
                            selectedDate.value.set(year, month, dayOfMonth)
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    })

                    Spacer(Modifier.width(4.dp))

                    PrimaryButton("Выбрать время", 1f, {
                        val hour = selectedDate.value.get(Calendar.HOUR_OF_DAY)
                        val minute = selectedDate.value.get(Calendar.MINUTE)
                        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                            selectedDate.value.set(Calendar.HOUR_OF_DAY, selectedHour)
                            selectedDate.value.set(Calendar.MINUTE, selectedMinute)
                        }, hour, minute, true).show()
                    })
                }

                Text(
                    text = "Текущее значение: ${selectedDate.value.get(Calendar.DAY_OF_MONTH)}/${selectedDate.value.get(Calendar.MONTH) + 1}/${selectedDate.value.get(Calendar.YEAR)} ${selectedDate.value.get(Calendar.HOUR_OF_DAY)}:${String.format("%02d", selectedDate.value.get(Calendar.MINUTE))}",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    label = { Text("Введите сообщение") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
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
                            scheduler.scheduleNotification(context, selectedDate.value.timeInMillis, message.value)
                            Log.i("Time", selectedDate.value.timeInMillis.toString())
                            Log.i("Message", message.value)
                            Log.e("!!!", message.value)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.send),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}