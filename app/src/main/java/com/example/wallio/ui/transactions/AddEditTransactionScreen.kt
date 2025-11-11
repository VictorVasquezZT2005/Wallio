package com.example.wallio.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wallio.data.model.Transaction
import com.example.wallio.data.model.TransactionType
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    viewModel: TransactionsViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    transactionId: String? = null
) {
    val isEditMode = transactionId != null
    val transactionsState = viewModel.state.collectAsState().value

    // Buscar la transacción a editar
    val transactionToEdit = remember(transactionId, transactionsState.transactions) {
        transactionId?.let { id ->
            transactionsState.transactions.find { it.id == id }
        }
    }

    // Estados con valores iniciales basados en modo edición
    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amount by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var selectedType by remember {
        mutableStateOf(transactionToEdit?.type ?: TransactionType.EXPENSE)
    }
    var selectedCategory by remember {
        mutableStateOf(transactionToEdit?.category ?: "Comida")
    }
    var description by remember { mutableStateOf(transactionToEdit?.description ?: "") }

    // Estados para alertas
    var showTitleError by remember { mutableStateOf(false) }
    var showAmountError by remember { mutableStateOf(false) }
    var showInvalidAmountError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // Categorías separadas por tipo (usando tus categorías existentes + nuevas)
    val expenseCategories = listOf(
        "Comida",
        "Transporte",
        "Entretenimiento",
        "Salud",
        "Educación",
        "Ropa",
        "Casa",
        "Regalos",
        "Viajes",
        "Servicios",
        "Tecnología",
        "Deportes",
        "Cuidado Personal",
        "Mascotas",
        "Impuestos",
        "Otros Gastos"
    )

    val incomeCategories = listOf(
        "Salario",
        "Freelance",
        "Inversiones",
        "Negocio",
        "Regalos",
        "Premios",
        "Ventas",
        "Alquiler",
        "Intereses",
        "Dividendos",
        "Bonos",
        "Comisiones",
        "Honorarios",
        "Reembolsos",
        "Herencia",
        "Otros Ingresos"
    )

    // Categorías actuales basadas en el tipo seleccionado
    val currentCategories = remember(selectedType) {
        if (selectedType == TransactionType.INCOME) incomeCategories else expenseCategories
    }

    // Efecto para cargar datos cuando la transacción se encuentra
    LaunchedEffect(transactionToEdit) {
        if (transactionToEdit != null) {
            title = transactionToEdit.title
            amount = transactionToEdit.amount.toString()
            selectedType = transactionToEdit.type
            selectedCategory = transactionToEdit.category
            description = transactionToEdit.description
        }
    }

    // Efecto para resetear categoría cuando cambia el tipo
    LaunchedEffect(selectedType) {
        if (!currentCategories.contains(selectedCategory)) {
            selectedCategory = currentCategories.first()
        }
    }

    // Función para validar y guardar
    fun validateAndSave() {
        // Resetear errores
        showTitleError = false
        showAmountError = false
        showInvalidAmountError = false

        var isValid = true

        // Validar título
        if (title.isEmpty()) {
            showTitleError = true
            isValid = false
        }

        // Validar monto
        if (amount.isEmpty()) {
            showAmountError = true
            isValid = false
        } else if (amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
            showInvalidAmountError = true
            isValid = false
        }

        if (isValid && !isLoading) {
            isLoading = true
            coroutineScope.launch {
                val userId = viewModel.getCurrentUserId()
                val transactionAmount = amount.toDouble()

                val transaction = if (isEditMode && transactionToEdit != null) {
                    // Modo edición - mantener el mismo ID y fecha original
                    transactionToEdit.copy(
                        title = title,
                        amount = transactionAmount,
                        type = selectedType,
                        category = selectedCategory,
                        description = description
                    )
                } else {
                    // Modo creación - nuevo ID y fecha actual
                    Transaction(
                        id = Transaction().id, // Generar nuevo ID
                        title = title,
                        amount = transactionAmount,
                        type = selectedType,
                        category = selectedCategory,
                        description = description,
                        date = Date(),
                        userId = userId
                    )
                }

                val success = if (isEditMode) {
                    viewModel.updateTransaction(transaction)
                } else {
                    viewModel.addTransaction(transaction)
                }

                if (success) {
                    onSaveSuccess()
                }
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Editar Transacción" else "Nueva Transacción"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { validateAndSave() },
                modifier = Modifier.padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text(if (isEditMode) "Actualizar" else "Guardar")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tipo de transacción
            Text(
                text = "Tipo de Transacción",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TransactionType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = {
                            Text(
                                text = if (type == TransactionType.INCOME) "Ingreso" else "Gasto"
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (type == TransactionType.INCOME)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = if (type == TransactionType.INCOME)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Título
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        showTitleError = false // Resetear error cuando el usuario empiece a escribir
                    },
                    label = { Text("Título") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = "Título")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showTitleError
                )
                if (showTitleError) {
                    Text(
                        text = "El título es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            // Monto
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                            amount = it
                            showAmountError = false // Resetear errores cuando el usuario empiece a escribir
                            showInvalidAmountError = false
                        }
                    },
                    label = { Text("Monto") },
                    leadingIcon = {
                        Icon(Icons.Default.Payments, contentDescription = "Monto")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showAmountError || showInvalidAmountError
                )
                if (showAmountError) {
                    Text(
                        text = "El monto es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                if (showInvalidAmountError) {
                    Text(
                        text = "Ingresa un monto válido mayor a 0",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            // Categoría
            Text(
                text = "Categoría - ${if (selectedType == TransactionType.INCOME) "Ingresos" else "Gastos"}",
                style = MaterialTheme.typography.titleMedium
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(currentCategories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onCategorySelected = { selectedCategory = category }
                    )
                }
            }

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Category, contentDescription = "Descripción")
                },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // Alertas como Snackbar
            if (showTitleError || showAmountError || showInvalidAmountError) {
                LaunchedEffect(showTitleError, showAmountError, showInvalidAmountError) {
                    // El efecto se ejecuta cuando hay errores, mostrando mensajes en los campos
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onCategorySelected: (String) -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = { onCategorySelected(category) },
        label = {
            Text(
                text = category,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Transaction.getCategoryIcon(category),
                contentDescription = category,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    )
}