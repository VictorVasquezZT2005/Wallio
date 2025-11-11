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
        // Mantenemos las categorías originales para compatibilidad
        val categories = listOf(
            "Comida", "Transporte", "Entretenimiento", "Salud",
            "Educación", "Ropa", "Casa", "Regalos", "Otros"
        )

        // Función para obtener el icono según la categoría (actualizada)
        fun getCategoryIcon(category: String): ImageVector {
            return when (category) {
                // Gastos
                "Comida" -> Icons.Default.Restaurant
                "Transporte" -> Icons.Default.DirectionsCar
                "Entretenimiento" -> Icons.Default.Movie
                "Salud" -> Icons.Default.LocalHospital
                "Educación" -> Icons.Default.School
                "Ropa" -> Icons.Default.ShoppingBag
                "Casa" -> Icons.Default.Home
                "Regalos" -> Icons.Default.CardGiftcard
                "Viajes" -> Icons.Default.Flight
                "Servicios" -> Icons.Default.Build
                "Tecnología" -> Icons.Default.Computer
                "Deportes" -> Icons.Default.Sports
                "Cuidado Personal" -> Icons.Default.Spa
                "Mascotas" -> Icons.Default.Pets
                "Impuestos" -> Icons.Default.AttachMoney
                "Otros Gastos" -> Icons.Default.MoreHoriz

                // Ingresos
                "Salario" -> Icons.Default.Work
                "Freelance" -> Icons.Default.Computer
                "Inversiones" -> Icons.Default.TrendingUp
                "Negocio" -> Icons.Default.Business
                "Regalos" -> Icons.Default.CardGiftcard
                "Premios" -> Icons.Default.EmojiEvents
                "Ventas" -> Icons.Default.ShoppingCart
                "Alquiler" -> Icons.Default.Home
                "Intereses" -> Icons.Default.AccountBalance
                "Dividendos" -> Icons.Default.PieChart
                "Bonos" -> Icons.Default.Security
                "Comisiones" -> Icons.Default.Money
                "Honorarios" -> Icons.Default.Description
                "Reembolsos" -> Icons.Default.Receipt
                "Herencia" -> Icons.Default.AccountBalanceWallet
                "Otros Ingresos" -> Icons.Default.MoreHoriz

                else -> Icons.Default.Category
            }
        }
    }
}