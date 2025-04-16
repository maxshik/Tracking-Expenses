package com.example.trackingexpenses.views.graphicsScreen

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.trackingexpenses.models.Transaction

@Composable
fun CategoryDonutChart(context: Context, transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        return
    }

    val categoryMap = transactions.groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.coast.toDoubleOrNull() ?: 0.0 } }
        .filter { it.value > 0 }

    val totalAmount = categoryMap.values.sum()

    if (totalAmount == 0.0) {
        return
    }

    val proportions = categoryMap.mapValues { (_, amount) -> (amount / totalAmount) * 100 }

    val pieChartData = PieChartData(
        slices = proportions.map { (category, percentage) ->
            PieChartData.Slice(
                label = category,
                value = percentage.toFloat(),
                color = getRandomColor()
            )
        },
        plotType = PlotType.Donut
    )

    val pieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 120f,
        labelColor = MaterialTheme.colorScheme.tertiary,
        activeSliceAlpha = .9f,
        isEllipsizeEnabled = true,
        labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
        isAnimationEnable = true,
        chartPadding = 25,
        labelFontSize = 42.sp,
        backgroundColor = MaterialTheme.colorScheme.background,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(800.dp)
    ) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData, 3))
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            pieChartData,
            pieChartConfig
        ) { slice ->
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}
