package com.namma.santhe.ledger.data.repository

import com.namma.santhe.ledger.data.db.CustomerDao
import com.namma.santhe.ledger.data.db.CustomerBalanceResult
import com.namma.santhe.ledger.data.db.TransactionDao
import com.namma.santhe.ledger.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {

    // ───── Customer Operations ─────

    suspend fun addCustomer(
        name: String,
        phone: String,
        village: String,
        address: String
    ): Long {
        return customerDao.insertCustomer(
            Customer(
                name = name.trim(),
                phone = phone.trim(),
                village = village.trim(),
                address = address.trim()
            )
        )
    }

    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

    suspend fun getCustomerById(id: Long): Customer? = customerDao.getCustomerById(id)

    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()

    fun getCustomersWithBalance(): Flow<List<CustomerWithBalance>> =
        customerDao.getCustomersWithBalance().map { list ->
            list.map { it.toCustomerWithBalance() }
        }

    fun searchCustomersWithBalance(query: String): Flow<List<CustomerWithBalance>> =
        customerDao.searchCustomersWithBalance(query).map { list ->
            list.map { it.toCustomerWithBalance() }
        }

    // ───── Transaction Operations ─────

    suspend fun addUdari(customerId: Long, amount: Double, note: String = ""): Long {
        return transactionDao.insertTransaction(
            Transaction(
                customerId = customerId,
                amount = amount,
                type = TransactionType.UDARI,
                note = note
            )
        )
    }

    suspend fun recordPayment(customerId: Long, amount: Double, note: String = ""): Long {
        return transactionDao.insertTransaction(
            Transaction(
                customerId = customerId,
                amount = amount,
                type = TransactionType.PAYMENT,
                note = note
            )
        )
    }

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    fun getTransactionsForCustomer(customerId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsForCustomer(customerId)

    fun getNetBalanceForCustomer(customerId: Long): Flow<Double> =
        transactionDao.getNetBalanceForCustomer(customerId)

    fun getTotalOutstanding(): Flow<Double> = transactionDao.getTotalOutstanding()

    // ───── Daily Summary ─────

    suspend fun getDailySummary(): DailySummary {
        val (start, end) = getTodayRange()
        val totalSales = transactionDao.getTodayTotalSales(start, end)
        val totalCollected = transactionDao.getTodayTotalCollected(start, end)
        val pendingDues = transactionDao.getTotalOutstanding()
        val count = transactionDao.getTodayTransactionCount(start, end)

        // Collect the flow value
        var outstanding = 0.0
        // We use a simple collect here via stateIn in ViewModel; use first() extension
        return DailySummary(
            totalSales = totalSales,
            totalCollected = totalCollected,
            pendingDues = outstanding,
            transactionCount = count
        )
    }

    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()
    fun getRecentTransactions(): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions()

    private fun getTodayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    suspend fun getTodaySales(): Double {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayTotalSales(start, end)
    }

    suspend fun getTodayCollections(): Double {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayTotalCollected(start, end)
    }

    suspend fun getTodayTransactionCount(): Int {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayTransactionCount(start, end)
    }
}

private fun CustomerBalanceResult.toCustomerWithBalance() = CustomerWithBalance(
    customer = Customer(
        id = this.id,
        name = this.name,
        phone = this.phone,
        address = this.address,
        createdAt = this.createdAt
    ),
    totalUdari = this.totalUdari,
    totalPayment = this.totalPayment,
    lastTransactionDate = this.lastTransactionDate
)