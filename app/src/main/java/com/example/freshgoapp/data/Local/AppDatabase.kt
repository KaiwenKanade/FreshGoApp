package com.example.freshgoapp.data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Hanya ada SATU deklarasi @Database yang merangkum semua tabel (Entities)
@Database(entities = [InventoryItem::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Deklarasi DAO yang terhubung ke database ini
    abstract fun inventoryDao(): InventoryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database" // Nama file database lokal kamu
                )
                    // Sangat berguna untuk mencegah crash saat kamu mengubah struktur tabel
                    .fallbackToDestructiveMigration()
                    .build()
                Instance = instance
                instance
            }
        }
    }
}