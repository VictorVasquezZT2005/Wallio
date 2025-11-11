package com.example.wallio.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wallio.data.repository.TransactionRepository

class ChartsViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChartsViewModel::class.java)) {
            return ChartsViewModel(transactionRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}