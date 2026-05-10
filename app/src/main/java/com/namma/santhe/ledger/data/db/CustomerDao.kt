package com.namma.santhe.ledger.data.db

import androidx.room.*
import com.namma.santhe.ledger.data.model.Customer
import com.namma.santhe.ledger.data.model.CustomerWithBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("""
        SELECT 
            c.id, c.name, c.phone, c.address, c.createdAt,
            COALESCE(SUM(CASE WHEN t.type = 'UDARI' THEN t.amount ELSE 0 END), 0) as totalUdari,
            COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0) as totalPayment,
            COALESCE(MAX(t.timestamp), 0) as lastTransactionDate
        FROM customers c
        LEFT JOIN transactions t ON c.id = t.customerId
        GROUP BY c.id
        ORDER BY c.name ASC
    """)
    fun getCustomersWithBalance(): Flow<List<CustomerBalanceResult>>

    @Query("""
        SELECT 
            c.id, c.name, c.phone, c.address, c.createdAt,
            COALESCE(SUM(CASE WHEN t.type = 'UDARI' THEN t.amount ELSE 0 END), 0) as totalUdari,
            COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0) as totalPayment,
            COALESCE(MAX(t.timestamp), 0) as lastTransactionDate
        FROM customers c
        LEFT JOIN transactions t ON c.id = t.customerId
        WHERE c.name LIKE '%' || :query || '%'
        GROUP BY c.id
        ORDER BY c.name ASC
    """)
    fun searchCustomersWithBalance(query: String): Flow<List<CustomerBalanceResult>>

    @Query("SELECT COUNT(*) FROM customers")
    suspend fun getCustomerCount(): Int
}

// Data class for Room query result
data class CustomerBalanceResult(
    val id: Long,
    val name: String,
    val phone: String,
    val address: String,
    val createdAt: Long,
    val totalUdari: Double,
    val totalPayment: Double,
    val lastTransactionDate: Long
)