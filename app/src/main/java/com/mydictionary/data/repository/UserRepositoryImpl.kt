package com.mydictionary.data.repository

import com.mydictionary.data.firebasestorage.InternalFirebaseStorage
import com.mydictionary.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(val firebaseStorage: InternalFirebaseStorage): UserRepository{

    override fun getUser() = firebaseStorage.getUser()

    override fun signIn(token: String) = firebaseStorage.loginFirebaseUser(token)

    override fun signOut() = firebaseStorage.logoutFirebaseUser()

}