package com.mydictionary.data.userrepo

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mydictionary.data.R
import com.mydictionary.domain.AuthorizationException
import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
@Inject constructor(val context: Context,
                    val firebaseAuth: FirebaseAuth,
                    val mapper: UserMapper) : UserRepository {

    private val TAG = UserRepositoryImpl::class.java.simpleName

    override fun getUser(): Single<User> = Single
            .create<FirebaseUser> { emitter ->
                firebaseAuth.currentUser?.let { emitter.onSuccess(it) }
                        ?: emitter.onError(AuthorizationException())
            }
            .map { mapper.mapFromFirebaseUserToUser(it) }

    override fun signIn(token: String) = Single.create<FirebaseUser> { emitter ->
        val credential = GoogleAuthProvider.getCredential(token, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.user != null) {
                        emitter.onSuccess(task.result.user!!)
                    } else {
                        Log.e(TAG, "firebaseAuthWithGoogle:failure", task.getException())
                        emitter.onError(
                                task.exception
                                        ?: Exception(context.getString(R.string.login_error))
                        )
                    }
                }
    }
            .map { mapper.mapFromFirebaseUserToUser(it) }


    override fun signOut(): Completable = Completable.create { emitter ->
        firebaseAuth.signOut()
        emitter.onComplete()
    }

}