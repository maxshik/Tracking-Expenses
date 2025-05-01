package com.example.trackingexpenses.views.graphicsScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import com.example.trackingexpenses.R
import com.example.trackingexpenses.models.BarData
import kotlin.math.absoluteValue

@Composable
fun BarchartBars(popularCategories: List<Pair<Double, String>>, context: Context) {
    if (popularCategories.isEmpty()) {
        return
    }

    val fixedColors = listOf(
        Color(0xFF2196F3), Color(0xFFF44336), Color(0xFF4CAF50), Color(0xFFFFC107),
        Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFFFF5722), Color(0xFF795548),
        Color(0xFF607D8B), Color(0xFFE91E63), Color(0xFF3F51B5), Color(0xFF009688),
        Color(0xFFCDDC39), Color(0xFF673AB7), Color(0xFFFF9800), Color(0xFF8BC34A),
        Color(0xFF03A9F4), Color(0xFFFFEB3B), Color(0xFF9E9E9E), Color(0xFF827717),
        Color(0xFF0288D1), Color(0xFFD81B60), Color(0xFF388E3C), Color(0xFFFBC02D),
        Color(0xFF5D4037), Color(0xFF7B1FA2), Color(0xFF1976D2), Color(0xFF689F38),
        Color(0xFFB0BEC5), Color(0xFFFF6F00), Color(0xFF512DA8), Color(0xFF006064)
    )

    val myBarData = popularCategories.mapIndexed { index, (amount, category) ->
        val colorIndex = category.hashCode().absoluteValue % fixedColors.size
        val color = fixedColors[colorIndex]
        BarData(
            label = category,
            point = Point(index.toFloat(), amount.toFloat()),
            color = color,
            value = amount.toFloat()
        )
    }

    val barData = myBarData.map { it.toLibraryBarData() }

    val maxRange = popularCategories.maxOfOrNull { it.first }?.toInt() ?: 100
    val yStepSize = 10

    val xAxisData = AxisData.Builder()
        .backgroundColor(MaterialTheme.colorScheme.background)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .shouldDrawAxisLineTillEnd(true)
        .steps(barData.size - 1)
        .bottomPadding(200.dp)
        .axisLabelAngle(55f)
        .startDrawPadding(50.dp)
        .labelData { index -> barData[index].label }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .backgroundColor(MaterialTheme.colorScheme.background)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .labelAndAxisLinePadding(0.dp)
        .axisOffset(20.dp)
        .labelData { index -> (index * (maxRange / yStepSize)).toString() }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        paddingEnd = 0.dp,
        horizontalExtraSpace = 30.dp,
        backgroundColor = MaterialTheme.colorScheme.background,
        barStyle = BarStyle(
            paddingBetweenBars = 40.dp,
            barWidth = 35.dp,
            selectionHighlightData = SelectionHighlightData(
                popUpLabel = { x, y ->
                    "${context.getString(R.string.category)}: ${y}. ${
                        context.getString(R.string.category)
                    }: ${barData[x.toInt()].label}"
                }
            )
        ),
        showYAxis = true,
        showXAxis = true,
    )

    BarChart(
        modifier = Modifier
            .height(500.dp)
            .background(MaterialTheme.colorScheme.background),
        barChartData = barChartData
    )
}