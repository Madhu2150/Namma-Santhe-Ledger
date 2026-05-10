package com.namma.santhe.ledger.data.db

import androidx.room.*
import com.namma.santhe.ledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Query("""
    SELECT * FROM transactions 
    ORDER BY timestamp DESC
""")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentTransactions(): Flow<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'UDARI' THEN amount ELSE -amount END), 0)
        FROM transactions
        WHERE customerId = :customerId
    """)
    fun getNetBalanceForCustomer(customerId: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'UDARI' THEN amount ELSE 0 END), 0)
        FROM transactions
        WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay
    """)
    suspend fun getTodayTotalSales(startOfDay: Long, endOfDay: Long): Double

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0)
        FROM transactions
        WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay
    """)
    suspend fun getTodayTotalCollected(startOfDay: Long, endOfDay: Long): Double

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'UDARI' THEN amount ELSE -amount END), 0)
        FROM transactions
    """)
    fun getTotalOutstanding(): Flow<Double>

    @Query("""
        SELECT COUNT(*) FROM transactions
        WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay
    """)
    suspend fun getTodayTransactionCount(startOfDay: Long, endOfDay: Long): Int

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastTransactionForCustomer(customerId: Long): Transaction?

    @Query("""
        SELECT * FROM transactions 
        WHERE timestamp >= :startDate AND timestamp <= :endDate
        ORDER BY timestamp DESC
    """)
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
}