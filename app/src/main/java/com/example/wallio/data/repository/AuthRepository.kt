package com.example.wallio.data.repository

import android.util.Log
import com.example.wallio.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        Log.d("AuthRepository", "🔥 Firebase inicializado - Listo para usar")
    }

    suspend fun login(email: String, password: String): Result<User> {
        Log.d("AuthRepository", "🔐 Intentando login: $email")

        return try {
            // 1. Primero autenticar con Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("No se pudo autenticar")

            Log.d("AuthRepository", "✅ Autenticación exitosa - UserId: $userId")

            // 2. Obtener datos del usuario de Firestore
            val user = getUserData(userId)
            Log.d("AuthRepository", "✅ Login completo exitoso")

            Result.success(user)

        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error en login: ${e.message}")
            Result.failure(Exception("Error de login: ${e.message}"))
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        Log.d("AuthRepository", "📝 Intentando registro: $email - $name")

        return try {
            // 1. Crear usuario en Firebase Authentication
            Log.d("AuthRepository", "1. Creando usuario en Firebase Auth...")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("No se pudo crear el usuario")

            Log.d("AuthRepository", "✅ Usuario creado en Auth - ID: $userId")

            // 2. Crear objeto usuario
            val user = User(
                id = userId,
                email = email,
                name = name
            )

            // 3. Guardar usuario en Firestore
            Log.d("AuthRepository", "2. Guardando usuario en Firestore...")
            db.collection("users").document(userId).set(user).await()
            Log.d("AuthRepository", "✅ Usuario guardado en Firestore")

            // 4. Crear también una transacción de prueba
            Log.d("AuthRepository", "3. Creando transacción de prueba...")
            try {
                val testTransaction = com.example.wallio.data.model.Transaction(
                    title = "Transacción de prueba",
                    amount = 100.0,
                    type = com.example.wallio.data.model.TransactionType.INCOME,
                    category = "Prueba",
                    description = "Transacción creada automáticamente",
                    userId = userId
                )
                db.collection("transactions").document(testTransaction.id).set(testTransaction).await()
                Log.d("AuthRepository", "✅ Transacción de prueba creada")
            } catch (e: Exception) {
                Log.w("AuthRepository", "⚠️ No se pudo crear transacción de prueba: ${e.message}")
            }

            Log.d("AuthRepository", "🎉 REGISTRO COMPLETADO EXITOSAMENTE")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ ERROR EN REGISTRO: ${e.message}")

            // Manejar errores específicos de Firebase
            when {
                e.message?.contains("EMAIL_EXISTS") == true ->
                    Result.failure(Exception("El email ya está registrado"))

                e.message?.contains("INVALID_EMAIL") == true ->
                    Result.failure(Exception("Email no válido"))

                e.message?.contains("WEAK_PASSWORD") == true ->
                    Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))

                e.message?.contains("network") == true || e.message?.contains("connection") == true ->
                    Result.failure(Exception("Error de conexión. Verifica tu internet"))

                else -> Result.failure(Exception("Error de registro: ${e.message}"))
            }
        }
    }

    fun logout() {
        auth.signOut()
        Log.d("AuthRepository", "🚪 Sesión cerrada")
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    private suspend fun getUserData(userId: String): User {
        return try {
            Log.d("AuthRepository", "📖 Obteniendo datos del usuario $userId de Firestore...")
            val document = db.collection("users").document(userId).get().await()

            if (document.exists()) {
                Log.d("AuthRepository", "✅ Usuario encontrado en Firestore")
                document.toObject(User::class.java) ?: User(
                    id = userId,
                    email = auth.currentUser?.email ?: "",
                    name = "Usuario"
                )
            } else {
                Log.w("AuthRepository", "⚠️ Usuario no encontrado en Firestore, creando...")
                val user = User(
                    id = userId,
                    email = auth.currentUser?.email ?: "",
                    name = "Usuario"
                )
                db.collection("users").document(userId).set(user).await()
                user
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error obteniendo datos usuario: ${e.message}")
            User(
                id = userId,
                email = auth.currentUser?.email ?: "",
                name = "Usuario"
            )
        }
    }
}