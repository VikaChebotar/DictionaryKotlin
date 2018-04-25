package com.mydictionary.data.userrepo.datasource

import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.User

interface UserDataSource {
    suspend fun getUser(): Result<User>
    suspend fun signIn(token: String): Result<User>
    suspend fun signOut()
}