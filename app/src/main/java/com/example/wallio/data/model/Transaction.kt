package com.example.wallio.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.*
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

        // Función para obtener el icono según la categoría
        fun getCategoryIcon(category: String): ImageVector {
            return when (category) {
                "Comida" -> Icons.Default.Restaurant
                "Transporte" -> Icons.Default.DirectionsCar
                "Entretenimiento" -> Icons.Default.Movie
                "Salud" -> Icons.Default.LocalHospital
                "Educación" -> Icons.Default.School
                "Ropa" -> Icons.Default.ShoppingBag
                "Casa" -> Icons.Default.Home
                "Regalos" -> Icons.Default.CardGiftcard
                "Otros" -> Icons.Default.MoreHoriz
                else -> Icons.Default.Category
            }
        }

        // Función para obtener el color según la categoría (opcional)
        fun getCategoryColor(category: String): String {
            return when (category) {
                "Comida" -> "#FF9800" // Naranja
                "Transporte" -> "#2196F3" // Azul
                "Entretenimiento" -> "#9C27B0" // Púrpura
                "Salud" -> "#F44336" // Rojo
                "Educación" -> "#4CAF50" // Verde
                "Ropa" -> "#E91E63" // Rosa
                "Casa" -> "#795548" // Marrón
                "Regalos" -> "#FFC107" // Amarillo
                "Otros" -> "#607D8B" // Gris azulado
                else -> "#9E9E9B" // Gris
            }
        }
    }
}

enum class TransactionType {
    INCOME, EXPENSE
}