package com.example.trackingexpenses.models

import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.Point

data class BarData(
    val label: String,
    val point: Point,
    val color: Color,
    val value: Float,
) {
    fun toLibraryBarData(): co.yml.charts.ui.barchart.models.BarData {
        return co.yml.charts.ui.barchart.models.BarData(
            label = this.label,
            point = this.point,
            color = this.color,
        )
    }
}