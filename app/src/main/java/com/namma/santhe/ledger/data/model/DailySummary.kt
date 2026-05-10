package com.namma.santhe.ledger.data.model

data class DailySummary(
    val totalSales: Double = 0.0,       // Total Udari given today
    val totalCollected: Double = 0.0,   // Total payments received today
    val pendingDues: Double = 0.0,      // Total outstanding across all customers
    val transactionCount: Int = 0
)