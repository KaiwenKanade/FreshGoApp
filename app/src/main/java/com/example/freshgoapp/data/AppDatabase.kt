package com.example.freshgoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InventoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao

    @Database(entities = [InventoryItem::class, User::class], version = 2, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun inventoryDao(): InventoryDao
        abstract fun userDao(): UserDao
    }
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database" // Nama file database
                )
                    .fallbackToDestructiveMigration() // Tambahkan ini agar tidak crash jika skema berubah
                    .build()
                Instance = instance
                instance
            }
        }
    }
}