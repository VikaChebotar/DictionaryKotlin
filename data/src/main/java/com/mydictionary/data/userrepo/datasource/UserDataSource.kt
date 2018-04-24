package com.mydictionary.data.userrepo.datasource

import com.mydictionary.domain.entity.User
import io.reactivex.Completable
import io.reactivex.Single

interface UserDataSource {
    fun getUser(): Single<User>
    fun signIn(token: String): Single<User>
    fun signOut(): Completable
}