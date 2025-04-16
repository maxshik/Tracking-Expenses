package com.example.trackingexpenses.views.profileScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.yml.charts.common.extensions.isNotNull
import coil.compose.rememberAsyncImagePainter
import com.example.trackingexpenses.NotificationScheduler
import com.example.trackingexpenses.R
import com.example.trackingexpenses.objects.Routes
import com.example.trackingexpenses.viewModels.UserViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier,
    userViewModel: UserViewModel,
    context: Context,
    navHostController: NavHostController,
    scrollState: ScrollState,
) {
    val userPhotoUrl by userViewModel.userPhotoUrl.collectAsState()
    val userEmail by userViewModel.userEmail.collectAsState()
    val userDisplayName by userViewModel.userDisplayName.collectAsState()
    val userFamilyId by userViewModel.usersFamilyId.collectAsState()
    val showDialogToAddDayLimit = remember { mutableStateOf(false) }
    val dayLimit by userViewModel.dayLimit.collectAsState()
    val showNotificationDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserData()
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
        ) {
            if (userPhotoUrl.isNotNull()) {
                Image(
                    painter = rememberAsyncImagePainter(userPhotoUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.man),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )

            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!userDisplayName.isNullOrEmpty()) {
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

        InformationAboutSpendingForAllTime(userViewModel)

        if (showDialogToAddDayLimit.value) {
            SetDayLimitDialog({ showDialogToAddDayLimit.value = false }, onConfirm = { limit ->
                userViewModel.setDayLimit(limit)
                showDialogToAddDayLimit.value = false
            }, LocalContext.current, dayLimit)
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            ButtonInProfileScreen(R.drawable.notifications,
                stringResource(id = R.string.notifications_button),
                {
                    showNotificationDialog.value = true
                })
            ButtonInProfileScreen(R.drawable.limit,
                stringResource(id = R.string.set_limit_button),
                {
                    showDialogToAddDayLimit.value = true
                })
            ButtonInProfileScreen(R.drawable.about,
                stringResource(id = R.string.send_error),
                {
                    val telegramUri = Uri.parse(context.getString(R.string.telegram_of_creator))
                    val intent = Intent(Intent.ACTION_VIEW, telegramUri)
                    context.startActivity(intent)
                })
            ButtonInProfileScreen(
                R.drawable.github,
                stringResource(id = R.string.github_button),
            ) {
                val url = context.getString(R.string.github_link)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                context.startActivity(intent)
            }
            ButtonInProfileScreen(R.drawable.parentsandchild,
                stringResource(id = R.string.family),
                {
                    navHostController.navigate(Routes.FAMILY)
                })
            ButtonInProfileScreen(R.drawable.exit,
                stringResource(id = R.string.sign_out_button),
                {
                    userViewModel.signOut(context)
                })
        }

        if (showNotificationDialog.value) {
            NotificationDialog(
                NotificationScheduler(),
                context,
                { showNotificationDialog.value = false })
        }
    }
}
