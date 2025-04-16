package com.example.trackingexpenses.views.familyScreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.trackingexpenses.objects.TypesOfAccount.FAMILY
import com.example.trackingexpenses.objects.TypesOfAccount.USER
import com.example.trackingexpenses.viewModels.FamilyViewModel

@Composable
fun FamilyScreen(
    modifier: Modifier,
    familyViewModel: FamilyViewModel,
    context: Context,
) {
    val currentTypeOfAccount by familyViewModel.currentTypeOfAccount.collectAsState()
    Log.i("TestTest", currentTypeOfAccount)

    when (currentTypeOfAccount) {
        USER -> FamilyInitScreen(familyViewModel, context, modifier)
        FAMILY -> FamilyAccountScreen(familyViewModel, context, modifier)
        else -> FamilyMemberScreen(familyViewModel, modifier)
    }
}