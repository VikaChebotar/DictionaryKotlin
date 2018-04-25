package com.mydictionary.data.userrepo

import com.mydictionary.data.userrepo.datasource.UserDataSource
import com.mydictionary.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
@Inject constructor(private val remoteDataSource: UserDataSource) : UserRepository {

    override suspend fun getUser() = remoteDataSource.getUser()

    override suspend fun signIn(token: String) = remoteDataSource.signIn(token)

    override suspend fun signOut() = remoteDataSource.signOut()
}