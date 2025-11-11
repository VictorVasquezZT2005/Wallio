package com.example.wallio.data.repository

import com.example.wallio.data.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class TransactionRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addTransaction(transaction: Transaction): Result<Boolean> {
        return try {
            // ✅ AGREGAR DEBUGGING PARA VERIFICAR USER ID
            println("💾 Guardando transacción en Firestore...")
            println("📝 Título: ${transaction.title}")
            println("💰 Monto: ${transaction.amount}")
            println("👤 UserId: ${transaction.userId}") // ← Esto mostrará si tiene userId
            println("🆔 TransactionId: ${transaction.id}")

            db.collection("transactions")
                .document(transaction.id)
                .set(transaction)
                .await()

            println("✅ Transacción guardada exitosamente con userId: ${transaction.userId}")
            Result.success(true)
        } catch (e: Exception) {
            println("❌ Error guardando transacción: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Result<Boolean> {
        return try {
            db.collection("transactions")
                .document(transaction.id)
                .set(transaction)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(transactionId: String): Result<Boolean> {
        return try {
            db.collection("transactions")
                .document(transactionId)
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTransactions(userId: String): Flow<List<Transaction>> = callbackFlow {
        val listener = db.collection("transactions")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val transactions = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Transaction::class.java)
                } ?: emptyList()

                trySend(transactions)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getTransactionsByDateRange(userId: String, startDate: Date, endDate: Date): List<Transaction> {
        return try {
            val snapshot = db.collection("transactions")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}