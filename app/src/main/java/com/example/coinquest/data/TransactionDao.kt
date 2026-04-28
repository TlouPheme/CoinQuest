package com.example.coinquest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the [Transaction] entity.
 * Provides methods for performing CRUD operations and complex aggregations.
 */
@Dao
interface TransactionDao {
    /**
     * Retrieves all transactions from the database, ordered by date (newest first).
     * Returns a [Flow] to allow reactive UI updates whenever the database changes.
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Retrieves transactions that occurred within a specific time range.
     * @param startDate Start timestamp in milliseconds.
     * @param endDate End timestamp in milliseconds.
     */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByPeriod(startDate: Long, endDate: Long): Flow<List<Transaction>>

    /**
     * Inserts a new transaction into the database.
     */
    @Insert
    suspend fun insert(transaction: Transaction)

    /**
     * Deletes an existing transaction from the database.
     */
    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * Calculates the grand total of all income entries.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isIncome = 1")
    fun getTotalIncome(): Flow<Double?>

    /**
     * Calculates the grand total of all expense entries.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isIncome = 0")
    fun getTotalExpenses(): Flow<Double?>

    /**
     * Groups transactions by category and sums their amounts for a specific period and type.
     * Useful for building summary reports or pie charts.
     * 
     * @param startDate Start timestamp.
     * @param endDate End timestamp.
     * @param isIncome Filter for income (true) or expenses (false).
     */
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE date BETWEEN :startDate AND :endDate AND isIncome = :isIncome GROUP BY category")
    fun getCategoryTotalsByPeriod(startDate: Long, endDate: Long, isIncome: Boolean): Flow<List<CategoryTotal>>
}
