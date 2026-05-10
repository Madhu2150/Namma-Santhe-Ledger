package com.namma.santhe.ledger.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.data.model.Transaction
import com.namma.santhe.ledger.data.model.TransactionType
import com.namma.santhe.ledger.ui.components.*
import com.namma.santhe.ledger.ui.theme.SantheAmber
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.ui.theme.SantheRed
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import com.namma.santhe.ledger.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLedgerScreen(
    viewModel: LedgerViewModel,
    customerId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToAddUdari: () -> Unit,
    onNavigateToAddPayment: () -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.customersWithBalance.collectAsState()
    val transactions by viewModel.selectedCustomerTransactions.collectAsState()
    val netBalance by viewModel.selectedCustomerBalance.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteTransactionDialog by remember { mutableStateOf<Transaction?>(null) }

    val customerWithBalance = customers.find { it.customer.id == customerId }

    LaunchedEffect(customerId) {
        viewModel.selectCustomer(customerId)
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    // Delete transaction dialog
    showDeleteTransactionDialog?.let { transaction ->
        AlertDialog(
            onDismissRequest = { showDeleteTransactionDialog = null },
            title = { Text("Delete Transaction?") },
            text = {
                Text(
                    "Remove ₹${
                        "%,.0f".format(transaction.amount)
                    } ${transaction.type.name.lowercase()} entry?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(transaction)
                    showDeleteTransactionDialog = null
                }) { Text("Delete", color = SantheRed) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteTransactionDialog = null
                }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            customerWithBalance?.customer?.name ?: "Ledger",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Transaction History",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (customerWithBalance?.customer?.phone?.isNotEmpty() == true) {
                        IconButton(onClick = {
                            sendWhatsAppReminder(
                                context = context,
                                phone = customerWithBalance.customer.phone,
                                message = viewModel.buildWhatsAppMessage(customerWithBalance)
                            )
                        }) {
                            Icon(
                                Icons.Default.Share,
                                "WhatsApp Reminder",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = onNavigateToAddPayment,
                    containerColor = SantheAmber,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.CheckCircle, "Record Payment")
                }
                FloatingActionButton(
                    onClick = onNavigateToAddUdari,
                    containerColor = SantheGreen,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.AddCircle, "Add Udari")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            // ✅ FIXED PaddingValues
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 100.dp,
                start = 0.dp,
                end = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Balance Overview Card
            item {
                customerWithBalance?.let { cwb ->
                    BalanceOverviewCard(
                        totalUdari = cwb.totalUdari,
                        totalPayment = cwb.totalPayment,
                        netBalance = netBalance,
                        daysSince = cwb.daysSinceLastTransaction
                    )
                }
            }

            // No phone warning
            customerWithBalance?.let { cwb ->
                if (cwb.customer.phone.isEmpty() && cwb.netBalance > 0) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📲", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Add phone number to send WhatsApp reminder",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Smart Alert
            customerWithBalance?.let { cwb ->
                val alert = viewModel.getSmartAlert(cwb)
                if (alert != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = SantheRed.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = alert,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                color = SantheRed
                            )
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToAddUdari,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SantheGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Udari")
                    }
                    OutlinedButton(
                        onClick = onNavigateToAddPayment,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Payment")
                    }
                }
            }

            // Transaction History Header
            item {
                SectionHeader("Transaction History (${transactions.size})")
            }

            // Empty State
            if (transactions.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "📒",
                        title = "No Transactions Yet",
                        subtitle = "Add the first Udari entry",
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionRow(
                        transaction = transaction,
                        onDelete = { showDeleteTransactionDialog = transaction }
                    )
                }
            }
        }
    }
}

// ─── Balance Overview Card ───
@Composable
private fun BalanceOverviewCard(
    totalUdari: Double,
    totalPayment: Double,
    netBalance: Double,
    daysSince: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (netBalance > 0)
                SantheRed.copy(alpha = 0.08f)
            else
                SantheGreen.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Net Balance",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "₹${"%,.0f".format(netBalance)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = if (netBalance > 0) SantheRed else SantheGreen
            )
            Text(
                text = if (netBalance > 0) "Amount Pending" else "All Cleared ✓",
                fontSize = 13.sp,
                color = if (netBalance > 0) SantheRed else SantheGreen
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Total Udari",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "₹${"%,.0f".format(totalUdari)}",
                        fontWeight = FontWeight.Bold,
                        color = SantheRed
                    )
                }
                VerticalDivider(modifier = Modifier.height(40.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Total Paid",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "₹${"%,.0f".format(totalPayment)}",
                        fontWeight = FontWeight.Bold,
                        color = SantheGreen
                    )
                }
                if (daysSince > 0) {
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Last Txn",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            "$daysSince days ago",
                            fontWeight = FontWeight.Bold,
                            color = if (daysSince >= 14) SantheRed
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ─── Transaction Row ───
@Composable
private fun TransactionRow(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    val isUdari = transaction.type == TransactionType.UDARI
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type Indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (isUdari)
                            SantheRed.copy(0.12f)
                        else
                            SantheGreen.copy(0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUdari) "📤" else "📥",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isUdari) "Udari (Credit)" else "Payment Received",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (isUdari) SantheRed else SantheGreen
                )
                Text(
                    text = dateFormat.format(Date(transaction.timestamp)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isUdari) "+" else "-"}₹${
                        "%,.0f".format(transaction.amount)
                    }",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isUdari) SantheRed else SantheGreen
                )
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = SantheRed) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, null, tint = SantheRed)
                            },
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}

// ─── WhatsApp Intent ───
private fun sendWhatsAppReminder(
    context: Context,
    phone: String,
    message: String
) {
    val cleanPhone = phone.replace(Regex("[^0-9]"), "")
    val phoneWithCode = if (cleanPhone.startsWith("91")) cleanPhone
    else "91$cleanPhone"

    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra("jid", "$phoneWithCode@s.whatsapp.net")
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback if WhatsApp not installed
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(fallback, "Send Reminder via..."))
    }
}