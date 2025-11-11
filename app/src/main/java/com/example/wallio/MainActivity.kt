package com.example.wallio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.wallio.ui.common.Navigation
import com.example.wallio.ui.theme.WallioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase manualmente
        initializeFirebase()

        enableEdgeToEdge()
        setContent {
            WallioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }

    private fun initializeFirebase() {
        try {
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)

            // Configurar Firestore
            val firestore = FirebaseFirestore.getInstance()

            // Configurar Auth
            val auth = FirebaseAuth.getInstance()

            println("✅ Firebase inicializado correctamente")
            println("📱 Project ID: wallio-87186")
            println("🔑 API Key: AIzaSyDgUKWBRVtNIaJB9W7UVJItWJNY0HUK0ao")

        } catch (e: Exception) {
            println("❌ Error inicializando Firebase: ${e.message}")
            e.printStackTrace()
        }
    }
}