package com.example.freshgoapp.data.Local

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

    // Mengambil data profil yang login
    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentUser(): User?
    //password baru
    @Query("UPDATE users SET pin = :newPin WHERE email = :email")
    suspend fun updatePassword(email: String, newPin: String): Int
}