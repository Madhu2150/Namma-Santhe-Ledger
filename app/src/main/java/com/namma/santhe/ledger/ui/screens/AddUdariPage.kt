package com.namma.santhe.ledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.data.model.Customer
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import com.namma.santhe.ledger.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUdariPage(
    viewModel: LedgerViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val customers by viewModel.customersWithBalance.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ─── Form State ───
    var customerNameInput by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var amountInput by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    // ─── Filtered customer list ───
    val filteredCustomers = remember(customerNameInput, customers) {
        if (customerNameInput.length >= 1) {
            customers.filter {
                it.customer.name.contains(
                    customerNameInput, ignoreCase = true
                ) ||
                        it.customer.village.contains(
                            customerNameInput, ignoreCase = true
                        )
            }.take(6)
        } else emptyList()
    }

    // ─── Listen for events ───
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.TransactionAdded -> {
                    isSubmitting = false
                    onSuccess()
                }
                else -> {
                    isSubmitting = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Udari",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SantheGreen,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor =
                        MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ─── STEP 1: Customer Name ───
            Column {
                Text(
                    text = "Customer Name",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = showDropdown &&
                            filteredCustomers.isNotEmpty(),
                    onExpandedChange = {}
                ) {
                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { input ->
                            customerNameInput = input
                            selectedCustomer = null
                            showDropdown = input.isNotEmpty()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    showDropdown = false
                                }
                            },
                        placeholder = {
                            Text(
                                "Type name or village...",
                                color = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.4f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                null,
                                tint = SantheGreen
                            )
                        },
                        trailingIcon = {
                            if (customerNameInput.isNotEmpty()) {
                                IconButton(onClick = {
                                    customerNameInput = ""
                                    selectedCustomer = null
                                    showDropdown = false
                                }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SantheGreen,
                            unfocusedBorderColor =
                                MaterialTheme.colorScheme.outline
                        )
                    )

                    // ─── Dropdown ───
                    if (filteredCustomers.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            filteredCustomers.forEach { cwb ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment =
                                                Alignment.CenterVertically,
                                            horizontalArrangement =
                                                Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Avatar
                                            Surface(
                                                modifier = Modifier.size(40.dp),
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme
                                                    .colorScheme
                                                    .primaryContainer
                                            ) {
                                                Box(
                                                    contentAlignment =
                                                        Alignment.Center
                                                ) {
                                                    Text(
                                                        text = cwb.customer
                                                            .name
                                                            .first()
                                                            .uppercaseChar()
                                                            .toString(),
                                                        fontSize = 16.sp,
                                                        fontWeight =
                                                            FontWeight.Bold,
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .onPrimaryContainer
                                                    )
                                                }
                                            }

                                            Column {
                                                // Name - Village
                                                Row(
                                                    verticalAlignment =
                                                        Alignment.CenterVertically,
                                                    horizontalArrangement =
                                                        Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = cwb.customer.name,
                                                        fontWeight =
                                                            FontWeight.SemiBold,
                                                        fontSize = 15.sp
                                                    )
                                                    if (cwb.customer.village
                                                            .isNotBlank()
                                                    ) {
                                                        Text(
                                                            "-",
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme
                                                                .colorScheme
                                                                .onSurface
                                                                .copy(alpha = 0.4f)
                                                        )
                                                        Text(
                                                            text = cwb
                                                                .customer
                                                                .village,
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme
                                                                .colorScheme
                                                                .onSurface
                                                                .copy(alpha = 0.6f)
                                                        )
                                                    }
                                                }
                                                if (cwb.netBalance > 0) {
                                                    Text(
                                                        "Due: ₹${
                                                            "%,.0f".format(
                                                                cwb.netBalance
                                                            )
                                                        }",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme
                                                            .colorScheme.error
                                                    )
                                                } else {
                                                    Text(
                                                        "No dues",
                                                        fontSize = 12.sp,
                                                        color = SantheGreen
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedCustomer = cwb.customer
                                        customerNameInput =
                                            if (cwb.customer.village
                                                    .isNotBlank()
                                            )
                                                "${cwb.customer.name}" +
                                                        " - ${cwb.customer.village}"
                                            else
                                                cwb.customer.name
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Status tag
                when {
                    selectedCustomer != null -> {
                        StatusTag(
                            icon = Icons.Default.CheckCircle,
                            text = "Customer selected ✓",
                            color = SantheGreen
                        )
                    }
                    customerNameInput.isNotBlank() -> {
                        StatusTag(
                            icon = Icons.Default.PersonAdd,
                            text = "New customer will be created",
                            color = SantheGreen
                        )
                    }
                }
            }

            // ─── STEP 2: Amount ───
            Column {
                Text(
                    text = "Amount (₹)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { value ->
                        if (value.isEmpty() ||
                            value.matches(
                                Regex("^\\d{0,6}(\\.\\d{0,2})?\$")
                            )
                        ) {
                            amountInput = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "0",
                            color = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.3f),
                            fontSize = 24.sp
                        )
                    },
                    leadingIcon = {
                        Text(
                            "₹",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SantheGreen,
                            modifier = Modifier.padding(start = 14.dp)
                        )
                    },
                    trailingIcon = {
                        if (amountInput.isNotEmpty()) {
                            IconButton(onClick = { amountInput = "" }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SantheGreen,
                        unfocusedBorderColor =
                            MaterialTheme.colorScheme.outline
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ─── Quick Add Buttons ───
                Text(
                    text = "Quick add:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(50, 100, 200, 500).forEach { quickAmount ->
                        OutlinedButton(
                            onClick = {
                                val current =
                                    amountInput.toDoubleOrNull() ?: 0.0
                                val newAmount = current + quickAmount
                                amountInput =
                                    if (newAmount % 1 == 0.0)
                                        newAmount.toInt().toString()
                                    else newAmount.toString()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(
                                horizontal = 4.dp,
                                vertical = 10.dp
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SantheGreen
                            )
                        ) {
                            Text(
                                text = "+$quickAmount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ─── Submit Button ───
            val amount = amountInput.toDoubleOrNull() ?: 0.0
            val isFormValid = customerNameInput.isNotBlank() && amount > 0

            Button(
                onClick = {
                    if (!isSubmitting) {
                        isSubmitting = true
                        val finalAmount = amountInput.toDoubleOrNull() ?: 0.0
                        val finalName = customerNameInput.trim()

                        if (selectedCustomer != null) {
                            // ✅ Existing customer
                            viewModel.addUdari(
                                customerId = selectedCustomer!!.id,
                                amount = finalAmount
                            )
                        } else {
                            // ✅ New customer
                            viewModel.addCustomerAndUdari(
                                name = finalName,
                                village = "",
                                amount = finalAmount,
                                onCustomerCreated = { newId ->
                                    viewModel.addUdari(
                                        customerId = newId,
                                        amount = finalAmount
                                    )
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                enabled = isFormValid && !isSubmitting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SantheGreen,
                    disabledContainerColor = SantheGreen.copy(alpha = 0.4f)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (amount > 0)
                            "Add Udari  ₹${amountInput}"
                        else
                            "Add Udari",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Status Tag ───
@Composable
private fun StatusTag(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Text(
                text = text,
                fontSize = 12.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}