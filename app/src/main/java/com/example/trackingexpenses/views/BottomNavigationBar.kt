package com.example.trackingexpenses.views

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

data class BottomNavigationItem(
    val title: String,
    val icon: Int,
    val route: String,
)

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String) {
    val items = listOf(
        BottomNavigationItem(
            title = "Главная",
            icon = R.drawable.homepage,
            route = "mainScreen"
        ),
        BottomNavigationItem(
            title = "История",
            icon = R.drawable.history,
            route = "history"
        ),
        BottomNavigationItem(
            title = "Графики",
            icon = R.drawable.graphic,
            route = "graphics"
        ),
        BottomNavigationItem(
            title = "Профиль",
            icon = R.drawable.profile,
            route = "profile"
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