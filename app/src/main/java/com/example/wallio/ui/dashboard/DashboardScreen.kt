package com.example.wallio.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallio.ui.auth.AuthViewModel
import com.example.wallio.ui.transactions.TransactionsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TransactionsViewModel,
    authViewModel: AuthViewModel,
    onAddTransaction: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onViewReports: () -> Unit,
    onLogout: () -> Unit
) {
    val transactionsState = viewModel.state.collectAsState().value
    val authState = authViewModel.authState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    // Refrescar datos COMPLETOS del usuario cuando se muestra la pantalla
    LaunchedEffect(Unit) {
        authViewModel.refreshAuthState()
        // También forzar la carga de datos desde Firestore
        coroutineScope.launch {
            authViewModel.refreshUserData()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wallio") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction) {
                Icon(Icons.Default.Add, contentDescription = "Agregar transacción")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Saludo de bienvenida - ahora debería cargar el nombre REAL
                authState.user?.let { user ->
                    if (user.name.isNotEmpty() && user.name != "Cargando..." && user.name != "Usuario") {
                        Text(
                            text = "Hola, ${user.name}",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        Text(
                            text = "Hola, Usuario",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                } ?: Text(
                    text = "Hola, Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Resumen financiero
                FinancialSummary(viewModel = viewModel)

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acciones
                ActionButtons(
                    onViewAllTransactions = onViewAllTransactions,
                    onViewReports = onViewReports
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Últimas transacciones
                Text(
                    text = "Últimas transacciones",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            if (transactionsState.transactions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay transacciones registradas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Presiona el botón + para agregar tu primera transacción",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                items(transactionsState.transactions.take(5).size) { index ->
                    val transaction = transactionsState.transactions.take(5)[index]
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { /* Implementar si es necesario */ }
                    )
                }

                // Mostrar mensaje si hay más transacciones
                if (transactionsState.transactions.size > 5) {
                    item {
                        Text(
                            text = "... y ${transactionsState.transactions.size - 5} más",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onViewAllTransactions: () -> Unit,
    onViewReports: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón para ver todas las transacciones
        Button(
            onClick = onViewAllTransactions,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver todas las transacciones")
        }

        // Botón para ver reportes
        Button(
            onClick = onViewReports,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Icon(Icons.Default.Analytics, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Reportes y Gráficos")
        }
    }
}

@Composable
fun FinancialSummary(viewModel: TransactionsViewModel) {
    val balance = viewModel.getBalance()
    val income = viewModel.getTotalIncome()
    val expenses = viewModel.getTotalExpenses()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Balance Total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "$${String.format("%.2f", balance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ingresos",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "+$${String.format("%.2f", income)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Gastos",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "-$${String.format("%.2f", expenses)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: com.example.wallio.data.model.Transaction,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${transaction.date.date}/${transaction.date.month + 1}/${transaction.date.year + 1900}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = if (transaction.type == com.example.wallio.data.model.TransactionType.INCOME)
                    "+$${String.format("%.2f", transaction.amount)}"
                else
                    "-$${String.format("%.2f", transaction.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == com.example.wallio.data.model.TransactionType.INCOME)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}