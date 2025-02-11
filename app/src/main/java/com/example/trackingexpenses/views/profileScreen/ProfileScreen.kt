package com.example.trackingexpenses.views.profileScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.extensions.isNotNull
import coil.compose.rememberAsyncImagePainter
import com.example.trackingexpenses.R
import com.example.trackingexpenses.viewModels.UserViewModel

@Composable
fun ProfileScreen(modifier: Modifier, userViewModel: UserViewModel, context: Context) {
    val userPhotoUrl by userViewModel.userPhotoUrl.collectAsState()
    val userEmail by userViewModel.userEmail.collectAsState()
    val userDisplayName by userViewModel.userDisplayName.collectAsState()

    LazyColumn(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            ) {
                if (userPhotoUrl.isNotNull()) {
                    Image(
                        painter = rememberAsyncImagePainter(userPhotoUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.man),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (userDisplayName.isNotNull()) {
                Text(
                    text = userDisplayName.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.W700
                )

                Text(
                    text = userEmail.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.W500
                )
            } else {
                Text(
                    text = userEmail.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.W700
                )
            }
        }

        item {
            InformationAboutSpendingForAllTime(userViewModel)
        }

        item {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                ButtonInProfileScreen(R.drawable.notifications, stringResource(id = R.string.notifications_button), {})
                ButtonInProfileScreen(R.drawable.limit, stringResource(id = R.string.set_limit_button), {})
                ButtonInProfileScreen(R.drawable.about, stringResource(id = R.string.about_button), {})
                ButtonInProfileScreen(R.drawable.github, stringResource(id = R.string.github_button), {})
                ButtonInProfileScreen(R.drawable.exit, stringResource(id = R.string.sign_out_button), {
                    userViewModel.signOut(context)
                })
            }
        }
    }
}
