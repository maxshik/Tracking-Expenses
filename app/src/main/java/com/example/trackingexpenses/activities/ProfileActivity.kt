package com.example.trackingexpenses.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.trackingexpenses.R
import com.example.trackingexpenses.views.BottomNavigationBar
import com.example.trackingexpenses.views.UpperMenu
import com.example.trackingexpenses.ui.theme.TrackingExpensesTheme
import com.example.trackingexpenses.viewModels.UserViewModel
import com.example.trackingexpenses.views.profileScreen.ProfileScreen

class ProfileActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TrackingExpensesTheme {
                Scaffold(content = { innerPadding ->
                    ProfileScreen(Modifier.padding(innerPadding), userViewModel, this)
                }, bottomBar = {
                    BottomNavigationBar(ProfileActivity::class.java, this)
                }, topBar = {
                    UpperMenu(getString(R.string.profile))
                })
            }
        }
    }
}