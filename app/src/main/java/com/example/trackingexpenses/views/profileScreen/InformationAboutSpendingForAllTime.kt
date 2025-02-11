package com.example.trackingexpenses.views.profileScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.UserViewModel

@Composable
fun InformationAboutSpendingForAllTime(userViewModel: UserViewModel) {
    val userTotalExpenditure by userViewModel.userTotalExpenditure.collectAsState()
    val userTotalIncome by userViewModel.userTotalIncome.collectAsState()
    val spendInPercent by userViewModel.spendInPercent.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .border(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            modifier = Modifier.padding(12.dp, bottom = 2.dp, top = 12.dp),
            text = stringResource(id = R.string.statistics_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.W500
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row() {
                ProgressBar(
                    progress = spendInPercent, color = Color(0xFF4169E1)
                )
                ProgressBar(
                    progress = 1f, color = Color(0xFF7FFF00)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box() {
                    ProgressBar(.055f, Color(0xFF4169E1))
                }

                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = stringResource(id = R.string.spent_text, userTotalExpenditure),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.W500
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box() {
                    ProgressBar(.055f, Color(0xFF7FFF00))
                }

                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = stringResource(id = R.string.earned_text, userTotalIncome),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}
