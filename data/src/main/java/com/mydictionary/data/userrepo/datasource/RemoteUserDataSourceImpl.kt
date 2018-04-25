package com.mydictionary.data.userrepo.datasource

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mydictionary.data.await
import com.mydictionary.domain.AuthorizationException
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.User

class RemoteUserDataSourceImpl(
    val context: Context,
    val firebaseAuth: FirebaseAuth,
    val mapper: UserMapper
) : UserDataSource {

    override suspend fun getUser(): Result<User> {
        return firebaseAuth.currentUser?.let {
            val user = mapper.mapFromFirebaseUserToUser(it)
            Result.Success(user)
        } ?: Result.Error(AuthorizationException())
    }

    override suspend fun signIn(token: String): Result<User> {
        val credential = GoogleAuthProvider.getCredential(token, null)
        return try {
            val taskResult = firebaseAuth.signInWithCredential(credential).await()
            val user = mapper.mapFromFirebaseUserToUser(taskResult.user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}