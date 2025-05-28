package com.flowmate.repository

import com.flowmate.data.UserDao
import com.flowmate.data.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val userDao: UserDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun registerUser(
        name: String,
        email: String,
        username: String,
        password: String
    ): Result<UserEntity> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("UID alınamadı"))

            val user = UserEntity(
                userId = userId,
                username = username,
                email = email,
                themePreference = "light",
                createdAt = System.currentTimeMillis(),
                // password alanı çıkarıldı
            )

            firestore.collection("users").document(userId).set(user).await()
            userDao.insertUser(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("UID boş"))

            val snapshot = firestore.collection("users").document(userId).get().await()
            val userMap = snapshot.data ?: return Result.failure(Exception("Firestore'da kullanıcı bulunamadı"))

            val userEntity = UserEntity(
                userId = userId,
                username = userMap["username"] as String,
                email = userMap["email"] as String,
                themePreference = userMap["themePreference"] as String,
                createdAt = userMap["createdAt"] as Long,
                // password yok!
            )

            userDao.insertUser(userEntity)

            Result.success(userEntity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun signOut() {
        auth.signOut()
    }
}
