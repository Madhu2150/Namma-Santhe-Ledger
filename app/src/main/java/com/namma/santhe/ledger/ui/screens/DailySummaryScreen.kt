package com.namma.santhe.ledger.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.ui.components.*
import com.namma.santhe.ledger.ui.theme.SantheAmber
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.ui.theme.SantheRed
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryScreen(
    viewModel: LedgerViewModel,
    onNavigateBack: () -> Unit
) {
    val homeState by viewModel.homeUiState.collectAsState()
    val customers by viewModel.customersWithBalance.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()

    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    val today = dateFormat.format(Date())

    LaunchedEffect(Unit) { viewModel.refreshDailySummary() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Daily Summary", fontWeight = FontWeight.Bold)
                        Text(today, fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
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
                    IconButton(onClick = { viewModel.refreshDailySummary() }) {
                        Icon(
                            Icons.Default.Refresh, "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ─── Main Summary Banner ───
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📊 Today's Market Report",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "\"Today you sold for ₹${"%,.0f".format(homeState.todaySales)};" +
                                    " Dues pending ₹${"%,.0f".format(totalOutstanding)}.\"",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ─── Today's Stats Grid ───
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Udari Given\nToday",
                        amount = homeState.todaySales,
                        icon = "📤",
                        cardColor = SantheRed.copy(alpha = 0.1f),
                        textColor = SantheRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Collected\nToday",
                        amount = homeState.todayCollections,
                        icon = "📥",
                        cardColor = SantheGreen.copy(alpha = 0.1f),
                        textColor = SantheGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SantheAmber.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Outstanding", fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Text(
                                "₹${"%,.0f".format(totalOutstanding)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = SantheAmber
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Transactions\nToday", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.End)
                            Text(
                                "${homeState.todayTransactionCount}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ─── Overdue Customers ───
            val overdueList = customers.filter { it.isOverdue }
            if (overdueList.isNotEmpty()) {
                item { SectionHeader("⚠️ Overdue Customers (${overdueList.size})") }
                items(overdueList) { customer ->
                    val alert = viewModel.getSmartAlert(customer)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SantheRed.copy(alpha = 0.07f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomerAvatar(name = customer.customer.name, hasOverdue = true)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(customer.customer.name, fontWeight = FontWeight.Bold)
                                Text(
                                    alert ?: "Overdue",
                                    fontSize = 12.sp, color = SantheRed
                                )
                            }
                            BalanceChip(balance = customer.netBalance)
                        }
                    }
                }
            }

            // ─── Top Debtors ───
            val topDebtors = customers
                .filter { it.netBalance > 0 }
                .sortedByDescending { it.netBalance }
                .take(5)

            if (topDebtors.isNotEmpty()) {
                item { SectionHeader("💰 Top Outstanding (by amount)") }
                items(topDebtors) { customer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomerAvatar(name = customer.customer.name)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(customer.customer.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (customer.customer.phone.isNotEmpty()) customer.customer.phone
                                    else "No phone",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            BalanceChip(balance = customer.netBalance)
                        }
                    }
                }
            }

            // ─── Net P&L ───
            item {
                SectionHeader("📈 Today's P&L")
                val netToday = homeState.todayCollections - homeState.todaySales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (netToday >= 0)
                            SantheGreen.copy(alpha = 0.08f)
                        else
                            SantheRed.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Net Collection vs Udari", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text(
                            "${if (netToday >= 0) "+" else ""}₹${"%,.0f".format(netToday)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (netToday >= 0) SantheGreen else SantheRed
                        )
                        Text(
                            if (netToday >= 0) "More collected than given today 👍"
                            else "More credit given than collected today",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}