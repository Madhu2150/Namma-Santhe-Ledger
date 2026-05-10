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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.ui.theme.SantheAmber
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import com.namma.santhe.ledger.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentPage(
    viewModel: LedgerViewModel,
    customerId: Long,
    onNavigateBack: () -> Unit
) {
    val customers by viewModel.customersWithBalance.collectAsState()
    val customer = customers.find { it.customer.id == customerId }
    val snackbarHostState = remember { SnackbarHostState() }

    var amountInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // ✅ Select customer when screen opens
    LaunchedEffect(customerId) {
        viewModel.selectCustomer(customerId)
    }

    // ✅ Listen for events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.TransactionAdded -> {
                    // ✅ Navigate back when payment is recorded
                    isSubmitting = false
                    onNavigateBack()
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
                        "Record Payment",
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
                    containerColor = SantheAmber,
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

            // ─── Customer Info Card ───
            customer?.let { cwb ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SantheAmber.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Avatar
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(50),
                            color = SantheAmber.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cwb.customer.name
                                        .first()
                                        .uppercaseChar()
                                        .toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SantheAmber
                                )
                            }
                        }

                        Column {
                            // Name - Village
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = cwb.customer.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                if (cwb.customer.village.isNotBlank()) {
                                    Text(
                                        text = "-",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme
                                            .onSurface.copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = cwb.customer.village,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme
                                            .onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Outstanding amount
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Outstanding:",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme
                                        .onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "₹${
                                        "%,.0f".format(cwb.netBalance)
                                    }",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cwb.netBalance > 0)
                                        MaterialTheme.colorScheme.error
                                    else SantheGreen
                                )
                            }
                        }
                    }
                }
            }

            // ─── Amount Field ───
            Column {
                Text(
                    text = "Payment Amount (₹)",
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
                            color = SantheAmber,
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
                        focusedBorderColor = SantheAmber,
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
                                contentColor = SantheAmber
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

                Spacer(modifier = Modifier.height(8.dp))

                // ─── Full Payment Shortcut ───
                customer?.let { cwb ->
                    if (cwb.netBalance > 0) {
                        OutlinedButton(
                            onClick = {
                                amountInput = cwb.netBalance
                                    .toInt().toString()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SantheGreen
                            )
                        ) {
                            Icon(
                                Icons.Default.DoneAll,
                                null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Full Payment  ₹${
                                    "%,.0f".format(cwb.netBalance)
                                }",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ─── Submit Button ───
            val amount = amountInput.toDoubleOrNull() ?: 0.0
            val isValid = amount > 0

            Button(
                onClick = {
                    if (!isSubmitting && isValid) {
                        isSubmitting = true
                        viewModel.recordPayment(
                            customerId = customerId,
                            amount = amount
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                enabled = isValid && !isSubmitting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SantheAmber,
                    disabledContainerColor = SantheAmber.copy(alpha = 0.4f)
                )
            ) {
                if (isSubmitting) {
                    // ✅ Show loading spinner while processing
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isValid)
                            "Record Payment  ₹${amountInput}"
                        else
                            "Record Payment",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}