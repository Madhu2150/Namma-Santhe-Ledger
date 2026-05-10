package com.namma.santhe.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.santhe.ledger.data.model.*
import com.namma.santhe.ledger.data.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalOutstanding: Double = 0.0,
    val todaySales: Double = 0.0,
    val todayCollections: Double = 0.0,
    val todayTransactionCount: Int = 0,
    val overdueCustomers: List<CustomerWithBalance> = emptyList(),
    val isLoading: Boolean = false
)

data class CustomerUiState(
    val customers: List<CustomerWithBalance> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val selectedCustomer: Customer? = null,
    val netBalance: Double = 0.0,
    val isLoading: Boolean = false
)

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data object NavigateBack : UiEvent()
    data class TransactionAdded(val customerId: Long) : UiEvent()
}

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    // ─── Search Query ───
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ─── UI Events ───
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    // ─── Total Outstanding (always live) ───
    val totalOutstanding: StateFlow<Double> = repository.getTotalOutstanding()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ─── All Transactions (for Transaction Page) ───
    val allRecentTransactions: StateFlow<List<Transaction>> =
        repository.getAllTransactions()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    // ─── Customers with Balance ───
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val customersWithBalance: StateFlow<List<CustomerWithBalance>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getCustomersWithBalance()
            else repository.searchCustomersWithBalance(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── Home UI State ───
    private val _homeUiState = MutableStateFlow(HomeUiState(isLoading = true))
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    // ─── Selected Customer for Ledger ───
    private val _selectedCustomerId = MutableStateFlow<Long?>(null)

    val selectedCustomerTransactions: StateFlow<List<Transaction>> =
        _selectedCustomerId
            .filterNotNull()
            .flatMapLatest { id -> repository.getTransactionsForCustomer(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCustomerBalance: StateFlow<Double> =
        _selectedCustomerId
            .filterNotNull()
            .flatMapLatest { id -> repository.getNetBalanceForCustomer(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        observeTotalOutstanding()
        refreshDailySummary()
    }

    private fun observeTotalOutstanding() {
        viewModelScope.launch {
            repository.getTotalOutstanding().collect { outstanding ->
                _homeUiState.update { it.copy(totalOutstanding = outstanding) }
            }
        }
    }


    fun refreshDailySummary() {
        viewModelScope.launch {
            _homeUiState.update { it.copy(isLoading = true) }
            try {
                val sales = repository.getTodaySales()
                val collections = repository.getTodayCollections()
                val count = repository.getTodayTransactionCount()
                val overdue = customersWithBalance.value.filter { it.isOverdue }

                _homeUiState.update {
                    it.copy(
                        todaySales = sales,
                        todayCollections = collections,
                        todayTransactionCount = count,
                        overdueCustomers = overdue,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _homeUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ─── Search ───
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCustomer(
        name: String,
        phone: String,
        village: String,
        address: String,
        onSuccess: (Long) -> Unit = {}
    ) {
        if (name.isBlank()) {
            viewModelScope.launch {
                _events.emit(
                    UiEvent.ShowSnackbar("Customer name cannot be empty")
                )
            }
            return
        }
        viewModelScope.launch {
            val id = repository.addCustomer(
                name = name,
                phone = phone,
                village = village,
                address = address
            )
            _events.emit(UiEvent.ShowSnackbar("'$name' added!"))
            onSuccess(id)
        }
    }

    fun addCustomerAndUdari(
        name: String,
        village: String,
        amount: Double,
        onCustomerCreated: (Long) -> Unit
    ) {
        if (name.isBlank()) {
            viewModelScope.launch {
                _events.emit(
                    UiEvent.ShowSnackbar("Customer name cannot be empty")
                )
            }
            return
        }
        viewModelScope.launch {
            // Step 1 — Create customer
            val newId = repository.addCustomer(
                name = name.trim(),
                phone = "",
                village = village.trim(),
                address = ""
            )
            // Step 2 — Add udari
            repository.addUdari(newId, amount)
            // Step 3 — Refresh
            refreshDailySummary()
            // Step 4 — Notify
            _events.emit(
                UiEvent.ShowSnackbar(
                    "Udari of ₹${amount.toInt()} added for $name!"
                )
            )
            // Step 5 — Navigate
            _events.emit(UiEvent.TransactionAdded(newId))
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            _events.emit(UiEvent.ShowSnackbar("Customer updated"))
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            _events.emit(UiEvent.ShowSnackbar("Customer '${customer.name}' deleted"))
        }
    }

    // ─── Transaction Operations ───
    fun selectCustomer(customerId: Long) {
        _selectedCustomerId.value = customerId
    }

    fun addUdari(
        customerId: Long,
        amount: Double,
        note: String = ""
    ) {
        if (amount <= 0) {
            viewModelScope.launch {
                _events.emit(
                    UiEvent.ShowSnackbar("Amount must be greater than 0")
                )
            }
            return
        }
        viewModelScope.launch {
            repository.addUdari(customerId, amount, note)
            refreshDailySummary()
            _events.emit(
                UiEvent.ShowSnackbar("Udari of ₹${amount.toInt()} added!")
            )
            _events.emit(UiEvent.TransactionAdded(customerId))
        }
    }

    fun recordPayment(
        customerId: Long,
        amount: Double,
        note: String = ""
    ) {
        if (amount <= 0) {
            viewModelScope.launch {
                _events.emit(
                    UiEvent.ShowSnackbar("Amount must be greater than 0")
                )
            }
            return
        }
        viewModelScope.launch {
            repository.recordPayment(customerId, amount, note)
            refreshDailySummary()
            _events.emit(
                UiEvent.ShowSnackbar(
                    "Payment of ₹${amount.toInt()} recorded!"
                )
            )
            // ✅ FIXED: emit TransactionAdded so screen navigates back
            _events.emit(UiEvent.TransactionAdded(customerId))
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            refreshDailySummary()
            _events.emit(UiEvent.ShowSnackbar("Transaction deleted"))
        }
    }

    // ─── GenAI Smart Alert ───
    fun getSmartAlert(customer: CustomerWithBalance): String? {
        return when {
            customer.netBalance <= 0 -> null
            customer.daysSinceLastTransaction >= 30 ->
                "⚠️ ${customer.customer.name} hasn't paid in ${customer.daysSinceLastTransaction} days! Outstanding: ₹${customer.netBalance.toInt()}"
            customer.daysSinceLastTransaction >= 14 ->
                "🔔 ${customer.customer.name} has ₹${customer.netBalance.toInt()} due for ${customer.daysSinceLastTransaction} days"
            customer.netBalance >= 1000 ->
                "💰 ${customer.customer.name} owes a large amount: ₹${customer.netBalance.toInt()}"
            else -> null
        }
    }

    // ─── WhatsApp Message Builder ───
    fun buildWhatsAppMessage(customer: CustomerWithBalance): String {
        val balance = customer.netBalance
        return """
            🙏 Namaste ${customer.customer.name}!
            
            Namma Santhe Ledger ನಿಂದ ಸ್ನೇಹಪೂರ್ವಕ ಜ್ಞಾಪನೆ:
            
            ನಿಮ್ಮ ಬಾಕಿ ಮೊತ್ತ: ₹${balance.toInt()}
            
            ದಯವಿಟ್ಟು ಅನುಕೂಲವಾದಾಗ ಪಾವತಿಸಿ.
            ಧನ್ಯವಾದಗಳು! 🙏
        """.trimIndent()
    }
}