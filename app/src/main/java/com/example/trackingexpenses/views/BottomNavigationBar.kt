package com.example.trackingexpenses.views

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.trackingexpenses.R
import com.example.trackingexpenses.models.BottomNavigationItem
import com.example.trackingexpenses.objects.Routes

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String, context: Context) {
    val items = listOf(
        BottomNavigationItem(
            title = context.getString(R.string.main),
            icon = R.drawable.homepage,
            route = Routes.MAIN_SCREEN
        ),
        BottomNavigationItem(
            title = context.getString(R.string.history),
            icon = R.drawable.history,
            route = Routes.HISTORY
        ),
        BottomNavigationItem(
            title = context.getString(R.string.graphics),
            icon = R.drawable.graphic,
            route = Routes.GRAPHICS
        ),
        BottomNavigationItem(
            title = context.getString(R.string.profile),
            icon = R.drawable.profile,
            route = Routes.PROFILE
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                icon = {
                    BadgedBox(badge = { }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = item.icon),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}