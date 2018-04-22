package com.mydictionary.data.userrepo

import com.google.firebase.auth.FirebaseUser
import com.mydictionary.domain.entity.User

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
object UserMapper {
    fun mapFromFirebaseUserToUser(firebaseUser: FirebaseUser) =
            User(firebaseUser.uid, firebaseUser.email ?: "")
}