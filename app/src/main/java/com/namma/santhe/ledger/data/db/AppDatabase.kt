package com.namma.santhe.ledger.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namma.santhe.ledger.data.model.Customer
import com.namma.santhe.ledger.data.model.Transaction

@Database(
    entities = [Customer::class, Transaction::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "namma_santhe_db"
    }
}