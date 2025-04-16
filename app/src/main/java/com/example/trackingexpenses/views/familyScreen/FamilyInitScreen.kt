package com.example.trackingexpenses.views.familyScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.trackingexpenses.DefaultDialogFragment
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.FamilyViewModel
import com.example.trackingexpenses.views.PrimaryButton

@Composable
fun FamilyInitScreen(familyViewModel: FamilyViewModel, context: Context, modifier: Modifier) {
    var showDialogAboutCreationFamily by remember { mutableStateOf(false) }
    var showSendQueryDialog by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf(false) }
    var showErrorToast by remember { mutableStateOf(false) }

    val buttons = listOf(
        Pair("Создать семью") { showDialogAboutCreationFamily = true },
        Pair("Отправить запрос") { showSendQueryDialog = true }
    )

    LazyColumn(modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PrimaryButton(
                        buttonText = buttons[0].first,
                        buttonSize = 0.5f,
                        onClick = buttons[0].second
                    )

                    PrimaryButton(
                        buttonText = buttons[1].first,
                        buttonSize = 0.9f,
                        onClick = buttons[1].second
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.customer),
                        contentDescription = stringResource(R.string.no_members),
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = stringResource(R.string.information_about_family),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }

    if (showDialogAboutCreationFamily) {
        DefaultDialogFragment(
            onDismiss = { showDialogAboutCreationFamily = false },
            onConfirm = {
                familyViewModel.createFamilyAccount()
                showSuccessToast = true
            },
            stringResource(id = R.string.confirmation_dialog_message_when_user_create_family),
            stringResource(id = R.string.warning_about_effect_after_creation_family),
            stringResource(id = R.string.create)
        )
    }

    if (showSendQueryDialog) {
        SendQueryDialog(
            onDismiss = { showSendQueryDialog = false },
            onConfirm = { inputValue ->
                familyViewModel.sendQueryToJoinInTheFamily(inputValue) { success ->
                    if (success) {
                        showSuccessToast = true
                    } else {
                        showErrorToast = true
                    }
                }
            },
            context
        )
    }

    // Показать Toast при успешной операции
    if (showSuccessToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, context.getString(R.string.success_information_about_send_query), Toast.LENGTH_SHORT).show()
            showSuccessToast = false // Сброс состояния после показа
        }
    }

    // Показать Toast при ошибке
    if (showErrorToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, context.getString(R.string.error_send_query), Toast.LENGTH_SHORT).show()
            showErrorToast = false // Сброс состояния после показа
        }
    }
}