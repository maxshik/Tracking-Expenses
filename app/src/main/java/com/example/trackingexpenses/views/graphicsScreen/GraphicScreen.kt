package com.example.trackingexpenses.views.graphicsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.trackingexpenses.objects.ChartTypes
import com.example.trackingexpenses.objects.ChartTypes.POPULAR_EXPENSES_BAR_CHART
import com.example.trackingexpenses.objects.SortTypesInHistoryActivity.ONLY_EXPENSES
import com.example.trackingexpenses.objects.SortTypesInHistoryActivity.ONLY_INCOME
import com.example.trackingexpenses.viewModels.TransactionHistoryViewModel
import com.example.trackingexpenses.views.SelectSortTypeMenu

@Composable
fun GraphicScreen(
    modifier: Modifier,
    categories: Set<String>,
    transactionHistoryViewModel: TransactionHistoryViewModel,
) {
    val transactions by transactionHistoryViewModel.allTransactions.observeAsState(emptyList())
    val popularCategories by transactionHistoryViewModel.popularCategoriesLiveData.observeAsState(emptyList())
    val transactionsLast30Days by transactionHistoryViewModel.transactionsLast30Days.observeAsState(emptyList())
    val incomeDataForPeriods by transactionHistoryViewModel.incomeDataForPeriods.observeAsState(emptyList())
    val expensesDataForPeriods by transactionHistoryViewModel.expensesDataForPeriods.observeAsState(emptyList())

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        transactionHistoryViewModel.currentSortType.value = POPULAR_EXPENSES_BAR_CHART
        transactionHistoryViewModel.fetchPopularCategoriesOfExpenses()
        transactionHistoryViewModel.fetchTransactionsLast30Days()
        transactionHistoryViewModel.fetchPeriodsData()
    }

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        SelectSortTypeMenu(transactionHistoryViewModel, categories)

        when (transactionHistoryViewModel.currentSortType.value) {
            POPULAR_EXPENSES_BAR_CHART -> {
                BarchartBars(popularCategories, context)
            }

            ChartTypes.POPULAR_EXPENSES_DONUT_CHART -> {
                transactionHistoryViewModel.fetchAndFilterTransactions(ONLY_EXPENSES)
                CategoryDonutChart(context, transactions)
            }

            ChartTypes.INCOME_SOURCES_DONUT_CHART -> {
                transactionHistoryViewModel.fetchAndFilterTransactions(ONLY_INCOME)
                CategoryDonutChart(context, transactions)
            }

            ChartTypes.EXPENSES_PERIODS_LINE_CHART -> {
                SingleLineChartWithGridLinesForPeriods(expensesDataForPeriods)
            }

            ChartTypes.INCOME_PERIODS_LINE_CHART -> {
                SingleLineChartWithGridLinesForPeriods(incomeDataForPeriods)
            }

            ChartTypes.LAST_30_DAYS_EXPENSES_LINE_CHART -> {
                SingleLineChartWithGridLines(transactionsLast30Days, context)
            }

            else -> {
                BarchartBars(popularCategories, context)
            }
        }
    }
}

@Composable
fun getRandomColor(): Color {
    val availableColors = setOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.White,
        Color.Yellow,
        Color(0xFFFFA500), // Оранжевый
        Color(0xFF800080), // Фиолетовый
        Color(0xFF808000), // Оливковый
        Color(0xFF008080), // Бирюзовый
        Color(0xFF000080), // Темно-синий
        Color(0xFFB22222), // Коралловый
        Color(0xFF4169E1), // Королевский синий
        Color(0xFF32CD32), // Лаймовый
        Color(0xFFFA8072), // Лососевый
        Color(0xFFFFD700), // Золотистый
        Color(0xFF00FA9A), // Средний морской зеленый
        Color(0xFF8B4513), // Сиена
        Color(0xFF6A5ACD), // Темно-слоновая кость
        Color(0xFF7FFF00), // Чартрез
        Color(0xFFFF6347), // Помидор
        Color(0xFF3CB371), // Средний морской зеленый
        Color(0xFFCD5C5C), // Индийский красный
        Color(0xFF4682B4), // Стальной синий
        Color(0xFF8B008B), // Темно-фиолетовый
        Color(0xFF20B2AA), // Светло-изумрудный
        Color(0xFFF0E68C), // Хаки
        Color(0xFF00CED1), // Темный циан
        Color(0xFF9370DB), // Светло-фиолетовый
        Color(0xFFFF1493), // Ярко-розовый
        Color(0xFFFA58A3), // Ярко-розовый
        Color(0xFF6B8E23), // Оливковый
        Color(0xFF8FBC8F), // Светло-зеленый
        Color(0xFF4682B4), // Стальной синий
        Color(0xFFF08080), // Светло-красный
        Color(0xFF00BFFF), // Ярко-голубой
        Color(0xFF5F9EA0), // Дельфиновый
        Color(0xFF7B68EE), // Темно-лавандовый
        Color(0xFF8A2BE2), // Темно-фиолетовый
        Color(0xFFD2691E), // Шоколадный
        Color(0xFFFF4500), // Оранжево-красный
        Color(0xFFADFF2F), // Зелёный лайм
        Color(0xFFF0E68C), // Хаки
        Color(0xFFB22222), // Коралловый
        Color(0xFF00FF7F), // Весенний зеленый
        Color(0xFF6A5ACD), // Темно-слоновая кость
        Color(0xFF3CB371), // Medium Sea Green
        Color(0xFF8B0000), // Темно-красный
        Color(0xFFFFD700), // Золотистый
        Color(0xFFFF69B4), // Ярко-розовый
        Color(0xFF7FFF00), // Чартрез
        Color(0xFF00FF00)  // Ярко-зеленый
    )

    return availableColors.random()
}