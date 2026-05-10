package com.namma.santhe.ledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.santhe.ledger.ui.AppLanguage
import com.namma.santhe.ledger.ui.LanguageManager
import com.namma.santhe.ledger.ui.LocalAppStrings
import com.namma.santhe.ledger.ui.theme.SantheAmber
import com.namma.santhe.ledger.ui.theme.SantheGreen
import com.namma.santhe.ledger.ui.theme.SantheRed
import com.namma.santhe.ledger.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(viewModel: LedgerViewModel) {

    val strings = LocalAppStrings.current
    val context = LocalContext.current

    val customers by viewModel.customersWithBalance.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val homeState by viewModel.homeUiState.collectAsState()

    var vendorName by remember { mutableStateOf("My Santhe Shop") }
    var isEditingName by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(vendorName) }

    // Language state
    var selectedLanguage by remember {
        mutableStateOf(LanguageManager.currentLanguage)
    }

    val totalCustomers = customers.size
    val customersWithDues = customers.filter { it.netBalance > 0 }.size
    val overdueCount = customers.filter { it.isOverdue }.size
    val clearedCount = customers.filter { it.netBalance <= 0 }.size
    val totalUdariGiven = customers.sumOf { it.totalUdari }
    val totalCollected = customers.sumOf { it.totalPayment }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.myProfile,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ─── Profile Header ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏪", fontSize = 44.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditingName) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
                                    .copy(alpha = 0.5f),
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    vendorName = tempName
                                    isEditingName = false
                                }) {
                                    Icon(
                                        Icons.Default.Check,
                                        strings.save,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = vendorName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    tempName = vendorName
                                    isEditingName = true
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    strings.edit,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                        .copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = strings.nammaSantheVendor,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ─── Language Settings Card ───
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🌐", fontSize = 20.sp)
                            Text(
                                strings.languageSettings,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            strings.selectLanguage,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // ─── Language Options ───
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // English Option
                            LanguageOptionCard(
                                flag = "🇬🇧",
                                language = strings.english,
                                subtitle = "English",
                                isSelected = selectedLanguage == AppLanguage.ENGLISH,
                                onClick = {
                                    selectedLanguage = AppLanguage.ENGLISH
                                    LanguageManager.setLanguage(
                                        context,
                                        AppLanguage.ENGLISH
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Kannada Option
                            LanguageOptionCard(
                                flag = "🇮🇳",
                                language = strings.kannada,
                                subtitle = "ಕನ್ನಡ",
                                isSelected = selectedLanguage == AppLanguage.KANNADA,
                                onClick = {
                                    selectedLanguage = AppLanguage.KANNADA
                                    LanguageManager.setLanguage(
                                        context,
                                        AppLanguage.KANNADA
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ─── Business Overview ───
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "📊 ${strings.businessOverview}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(
                                value = "$totalCustomers",
                                label = strings.totalCustomers,
                                emoji = "👥"
                            )
                            VerticalDivider(modifier = Modifier.height(60.dp))
                            ProfileStatItem(
                                value = "$customersWithDues",
                                label = strings.withDues,
                                emoji = "📋"
                            )
                            VerticalDivider(modifier = Modifier.height(60.dp))
                            ProfileStatItem(
                                value = "$overdueCount",
                                label = strings.overdue,
                                emoji = "⚠️"
                            )
                            VerticalDivider(modifier = Modifier.height(60.dp))
                            ProfileStatItem(
                                value = "$clearedCount",
                                label = strings.allClearedTick,
                                emoji = "✅"
                            )
                        }
                    }
                }

                // ─── Financial Summary ───
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "💰 ${strings.financialSummary}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        FinancialRow(
                            label = strings.totalUdariGiven,
                            value = "₹${"%,.0f".format(totalUdariGiven)}",
                            color = SantheRed,
                            icon = "📤"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        FinancialRow(
                            label = strings.totalCollected,
                            value = "₹${"%,.0f".format(totalCollected)}",
                            color = SantheGreen,
                            icon = "📥"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        FinancialRow(
                            label = strings.outstandingDues,
                            value = "₹${"%,.0f".format(totalOutstanding)}",
                            color = SantheAmber,
                            icon = "⏳"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val collectionRate = if (totalUdariGiven > 0)
                            (totalCollected / totalUdariGiven * 100).toInt()
                        else 0

                        Text(
                            strings.collectionRate,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { collectionRate / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SantheGreen,
                            trackColor = SantheRed.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$collectionRate${strings.ofTotalCollected}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (collectionRate >= 70) SantheGreen else SantheAmber
                        )
                    }
                }

                // ─── Today's Activity ───
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "📅 ${strings.todayActivity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(
                                value = "₹${"%,.0f".format(homeState.todaySales)}",
                                label = strings.todayUdari,
                                emoji = "📤"
                            )
                            VerticalDivider(modifier = Modifier.height(60.dp))
                            ProfileStatItem(
                                value = "₹${"%,.0f".format(homeState.todayCollections)}",
                                label = strings.todayCollected,
                                emoji = "📥"
                            )
                            VerticalDivider(modifier = Modifier.height(60.dp))
                            ProfileStatItem(
                                value = "${homeState.todayTransactionCount}",
                                label = strings.totalTransactions,
                                emoji = "🔢"
                            )
                        }
                    }
                }

                // ─── App Info ───
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "ℹ️ ${strings.appInfo}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(
                            icon = Icons.Default.Info,
                            label = strings.appNameLabel,
                            value = strings.appName
                        )
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = strings.version,
                            value = "1.0.0"
                        )
                        InfoRow(
                            icon = Icons.Default.Storage,
                            label = strings.storage,
                            value = strings.localOffline
                        )
                        InfoRow(
                            icon = Icons.Default.Security,
                            label = strings.data,
                            value = strings.onDeviceOnly
                        )
                        InfoRow(
                            icon = Icons.Default.Wifi,
                            label = strings.internet,
                            value = strings.notRequired
                        )
                    }
                }

                // ─── Built for Bharat ───
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌿", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            strings.appName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            strings.builtForBharat,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ─── Language Option Card ───
@Composable
private fun LanguageOptionCard(
    flag: String,
    language: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(flag, fontSize = 28.sp)

            Text(
                text = subtitle,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Active",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ─── Profile Stat Item ───
@Composable
private fun ProfileStatItem(
    value: String,
    label: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

// ─── Financial Row ───
@Composable
private fun FinancialRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(icon, fontSize = 18.sp)
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = color
        )
    }
}

// ─── Info Row ───
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}