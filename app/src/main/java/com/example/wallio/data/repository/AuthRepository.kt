package com.example.wallio.data.repository

import android.util.Log
import com.example.wallio.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estado actual del usuario
    private val _currentUser = MutableStateFlow<User?>(null)

    init {
        Log.d("AuthRepository", "🔥 Firebase inicializado - Listo para usar")
        // Configurar listener de cambios de autenticación
        setupAuthStateListener()
    }

    private fun setupAuthStateListener() {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Usuario está autenticado - cargar datos COMPLETOS de Firestore
                Log.d("AuthRepository", "👤 Usuario autenticado detectado: ${firebaseUser.uid}")
                // Usar coroutine para cargar datos de Firestore
                loadUserDataFromFirestore(firebaseUser.uid)
            } else {
                // Usuario no autenticado
                Log.d("AuthRepository", "🚪 Usuario no autenticado")
                _currentUser.value = null
            }
        }
    }

    private fun loadUserDataFromFirestore(userId: String) {
        // Cargar datos COMPLETOS del usuario desde Firestore
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("AuthRepository", "📖 Cargando datos completos del usuario $userId desde Firestore...")
                val document = db.collection("users").document(userId).get().await()

                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        _currentUser.value = user
                        Log.d("AuthRepository", "✅ Datos completos cargados: ${user.name} - ${user.email}")
                    } else {
                        // Si no se puede convertir, crear usuario básico
                        createDefaultUser(userId)
                    }
                } else {
                    // Si no existe en Firestore, crear usuario
                    Log.w("AuthRepository", "⚠️ Usuario no encontrado en Firestore, creando...")
                    createDefaultUser(userId)
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "❌ Error cargando datos de Firestore: ${e.message}")
                // En caso de error, crear usuario básico
                createDefaultUser(userId)
            }
        }
    }

    private fun createDefaultUser(userId: String) {
        val defaultUser = User(
            id = userId,
            email = auth.currentUser?.email ?: "",
            name = auth.currentUser?.displayName ?:
            auth.currentUser?.email?.substringBefore("@") ?: "Usuario"
        )
        _currentUser.value = defaultUser
        Log.d("AuthRepository", "👤 Usuario por defecto creado: ${defaultUser.name}")

        // Guardar en Firestore para futuras sesiones
        GlobalScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(userId).set(defaultUser).await()
                Log.d("AuthRepository", "💾 Usuario guardado en Firestore")
            } catch (e: Exception) {
                Log.e("AuthRepository", "❌ Error guardando usuario en Firestore: ${e.message}")
            }
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        Log.d("AuthRepository", "🔐 Intentando login: $email")

        return try {
            // 1. Primero autenticar con Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("No se pudo autenticar")

            Log.d("AuthRepository", "✅ Autenticación exitosa - UserId: $userId")

            // 2. Obtener datos COMPLETOS del usuario de Firestore
            val user = getUserData(userId)
            _currentUser.value = user // Actualizar estado actual
            Log.d("AuthRepository", "✅ Login completo exitoso - Nombre: ${user.name}")

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

            // 2. Crear objeto usuario con el nombre REAL
            val user = User(
                id = userId,
                email = email,
                name = name // ← Este es el nombre REAL del usuario
            )

            // 3. Guardar usuario en Firestore
            Log.d("AuthRepository", "2. Guardando usuario en Firestore...")
            db.collection("users").document(userId).set(user).await()
            Log.d("AuthRepository", "✅ Usuario guardado en Firestore - Nombre: $name")

            // 4. Actualizar estado actual
            _currentUser.value = user

            Log.d("AuthRepository", "🎉 REGISTRO COMPLETADO EXITOSAMENTE - Nombre: $name")
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
        _currentUser.value = null
        Log.d("AuthRepository", "🚪 Sesión cerrada")
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid ?: _currentUser.value?.id
    }

    // Obtener usuario actual (para uso inmediato)
    fun getCurrentUser(): User? {
        return _currentUser.value ?: auth.currentUser?.let { firebaseUser ->
            // Si no hay usuario en el estado, crear uno temporal
            User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = "Cargando..." // ← Mensaje temporal mientras se cargan los datos
            )
        }
    }

    // Método para forzar la carga de datos desde Firestore
    suspend fun refreshUserData(): User? {
        val userId = getCurrentUserId() ?: return null
        return try {
            Log.d("AuthRepository", "🔄 Forzando carga de datos del usuario...")
            val user = getUserData(userId)
            _currentUser.value = user
            Log.d("AuthRepository", "✅ Datos refrescados: ${user.name}")
            user
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error refrescando datos: ${e.message}")
            null
        }
    }

    private suspend fun getUserData(userId: String): User {
        return try {
            Log.d("AuthRepository", "📖 Obteniendo datos del usuario $userId de Firestore...")
            val document = db.collection("users").document(userId).get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Log.d("AuthRepository", "✅ Usuario encontrado en Firestore: ${user.name}")
                    user
                } else {
                    Log.w("AuthRepository", "⚠️ Usuario nulo en Firestore, creando default...")
                    createUserInFirestore(userId)
                }
            } else {
                Log.w("AuthRepository", "⚠️ Usuario no encontrado en Firestore, creando...")
                createUserInFirestore(userId)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error obteniendo datos usuario: ${e.message}")
            createUserInFirestore(userId)
        }
    }

    private suspend fun createUserInFirestore(userId: String): User {
        val user = User(
            id = userId,
            email = auth.currentUser?.email ?: "",
            name = auth.currentUser?.displayName ?:
            auth.currentUser?.email?.substringBefore("@") ?: "Usuario"
        )
        db.collection("users").document(userId).set(user).await()
        Log.d("AuthRepository", "👤 Usuario creado en Firestore: ${user.name}")
        return user
    }
}