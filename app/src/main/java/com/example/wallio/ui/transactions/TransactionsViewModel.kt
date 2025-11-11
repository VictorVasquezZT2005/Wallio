package com.example.wallio.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wallio.data.model.Transaction
import com.example.wallio.data.model.TransactionType
import com.example.wallio.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class TransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val userId: String
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        loadTransactions()
    }

    // ✅ NUEVO MÉTODO AGREGADO - Para obtener el userId desde el composable
    fun getCurrentUserId(): String {
        return userId
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.getTransactions(userId).collect { transactions ->
                _state.value = _state.value.copy(transactions = transactions)
            }
        }
    }

    suspend fun addTransaction(transaction: Transaction): Boolean {
        return try {
            // ✅ Asegurar que la transacción tenga el userId correcto
            val transactionWithUserId = transaction.copy(userId = userId)
            val result = transactionRepository.addTransaction(transactionWithUserId)
            result.isSuccess
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Boolean {
        return try {
            val result = transactionRepository.updateTransaction(transaction)
            result.isSuccess
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteTransaction(transactionId: String): Boolean {
        return try {
            val result = transactionRepository.deleteTransaction(transactionId)
            result.isSuccess
        } catch (e: Exception) {
            false
        }
    }

    // NUEVO MÉTODO: Obtener transacción por ID
    fun getTransactionById(transactionId: String): Transaction? {
        return _state.value.transactions.find { it.id == transactionId }
    }

    fun getTotalIncome(): Double {
        return _state.value.transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    fun getTotalExpenses(): Double {
        return _state.value.transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    fun getBalance(): Double {
        return getTotalIncome() - getTotalExpenses()
    }
}

class TransactionsViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            return TransactionsViewModel(transactionRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}