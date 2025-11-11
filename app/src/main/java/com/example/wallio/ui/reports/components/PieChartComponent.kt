package com.example.wallio.ui.reports.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.wallio.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

@Composable
fun PieChartComponent(
    data: Map<String, Double>,
    modifier: Modifier = Modifier,
    title: String = "Distribución"
) {
    val context = LocalContext.current

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

            if (data.isNotEmpty()) {
                val entries = ArrayList<PieEntry>()
                val colors = ArrayList<Int>()

                // Crear entradas para el gráfico
                data.forEach { (category, amount) ->
                    if (amount > 0) {
                        entries.add(PieEntry(amount.toFloat(), category))
                    }
                }

                // Colores para el gráfico (usando colores Material Design)
                val materialColors = listOf(
                    Color.parseColor("#FF4CAF50"), // Verde
                    Color.parseColor("#FF2196F3"), // Azul
                    Color.parseColor("#FFFFC107"), // Amarillo
                    Color.parseColor("#FFFF9800"), // Naranja
                    Color.parseColor("#FF9C27B0"), // Púrpura
                    Color.parseColor("#FFF44336"), // Rojo
                    Color.parseColor("#FF607D8B"), // Gris azulado
                    Color.parseColor("#FF795548"), // Marrón
                    Color.parseColor("#FF009688"), // Verde azulado
                    Color.parseColor("#FF3F51B5")  // Índigo
                )

                colors.addAll(materialColors)

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