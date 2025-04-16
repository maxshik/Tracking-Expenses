package com.example.trackingexpenses.views.familyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.trackingexpenses.R
import com.example.trackingexpenses.models.FamilyMember

@Composable
fun FamilyMemberItem(
    familyMember: FamilyMember,
    onSettingsClick: () -> Unit,
    isItCardForAdmin: Boolean = true
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(familyMember.img),
                contentDescription = null,
                Modifier
                    .size(60.dp)
                    .padding(6.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Name: ${familyMember.name ?: "Unknown"}",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = "Email: ${familyMember.email}",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = "Status: ${familyMember.status}",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(2.dp)
                )
            }

            if (isItCardForAdmin) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.settings),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}