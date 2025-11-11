package com.example.wallio.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallio.data.model.Transaction
import com.example.wallio.data.model.TransactionType
import com.example.wallio.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

data class ChartsState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChartsViewModel(
    private val transactionRepository: TransactionRepository,
    private val userId: String
) : ViewModel() {

    private val _state = MutableStateFlow(ChartsState())
    val state: StateFlow<ChartsState> = _state.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                transactionRepository.getTransactions(userId).collect { transactions ->
                    _state.value = _state.value.copy(
                        transactions = transactions,
                        isLoading = false
                    )
                    println("📊 ChartsViewModel: Cargadas ${transactions.size} transacciones")
                    transactions.forEach { transaction ->
                        println("📝 Transacción: ${transaction.title} - ${transaction.amount} - ${transaction.type}")
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
                println("❌ Error en ChartsViewModel: ${e.message}")
            }
        }
    }

    // Datos para gráfico de pastel por categorías
    fun getCategoryData(): Map<String, Double> {
        val categoryMap = HashMap<String, Double>()

        val expenses = _state.value.transactions
            .filter { it.type == TransactionType.EXPENSE }

        println("💰 Gastos encontrados: ${expenses.size}")

        expenses.forEach { transaction ->
            val currentAmount = categoryMap.getOrDefault(transaction.category, 0.0)
            categoryMap[transaction.category] = currentAmount + transaction.amount
            println("📊 Categoría: ${transaction.category} - Monto: ${transaction.amount}")
        }

        return categoryMap.toList()
            .sortedByDescending { (_, amount) -> amount }
            .toMap()
    }

    // Datos para gráfico de barras mensual
    fun getMonthlyData(): Map<String, Pair<Double, Double>> {
        val monthlyMap = HashMap<String, Pair<Double, Double>>()
        val calendar = Calendar.getInstance()

        _state.value.transactions.forEach { transaction ->
            calendar.time = transaction.date
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val monthYear = "$month/$year"

            val current = monthlyMap.getOrDefault(monthYear, Pair(0.0, 0.0))

            val newIncome = if (transaction.type == TransactionType.INCOME)
                current.first + transaction.amount
            else
                current.first

            val newExpense = if (transaction.type == TransactionType.EXPENSE)
                current.second + transaction.amount
            else
                current.second

            monthlyMap[monthYear] = Pair(newIncome, newExpense)
        }

        return monthlyMap.toList()
            .sortedBy { (key, _) ->
                val parts = key.split("/")
                parts[1].toInt() * 100 + parts[0].toInt()
            }
            .toMap()
    }

    // Estadísticas resumen
    fun getSummaryStats(): SummaryStats {
        val transactions = _state.value.transactions
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val balance = totalIncome - totalExpenses

        val incomeCount = transactions.count { it.type == TransactionType.INCOME }
        val expenseCount = transactions.count { it.type == TransactionType.EXPENSE }

        val averageIncome = if (incomeCount > 0) totalIncome / incomeCount else 0.0
        val averageExpense = if (expenseCount > 0) totalExpenses / expenseCount else 0.0

        println("📈 Resumen - Ingresos: $totalIncome, Gastos: $totalExpenses, Balance: $balance")
        println("📈 Total transacciones: ${transactions.size}")

        return SummaryStats(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            balance = balance,
            averageIncome = averageIncome,
            averageExpense = averageExpense,
            transactionCount = transactions.size,
            incomeCount = incomeCount,
            expenseCount = expenseCount
        )
    }
}

data class SummaryStats(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double,
    val averageIncome: Double,
    val averageExpense: Double,
    val transactionCount: Int,
    val incomeCount: Int = 0,
    val expenseCount: Int = 0
)