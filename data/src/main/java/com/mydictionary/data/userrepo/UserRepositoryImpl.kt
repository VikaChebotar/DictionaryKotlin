package com.mydictionary.data.userrepo

import com.mydictionary.data.userrepo.datasource.UserDataSource
import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
@Inject constructor(private val remoteDataSource: UserDataSource) : UserRepository {

    override fun getUser(): Single<User> = remoteDataSource.getUser()

    override fun signIn(token: String) = remoteDataSource.signIn(token)

    override fun signOut(): Completable = remoteDataSource.signOut()

}