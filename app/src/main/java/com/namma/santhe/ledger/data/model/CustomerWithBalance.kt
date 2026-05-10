package com.namma.santhe.ledger.data.model

data class CustomerWithBalance(
    val customer: Customer,
    val totalUdari: Double = 0.0,
    val totalPayment: Double = 0.0,
    val lastTransactionDate: Long = 0L
) {
    val netBalance: Double get() = totalUdari - totalPayment
    val isOverdue: Boolean get() {
        if (netBalance <= 0) return false
        val daysSinceLastTransaction = (System.currentTimeMillis() - lastTransactionDate) / (1000 * 60 * 60 * 24)
        return daysSinceLastTransaction >= 14
    }
    val daysSinceLastTransaction: Long get() {
        if (lastTransactionDate == 0L) return 0L
        return (System.currentTimeMillis() - lastTransactionDate) / (1000 * 60 * 60 * 24)
    }
}