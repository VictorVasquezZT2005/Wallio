package com.example.wallio.data.model

import java.util.Date
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "",
    val date: Date = Date(),
    val description: String = "",
    val userId: String = ""
) {
    companion object {
        val categories = listOf(
            "Comida", "Transporte", "Entretenimiento", "Salud",
            "Educación", "Ropa", "Casa", "Regalos", "Otros"
        )
    }
}

enum class TransactionType {
    INCOME, EXPENSE
}