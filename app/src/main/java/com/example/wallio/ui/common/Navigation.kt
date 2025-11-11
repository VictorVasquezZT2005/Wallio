package com.example.wallio.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wallio.data.repository.AuthRepository
import com.example.wallio.data.repository.TransactionRepository
import com.example.wallio.ui.auth.AuthViewModel
import com.example.wallio.ui.auth.AuthViewModelFactory
import com.example.wallio.ui.auth.LoginScreen
import com.example.wallio.ui.auth.RegisterScreen
import com.example.wallio.ui.dashboard.DashboardScreen
import com.example.wallio.ui.reports.ChartsViewModel
import com.example.wallio.ui.reports.ChartsViewModelFactory
import com.example.wallio.ui.reports.ReportsScreen
import com.example.wallio.ui.transactions.AddEditTransactionScreen
import com.example.wallio.ui.transactions.TransactionListScreen
import com.example.wallio.ui.transactions.TransactionsViewModel
import com.example.wallio.ui.transactions.TransactionsViewModelFactory

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val authRepository = remember { AuthRepository() }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("dashboard") },
                onNavigateToRegister = { navController.navigate("register") },
                onSkipLogin = {
                    navController.navigate("dashboard")
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("dashboard") },
                onNavigateToLogin = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        composable("dashboard") {
            val userId = authRepository.getCurrentUserId() ?: "local_user"
            val transactionRepository = remember { TransactionRepository() }
            val transactionsViewModel: TransactionsViewModel = viewModel(
                factory = TransactionsViewModelFactory(transactionRepository, userId)
            )

            DashboardScreen(
                viewModel = transactionsViewModel,
                authViewModel = authViewModel,
                onAddTransaction = { navController.navigate("add-transaction") },
                onViewAllTransactions = { navController.navigate("transactions") },
                onViewReports = { navController.navigate("reports") }, // NUEVO
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }

        // Ruta para agregar transacción (sin parámetros)
        composable("add-transaction") {
            val userId = authRepository.getCurrentUserId() ?: "local_user"
            val transactionRepository = remember { TransactionRepository() }
            val transactionsViewModel: TransactionsViewModel = viewModel(
                factory = TransactionsViewModelFactory(transactionRepository, userId)
            )

            AddEditTransactionScreen(
                viewModel = transactionsViewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Nueva ruta para editar transacción con parámetro
        composable("edit-transaction/{transactionId}") { backStackEntry ->
            val userId = authRepository.getCurrentUserId() ?: "local_user"
            val transactionRepository = remember { TransactionRepository() }
            val transactionsViewModel: TransactionsViewModel = viewModel(
                factory = TransactionsViewModelFactory(transactionRepository, userId)
            )
            val transactionId = backStackEntry.arguments?.getString("transactionId")

            AddEditTransactionScreen(
                viewModel = transactionsViewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() },
                transactionId = transactionId
            )
        }

        composable("transactions") {
            val userId = authRepository.getCurrentUserId() ?: "local_user"
            val transactionRepository = remember { TransactionRepository() }
            val transactionsViewModel: TransactionsViewModel = viewModel(
                factory = TransactionsViewModelFactory(transactionRepository, userId)
            )

            TransactionListScreen(
                viewModel = transactionsViewModel,
                onBack = { navController.popBackStack() },
                onAddTransaction = { navController.navigate("add-transaction") },
                onEditTransaction = { transactionId ->
                    navController.navigate("edit-transaction/$transactionId")
                }
            )
        }

        // NUEVA RUTA: Reportes y Gráficos
        composable("reports") {
            val userId = authRepository.getCurrentUserId() ?: "local_user"
            val transactionRepository = remember { TransactionRepository() }
            val chartsViewModel: ChartsViewModel = viewModel(
                factory = ChartsViewModelFactory(transactionRepository, userId)
            )

            ReportsScreen(
                viewModel = chartsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}