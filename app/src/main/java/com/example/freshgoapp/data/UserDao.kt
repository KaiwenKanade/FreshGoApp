package com.example.freshgoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    // Untuk verifikasi login
    @Query("SELECT * FROM users WHERE email = :email AND pin = :pin LIMIT 1")
    suspend fun login(email: String, pin: String): User?

    // Mengambil data profil yang sedang login
    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentUser(): User?
}