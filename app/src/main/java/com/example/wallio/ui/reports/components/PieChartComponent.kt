package com.example.wallio.ui.reports.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun PieChartComponent(
    data: Map<String, Double>,
    modifier: Modifier = Modifier,
    title: String = "Distribución"
) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                // Configuración básica
                setUsePercentValues(true)
                description.isEnabled = false
                setDrawEntryLabels(true)
                setEntryLabelColor(Color.BLACK)
                setEntryLabelTextSize(12f)

                // Configurar el agujero central
                setDrawHoleEnabled(true)
                holeRadius = 40f
                transparentCircleRadius = 45f
                setHoleColor(Color.TRANSPARENT)

                // Configurar leyenda
                legend.isEnabled = true
                legend.textSize = 12f
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)

                // Animación
                animateY(1000)
                setExtraOffsets(20f, 0f, 20f, 20f)
            }
        },
        update = { chart ->
            if (data.isNotEmpty()) {
                val entries = ArrayList<PieEntry>()
                val colors = ArrayList<Int>()

                // Crear entradas para el gráfico
                data.forEach { (category, amount) ->
                    if (amount > 0) {
                        entries.add(PieEntry(amount.toFloat(), category))
                    }
                }

                // Colores para el gráfico
                colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
                colors.addAll(ColorTemplate.JOYFUL_COLORS.toList())
                colors.addAll(ColorTemplate.COLORFUL_COLORS.toList())
                colors.addAll(ColorTemplate.VORDIPLOM_COLORS.toList())
                colors.addAll(ColorTemplate.LIBERTY_COLORS.toList())
                colors.addAll(ColorTemplate.PASTEL_COLORS.toList())

                val dataSet = PieDataSet(entries, "").apply {
                    setColors(colors)
                    valueTextSize = 13f
                    valueTextColor = Color.WHITE
                    yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
                    valueFormatter = PercentFormatter(chart)
                    sliceSpace = 2f
                }

                val pieData = PieData(dataSet).apply {
                    setValueTextSize(13f)
                    setValueTextColor(Color.WHITE)
                }

                chart.data = pieData
                chart.invalidate() // Refrescar el gráfico
            } else {
                // Limpiar el gráfico si no hay datos
                chart.clear()
                chart.data = null
            }
        },
        modifier = modifier
    )
}