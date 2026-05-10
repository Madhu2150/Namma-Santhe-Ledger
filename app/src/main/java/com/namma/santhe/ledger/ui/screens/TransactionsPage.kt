package com.namma.santhe.ledger.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.data.model.Transaction
import com.namma.santhe.ledger.data.model.TransactionType
import com.namma.santhe.ledger.ui.LocalAppStrings
import com.namma.santhe.ledger.ui.components.EmptyState
import com.namma.santhe.ledger.ui.theme.SantheAmber
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.ui.theme.SantheRed
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsPage(
    viewModel: LedgerViewModel,
    onNavigateToLedger: (Long) -> Unit,
    onNavigateToAddUdari: () -> Unit
) {
    val customers by viewModel.customersWithBalance.collectAsState()
    val recentTransactions by viewModel.allRecentTransactions.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val homeState by viewModel.homeUiState.collectAsState()

    // Tab state
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Udari", "Payments")

    // Filter transactions by tab
    val filteredTransactions = when (selectedTab) {
        0 -> recentTransactions
        1 -> recentTransactions.filter { it.type == TransactionType.UDARI }
        2 -> recentTransactions.filter { it.type == TransactionType.PAYMENT }
        else -> recentTransactions
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Transactions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "${recentTransactions.size} total records",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                                .copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { viewModel.refreshDailySummary() }) {
                        Icon(
                            Icons.Default.Refresh,
                            "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddUdari,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    "Add Udari",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ─── Summary Strip ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TransactionSummaryChip(
                    label = "Outstanding",
                    value = "₹${"%,.0f".format(totalOutstanding)}",
                    color = SantheRed,
                    modifier = Modifier.weight(1f)
                )
                TransactionSummaryChip(
                    label = "Today Udari",
                    value = "₹${"%,.0f".format(homeState.todaySales)}",
                    color = SantheAmber,
                    modifier = Modifier.weight(1f)
                )
                TransactionSummaryChip(
                    label = "Today Collected",
                    value = "₹${"%,.0f".format(homeState.todayCollections)}",
                    color = SantheGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            // ─── Filter Tabs ───
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider() }
            ) {
                tabs.forEachIndexed { index, title ->
                    val count = when (index) {
                        0 -> recentTransactions.size
                        1 -> recentTransactions.count {
                            it.type == TransactionType.UDARI
                        }
                        2 -> recentTransactions.count {
                            it.type == TransactionType.PAYMENT
                        }
                        else -> 0
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index)
                                        FontWeight.Bold
                                    else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedTab == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "$count",
                                        modifier = Modifier.padding(
                                            horizontal = 6.dp,
                                            vertical = 1.dp
                                        ),
                                        fontSize = 11.sp,
                                        color = if (selectedTab == index)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // ─── Transaction List ───
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        emoji = "📋",
                        title = "No Transactions Yet",
                        subtitle = "Tap + to add your first Udari"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Group transactions by date
                    val grouped = filteredTransactions.groupBy { txn ->
                        val sdf = SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.getDefault()
                        )
                        sdf.format(Date(txn.timestamp))
                    }

                    grouped.forEach { (date, txns) ->
                        // Date Header
                        item {
                            DateHeader(date = date)
                        }

                        // Transactions for that date
                        items(txns, key = { it.id }) { transaction ->
                            val customerName = customers.find {
                                it.customer.id == transaction.customerId
                            }?.customer?.name ?: "Unknown"

                            TransactionListItem(
                                transaction = transaction,
                                customerName = customerName,
                                onClick = {
                                    onNavigateToLedger(transaction.customerId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Summary Chip ───
@Composable
private fun TransactionSummaryChip(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = color,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = color.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Date Header ───
@Composable
private fun DateHeader(date: String) {
    val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        .format(Date())
    val yesterday = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        .format(Date(System.currentTimeMillis() - 86400000))

    val displayDate = when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = displayDate,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 4.dp
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.6f)
            )
        }
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

// ─── Transaction List Item ───
@Composable
private fun TransactionListItem(
    transaction: Transaction,
    customerName: String,
    onClick: () -> Unit
) {
    val isUdari = transaction.type == TransactionType.UDARI
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ─── Type Icon ───
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isUdari)
                            SantheRed.copy(alpha = 0.1f)
                        else
                            SantheGreen.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUdari) "📤" else "📥",
                    fontSize = 24.sp
                )
            }

            // ─── Info ───
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (isUdari) "Udari given" else "Payment received",
                    fontSize = 12.sp,
                    color = if (isUdari)
                        SantheRed.copy(alpha = 0.8f)
                    else
                        SantheGreen.copy(alpha = 0.8f)
                )
                Text(
                    text = timeFormat.format(Date(transaction.timestamp)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.45f)
                )
            }

            // ─── Amount ───
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isUdari) "+" else "-"}₹${
                        "%,.0f".format(transaction.amount)
                    }",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isUdari) SantheRed else SantheGreen
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isUdari)
                        SantheRed.copy(alpha = 0.1f)
                    else
                        SantheGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (isUdari) "Udari" else "Paid",
                        modifier = Modifier.padding(
                            horizontal = 6.dp,
                            vertical = 2.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isUdari) SantheRed else SantheGreen
                    )
                }
            }
        }
    }
}