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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trackingexpenses.DefaultDialogFragment
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.FamilyViewModel

@Composable
fun FamilyAccountScreen(familyViewModel: FamilyViewModel, context: Context, modifier: Modifier) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val familyMembers = familyViewModel.userFamilyMembers.collectAsState().value

    LazyColumn(modifier.fillMaxSize()) {
        val buttons = listOf(
            Pair("Отправить приглашение") { familyViewModel.sendInviteToFamily(context) },
            Pair("Удалить семью") { showDeleteDialog = true },
        )
        item {
            Spacer(modifier = Modifier.height(20.dp))

            Column {
                buttons.chunked(2).forEach { rowButtons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowButtons.forEach { button ->
                            Button(
                                modifier = Modifier
                                    .weight(0.4f)
                                    .padding(4.dp)
                                    .wrapContentHeight(),
                                onClick = button.second,
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text(
                                    text = button.first,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.W700,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        item {
            Text(
                text = "Члены семьи",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            if (familyMembers.isEmpty()) {
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
                            text = stringResource(R.string.no_members),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        items(familyMembers) { member ->
            FamilyMemberItem(
                member,
                { familyViewModel.acceptUserToTheFamily(member.userId) }
            )
        }

        item {
            Text(
                text = "Семейный аккаунт - это возможность контролировать траты определнной группы людей на одном аккаунте. Для того, чтобы создать семью нажмите на соответствующую кнопку. Не используйте личный профиль для создания семьи: для этих нужд создайте отдельный аккаунт. Добавляя транзакции на аккаунта членов семьи они автоматически добавляются на семейный аккаунт. У него все те же функции, что и обычного пользователя.",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(4.dp)
            )
        }
    }

    if (showDeleteDialog) {
        DefaultDialogFragment(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                familyViewModel.deleteFamily()
                Toast.makeText(context, context.getString(R.string.delete_family_information), Toast.LENGTH_SHORT).show()
            },
            stringResource(id = R.string.are_you_sure_that_you_want_delete_family),
            stringResource(id = R.string.effect_after_delete_family),
            stringResource(id = R.string.delete)
        )
    }
}