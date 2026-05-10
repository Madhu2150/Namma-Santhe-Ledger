package com.namma.santhe.ledger.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.namma.santhe.ledger.ui.screens.*
import com.namma.santhe.ledger.ui.theme.NammaSantheTheme
import com.namma.santhe.ledger.viewmodel.LedgerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LedgerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaSantheTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(viewModel = viewModel)
                }
            }
        }
    }
}

// ─── Bottom Nav Items ───
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    object Transactions : BottomNavItem(
        route = "transactions",
        title = "Transactions",
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

// ─── All App Screens ───
sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object AddContact : AppScreen("add_contact")
    object Transactions : AppScreen("transactions")
    object Profile : AppScreen("profile")
    object AddUdari : AppScreen("add_udari")
    object CustomersList : AppScreen("customers_list")
    object CustomerLedger : AppScreen("ledger/{customerId}") {
        fun createRoute(id: Long) = "ledger/$id"
    }
    object AddPayment : AppScreen("add_payment/{customerId}") {
        fun createRoute(id: Long) = "add_payment/$id"
    }
    object DailySummary : AppScreen("daily_summary")
}

// ─── Main App ───
@Composable
fun MainApp(viewModel: LedgerViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val strings = LanguageManager.strings

    // Only show bottom nav on these 3 main pages
    val bottomNavRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Transactions.route,
        BottomNavItem.Profile.route
    )
    val showBottomNav = currentRoute in bottomNavRoutes

    CompositionLocalProvider(
        LocalAppStrings provides strings
    ) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomNav,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    NammaBottomNavBar(
                        currentRoute = currentRoute,
                        strings = strings,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                // Always pop back to home
                                // so back button works correctly
                                popUpTo(AppScreen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                viewModel = viewModel,
                innerPadding = innerPadding
            )
        }
    }
}

// ─── Bottom Navigation Bar ───
@Composable
fun NammaBottomNavBar(
    currentRoute: String?,
    strings: AppStrings,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Transactions,
        BottomNavItem.Profile
    )

    NavigationBar(
        modifier = Modifier.clip(
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val label = when (item) {
                BottomNavItem.Home -> strings.home
                BottomNavItem.Transactions -> "Transactions"
                BottomNavItem.Profile -> strings.profile
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected)
                            item.selectedIcon
                        else
                            item.unselectedIcon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.5f)
                )
            )
        }
    }
}

// ─── Navigation Graph ───
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: LedgerViewModel,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        // ── Page 1: Home ──
        composable(AppScreen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAddUdari = {
                    navController.navigate(AppScreen.AddUdari.route)
                },
                onNavigateToDailySummary = {
                    navController.navigate(AppScreen.DailySummary.route)
                },
                onNavigateToLedger = { customerId ->
                    navController.navigate(
                        AppScreen.CustomerLedger.createRoute(customerId)
                    )
                },
                // ✅ FIXED: Customers goes to CustomersList not Transactions
                onNavigateToCustomers = {
                    navController.navigate(AppScreen.CustomersList.route)
                }
            )
        }

        // ── Page 2: Transactions ──
        composable(AppScreen.Transactions.route) {
            TransactionsPage(
                viewModel = viewModel,
                onNavigateToLedger = { customerId ->
                    navController.navigate(
                        AppScreen.CustomerLedger.createRoute(customerId)
                    )
                },
                onNavigateToAddUdari = {
                    navController.navigate(AppScreen.AddUdari.route)
                }
            )
        }

        // ── Page 3: Profile ──
        composable(AppScreen.Profile.route) {
            ProfilePage(viewModel = viewModel)
        }

        // ── Customers List (from Home quick action) ──
        composable(AppScreen.CustomersList.route) {
            CustomersListPage(
                viewModel = viewModel,
                onNavigateToLedger = { customerId ->
                    navController.navigate(
                        AppScreen.CustomerLedger.createRoute(customerId)
                    )
                },
                onNavigateToAddUdari = {
                    navController.navigate(AppScreen.AddUdari.route)
                },
                // ✅ NEW: Add Contact navigation
                onNavigateToAddContact = {
                    navController.navigate(AppScreen.AddContact.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Add Contact Screen ──
        composable(AppScreen.AddContact.route) {
            AddContactPage(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Add Udari ──
        composable(AppScreen.AddUdari.route) {
            AddUdariPage(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    // ✅ Go back to previous screen
                    navController.popBackStack()
                }
            )
        }

        // ── Customer Ledger ──
        composable(
            AppScreen.CustomerLedger.route,
            arguments = listOf(
                navArgument("customerId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments
                ?.getLong("customerId") ?: return@composable

            CustomerLedgerScreen(
                viewModel = viewModel,
                customerId = customerId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddUdari = {
                    navController.navigate(AppScreen.AddUdari.route)
                },
                onNavigateToAddPayment = {
                    navController.navigate(
                        AppScreen.AddPayment.createRoute(customerId)
                    )
                }
            )
        }

        // ── Add Payment ──
        composable(
            AppScreen.AddPayment.route,
            arguments = listOf(
                navArgument("customerId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments
                ?.getLong("customerId") ?: return@composable

            AddPaymentPage(
                viewModel = viewModel,
                customerId = customerId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Daily Summary ──
        composable(AppScreen.DailySummary.route) {
            DailySummaryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}