package com.example.wallio.ui.reports.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

@Composable
fun IncomeVsExpensePieChart(
    monthlyData: Map<String, Pair<Double, Double>>,
    modifier: Modifier = Modifier,
    title: String = "Ingresos vs Gastos Totales"
) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                // Configuración básica
                setUsePercentValues(true)
                description.isEnabled = false
                setDrawEntryLabels(true)
                setEntryLabelTextSize(12f)

                // Configurar el agujero central
                setDrawHoleEnabled(true)
                holeRadius = 40f
                transparentCircleRadius = 45f
                setHoleColor(Color.TRANSPARENT)

                // Configurar leyenda
                legend.isEnabled = true
                legend.textSize = 12f
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.xEntrySpace = 15f
                legend.yEntrySpace = 5f
                legend.formSize = 12f

                // Animación
                animateY(1000)
                setExtraOffsets(20f, 0f, 20f, 20f)
            }
        },
        update = { chart ->
            // Obtener colores del tema
            val textColor = if (isSystemInDarkTheme()) {
                Color.WHITE
            } else {
                Color.BLACK
            }

            // Configurar colores de texto
            chart.setEntryLabelColor(textColor)
            chart.legend.textColor = textColor

            // Calcular totales de ingresos y gastos
            val totalIncome = monthlyData.values.sumOf { it.first }
            val totalExpenses = monthlyData.values.sumOf { it.second }
            val total = totalIncome + totalExpenses

            if (total > 0) {
                val entries = ArrayList<PieEntry>()
                val colors = ArrayList<Int>()

                // Agregar ingresos
                if (totalIncome > 0) {
                    entries.add(PieEntry(totalIncome.toFloat(), "Ingresos"))
                    colors.add(Color.parseColor("#4CAF50")) // Verde para ingresos
                }

                // Agregar gastos
                if (totalExpenses > 0) {
                    entries.add(PieEntry(totalExpenses.toFloat(), "Gastos"))
                    colors.add(Color.parseColor("#F44336")) // Rojo para gastos
                }

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

// Función para detectar modo oscuro
private fun isSystemInDarkTheme(): Boolean {
    val configuration = android.content.res.Resources.getSystem().configuration
    val currentNightMode = configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
}