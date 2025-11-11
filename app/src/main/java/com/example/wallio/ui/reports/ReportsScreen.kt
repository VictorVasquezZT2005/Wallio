package com.example.wallio.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallio.ui.reports.components.IncomeVsExpensePieChart
import com.example.wallio.ui.reports.components.PieChartComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ChartsViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    val categoryData by remember(state.transactions) {
        derivedStateOf { viewModel.getCategoryData() }
    }
    val monthlyData by remember(state.transactions) {
        derivedStateOf { viewModel.getMonthlyData() }
    }
    val summaryStats by remember(state.transactions) {
        derivedStateOf { viewModel.getSummaryStats() }
    }

    // Debug info
    LaunchedEffect(state.transactions) {
        println("🔄 ReportsScreen - Transacciones: ${state.transactions.size}")
        println("📊 ReportsScreen - Categorías: ${categoryData.size}")
        categoryData.forEach { (category, amount) ->
            println("📈 $category: $${amount}")
        }
        println("📅 ReportsScreen - Meses: ${monthlyData.size}")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes y Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: ${state.error}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Resumen de estadísticas
                SummaryCards(stats = summaryStats)

                // Gráfico de pastel - Distribución de gastos por categoría
                if (categoryData.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Distribución de Gastos por Categoría",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            PieChartComponent(
                                data = categoryData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            )
                            // Mostrar datos detallados
                            CategoryDetails(categoryData = categoryData)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay datos de gastos para mostrar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Gráfico de pastel - Ingresos vs Gastos totales
                if (monthlyData.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Distribución Ingresos vs Gastos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            IncomeVsExpensePieChart(
                                monthlyData = monthlyData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            )
                            // Mostrar datos detallados
                            IncomeExpenseDetails(monthlyData = monthlyData)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay datos mensuales para mostrar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Mensaje cuando no hay datos
                if (state.transactions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay transacciones para mostrar reportes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Agrega algunas transacciones primero",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDetails(categoryData: Map<String, Double>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Detalles por Categoría:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        categoryData.forEach { (category, amount) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$${String.format("%.2f", amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseDetails(monthlyData: Map<String, Pair<Double, Double>>) {
    val totalIncome = monthlyData.values.sumOf { it.first }
    val totalExpenses = monthlyData.values.sumOf { it.second }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Resumen Total:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Ingresos:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$${String.format("%.2f", totalIncome)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Gastos:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$${String.format("%.2f", totalExpenses)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Balance:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$${String.format("%.2f", totalIncome - totalExpenses)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (totalIncome - totalExpenses >= 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

// Los componentes SummaryCards y SummaryCard se mantienen igual que antes
@Composable
fun SummaryCards(stats: SummaryStats) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fila 1: Balance e Ingresos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card de Balance
            SummaryCard(
                title = "Balance Total",
                value = stats.balance,
                isPositive = stats.balance >= 0,
                modifier = Modifier.weight(1f)
            )

            // Card de Ingresos
            SummaryCard(
                title = "Total Ingresos",
                value = stats.totalIncome,
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 2: Gastos y Transacciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card de Gastos
            SummaryCard(
                title = "Total Gastos",
                value = stats.totalExpenses,
                isPositive = false,
                modifier = Modifier.weight(1f)
            )

            // Card de Cantidad de Transacciones
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Transacciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stats.transactionCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${stats.incomeCount}I / ${stats.expenseCount}G",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: Double,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${String.format("%.2f", value)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}