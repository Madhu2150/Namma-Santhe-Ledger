package com.namma.santhe.ledger.ui.screens

import androidx.compose.foundation.clickable
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
import com.namma.santhe.ledger.data.model.CustomerWithBalance
import com.namma.santhe.ledger.ui.components.EmptyState
import com.namma.santhe.ledger.ui.components.SearchBar
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersListPage(
    viewModel: LedgerViewModel,
    onNavigateToLedger: (Long) -> Unit,
    onNavigateToAddUdari: () -> Unit,
    onNavigateToAddContact: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val customers by viewModel.customersWithBalance.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val displayCustomers = if (searchQuery.isBlank()) customers
    else customers.filter {
        it.customer.name.contains(searchQuery, ignoreCase = true) ||
                it.customer.village.contains(searchQuery, ignoreCase = true)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.updateSearchQuery("") }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Customers",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "${customers.size} contacts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                                .copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // ✅ Add Contact FAB
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddContact,
                containerColor = SantheGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    Icon(Icons.Default.PersonAdd, "Add Contact")
                },
                text = {
                    Text(
                        "Add Contact",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar — searches name AND village
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholder = "Search by name or village...",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (displayCustomers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        emoji = "👥",
                        title = if (searchQuery.isBlank())
                            "No Contacts Yet"
                        else
                            "No Results Found",
                        subtitle = if (searchQuery.isBlank())
                            "Tap 'Add Contact' to add your first customer"
                        else
                            "Try searching by name or village"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        displayCustomers,
                        key = { it.customer.id }
                    ) { customerWithBalance ->
                        ContactCard(
                            customerWithBalance = customerWithBalance,
                            onClick = {
                                onNavigateToLedger(
                                    customerWithBalance.customer.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// ─── Contact Card (Name - Village style) ───
@Composable
private fun ContactCard(
    customerWithBalance: CustomerWithBalance,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ─── Avatar ───
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = customerWithBalance.customer.name
                            .first()
                            .uppercaseChar()
                            .toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // ─── Name + Village ───
            Column(modifier = Modifier.weight(1f)) {

                // "Ravi - Bangalore" style
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = customerWithBalance.customer.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (customerWithBalance.customer.village.isNotBlank()) {
                        Text(
                            text = "-",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.4f)
                        )
                        Text(
                            text = customerWithBalance.customer.village,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Phone number
                if (customerWithBalance.customer.phone.isNotEmpty()) {
                    Text(
                        text = "📞 ${customerWithBalance.customer.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                            .copy(alpha = 0.5f)
                    )
                }
            }

            // ─── Arrow ───
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.3f)
            )
        }
    }
}