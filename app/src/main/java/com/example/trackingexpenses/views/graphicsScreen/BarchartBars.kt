package com.example.trackingexpenses.views.graphicsScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import com.example.trackingexpenses.R
import com.example.trackingexpenses.models.BarData

@Composable
fun BarchartBars(popularCategories: List<Pair<Double, String>>, context: Context) {
    if (popularCategories.isEmpty()) {
        return
    }

    val myBarData = popularCategories.mapIndexed { index, (amount, category) ->
        val color = getRandomColor()
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