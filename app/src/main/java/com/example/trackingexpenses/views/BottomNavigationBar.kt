package com.example.trackingexpenses.views

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
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
import com.example.trackingexpenses.R
import com.example.trackingexpenses.activities.GraphicsActivity
import com.example.trackingexpenses.activities.HistoryActivity
import com.example.trackingexpenses.activities.MainActivity
import com.example.trackingexpenses.activities.ProfileActivity

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val activityClass: Class<out ComponentActivity>,
)

@Composable
fun BottomNavigationBar(currentActivity: Class<out ComponentActivity>, context: Context) {
    val items = listOf(
        BottomNavigationItem(
            title = context.getString(R.string.main),
            selectedIcon = R.drawable.homepage,
            activityClass = MainActivity::class.java
        ),
        BottomNavigationItem(
            title = context.getString(R.string.history),
            selectedIcon = R.drawable.history,
            activityClass = HistoryActivity::class.java
        ),
        BottomNavigationItem(
            title = context.getString(R.string.graphics),
            selectedIcon = R.drawable.graphic,
            activityClass = GraphicsActivity::class.java
        ),
        BottomNavigationItem(
            title = context.getString(R.string.profile),
            selectedIcon = R.drawable.profile,
            activityClass = ProfileActivity::class.java
        )
    )

    val selectedItemIndex = items.indexOfFirst { it.activityClass == currentActivity }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    if (currentActivity != item.activityClass) {
                        val i = Intent(context, item.activityClass).apply {
                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        context.startActivity(i)
                    }
                },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                icon = {
                    BadgedBox(badge = { }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = item.selectedIcon),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}