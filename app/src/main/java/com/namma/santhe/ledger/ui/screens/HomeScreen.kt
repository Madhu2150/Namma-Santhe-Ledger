package com.namma.santhe.ledger.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.ui.LocalAppStrings
import com.namma.santhe.ledger.ui.components.*
import com.namma.santhe.ledger.ui.theme.SantheAmber
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.ui.theme.SantheRed
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import com.namma.santhe.ledger.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LedgerViewModel,
    onNavigateToAddUdari: () -> Unit,
    onNavigateToDailySummary: () -> Unit,
    onNavigateToLedger: (Long) -> Unit,
    onNavigateToCustomers: () -> Unit
) {
    val strings = LocalAppStrings.current
    val homeState by viewModel.homeUiState.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.refreshDailySummary() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "🌿 ${strings.appName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Digital Khata for Santhe Vendors",
                            fontSize = 11.sp,
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
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddUdari,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, strings.addUdari) },
                text = {
                    Text(
                        strings.addUdari,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 100.dp
            )
        ) {

            // ─── 1. Total Outstanding Card ───
            item {
                TotalOutstandingCard(
                    totalOutstanding = totalOutstanding
                )
            }

            // ─── 2. Today Stats Row ───
            item {
                TodayStatsRow(
                    todayUdari = homeState.todaySales,
                    todayCollected = homeState.todayCollections
                )
            }

            // ─── 3. Quick Actions ───
            item {
                QuickActionsSection(
                    onNavigateToCustomers = onNavigateToCustomers,
                    onNavigateToDailySummary = onNavigateToDailySummary
                )
            }
        }
    }
}

// ─── Total Outstanding Card ───
@Composable
fun TotalOutstandingCard(
    totalOutstanding: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📋 Total Outstanding Dues",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
                    .copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "₹${"%,.0f".format(totalOutstanding)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 52.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Pending Udari across all customers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
                    .copy(alpha = 0.7f)
            )
        }
    }
}

// ─── Today Stats Row ───
@Composable
private fun TodayStatsRow(
    todayUdari: Double,
    todayCollected: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Today's Udari
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SantheRed.copy(alpha = 0.08f)
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📤", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₹${"%,.0f".format(todayUdari)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = SantheRed
                )
                Text(
                    text = "Today's Udari",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.6f)
                )
            }
        }

        // Today's Collected
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SantheGreen.copy(alpha = 0.08f)
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📥", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₹${"%,.0f".format(todayCollected)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = SantheGreen
                )
                Text(
                    text = "Collected Today",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ─── Quick Actions Section ───
@Composable
private fun QuickActionsSection(
    onNavigateToCustomers: () -> Unit,
    onNavigateToDailySummary: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Customers Button
        QuickActionCard(
            icon = Icons.Default.People,
            title = "Customers",
            subtitle = "View all your customers",
            iconColor = SantheGreen,
            onClick = onNavigateToCustomers
        )

        // Daily Summary Button
        QuickActionCard(
            icon = Icons.Default.BarChart,
            title = "Daily Summary",
            subtitle = "Today's market report",
            iconColor = SantheAmber,
            onClick = onNavigateToDailySummary
        )
    }
}

// ─── Quick Action Card ───
@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Box
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.55f)
                )
            }

            // Arrow
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.3f)
            )
        }
    }
}