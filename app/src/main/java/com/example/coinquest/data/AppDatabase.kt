package com.example.coinquest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The main Room database for the application.
 * Defines entities and provides access to DAOs.
 */
@Database(entities = [Transaction::class, User::class, Goal::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    /** Accessor for Transaction-related database operations. */
    abstract fun transactionDao(): TransactionDao
    
    /** Accessor for User-related database operations (Login/Registration). */
    abstract fun userDao(): UserDao
    
    /** Accessor for Goal-related database operations (Income/Expense targets). */
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton method to get the database instance.
         * Ensures only one instance of the database exists to save resources.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coinquest_database"
                )
                // Note: destructive migration is used here for development ease.
                // In production, proper migrations should be defined.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
